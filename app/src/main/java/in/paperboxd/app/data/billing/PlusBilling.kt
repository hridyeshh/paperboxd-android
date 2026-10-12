package `in`.paperboxd.app.data.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import dagger.hilt.android.qualifiers.ApplicationContext
import `in`.paperboxd.app.data.remote.ApiService
import `in`.paperboxd.app.data.remote.safeApiCall
import `in`.paperboxd.app.data.repository.AnalyticsRepository
import `in`.paperboxd.app.domain.model.SubscriptionStatus
import `in`.paperboxd.app.ui.screens.scan.ScanPrefs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

/**
 * PaperBoxd Plus — the subscription that unlocks Ask Jazy. iOS twin: `PlusStore`.
 *
 * Play Billing on the device, one row on the backend. The backend is the
 * source of truth for the entitlement (`GET /subscriptions/me`); Play only
 * supplies purchase tokens. Every purchase — new, restored, or found on
 * reconnect — goes to `POST /subscriptions/google`, which verifies and
 * acknowledges it with Google.
 *
 * One Play subscription product (`plus`) with two base plans (`monthly`,
 * `yearly`); each base plan surfaces as one [Plan].
 */
@Singleton
class PlusBilling @Inject constructor(
    @ApplicationContext private val context: Context,
    private val api: ApiService,
    private val analytics: AnalyticsRepository
) : PurchasesUpdatedListener {

    data class Plan(
        val basePlanId: String,
        val offerToken: String,
        val formattedPrice: String,
        /** Days of free trial on this offer, 0 for the plain base plan. */
        val trialDays: Int,
        val details: ProductDetails
    )

    /** Null until the first backend check answers. */
    private val _isActive = MutableStateFlow<Boolean?>(null)
    val isActive: StateFlow<Boolean?> = _isActive.asStateFlow()

    private val _plans = MutableStateFlow<List<Plan>>(emptyList())
    val plans: StateFlow<List<Plan>> = _plans.asStateFlow()

    /**
     * True once a plan query came back empty — Play not reachable, or the
     * `plus` product isn't live for this build/account. Lets the paywall show
     * "unavailable" instead of spinning forever.
     */
    private val _plansUnavailable = MutableStateFlow(false)
    val plansUnavailable: StateFlow<Boolean> = _plansUnavailable.asStateFlow()

    /** True when a purchase from the Play sheet was linked; false when it was cancelled or failed. */
    private val _purchaseResults = MutableSharedFlow<Boolean>(extraBufferCapacity = 1)
    val purchaseResults: SharedFlow<Boolean> = _purchaseResults.asSharedFlow()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val client: BillingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
        .build()

    /** Connects (idempotent), refreshes the entitlement, links any purchase Play already holds. */
    fun start() {
        scope.launch {
            refresh()
            if (connect()) restore()
        }
    }

    suspend fun refresh() {
        safeApiCall { api.subscriptionStatus() }.onSuccess(::apply)
    }

    /** The backend owns the scan allowance too; keep the local counter honest. */
    private fun apply(status: SubscriptionStatus) {
        _isActive.value = status.active
        status.scansRemaining?.let { ScanPrefs.setScansRemaining(context, it, status.scansUnlimited ?: status.active) }
            ?: ScanPrefs.setScansUnlimited(context, status.scansUnlimited ?: status.active)
    }

    fun track(event: String, feature: String, extra: Map<String, String> = emptyMap()) =
        analytics.track(event, metadata = mapOf("feature" to feature, "surface" to "app") + extra)

    suspend fun loadPlans() {
        if (_plans.value.isNotEmpty()) return
        _plansUnavailable.value = false
        if (!connect()) { _plansUnavailable.value = true; return }
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(
                listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(PRODUCT_ID)
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build()
                )
            )
            .build()
        val details = suspendCancellableCoroutine<ProductDetails?> { cont ->
            client.queryProductDetailsAsync(params) { _, result ->
                cont.resume(result.productDetailsList.firstOrNull())
            }
        } ?: run { _plansUnavailable.value = true; return }
        // One Plan per base plan. Play only returns offers this account is
        // eligible for, so when a free-trial offer is present it wins over the
        // plain base plan; the trial length comes from its zero-price phase.
        _plans.value = details.subscriptionOfferDetails.orEmpty()
            .groupBy { it.basePlanId }
            .map { (basePlanId, offers) ->
                val offer = offers.maxByOrNull { trialDays(it) } ?: offers.first()
                Plan(
                    basePlanId = basePlanId,
                    offerToken = offer.offerToken,
                    formattedPrice = offer.pricingPhases.pricingPhaseList.last().formattedPrice,
                    trialDays = trialDays(offer),
                    details = details
                )
            }
            .sortedBy { it.basePlanId != BASE_PLAN_MONTHLY }
        _plansUnavailable.value = _plans.value.isEmpty()
    }

    /** Free-trial length of an offer: its zero-price phase's ISO-8601 period (P7D, P1W). */
    private fun trialDays(offer: ProductDetails.SubscriptionOfferDetails): Int {
        val phase = offer.pricingPhases.pricingPhaseList.firstOrNull { it.priceAmountMicros == 0L } ?: return 0
        val m = Regex("P(?:(\\d+)W)?(?:(\\d+)D)?").matchEntire(phase.billingPeriod) ?: return 0
        return (m.groupValues[1].toIntOrNull() ?: 0) * 7 + (m.groupValues[2].toIntOrNull() ?: 0)
    }

    /** Opens the Play purchase sheet; the outcome arrives on [purchaseResults]. */
    fun launch(activity: Activity, plan: Plan) {
        val params = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(
                listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(plan.details)
                        .setOfferToken(plan.offerToken)
                        .build()
                )
            )
            .build()
        val result = client.launchBillingFlow(activity, params)
        if (result.responseCode != BillingClient.BillingResponseCode.OK) {
            _purchaseResults.tryEmit(false)
        }
    }

    /** "Restore purchases": re-link whatever subscription this Google account holds. */
    suspend fun restore(): Boolean {
        if (!connect()) return false
        val purchases = suspendCancellableCoroutine<List<Purchase>> { cont ->
            client.queryPurchasesAsync(
                QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build()
            ) { _, list -> cont.resume(list) }
        }
        var linked = false
        for (p in purchases) if (p.purchaseState == Purchase.PurchaseState.PURCHASED && link(p)) linked = true
        if (!linked) refresh()
        return linked
    }

    override fun onPurchasesUpdated(result: BillingResult, purchases: List<Purchase>?) {
        if (result.responseCode != BillingClient.BillingResponseCode.OK || purchases.isNullOrEmpty()) {
            _purchaseResults.tryEmit(false)
            return
        }
        scope.launch {
            var linked = false
            for (p in purchases) if (p.purchaseState == Purchase.PurchaseState.PURCHASED && link(p)) linked = true
            _purchaseResults.emit(linked)
        }
    }

    /**
     * POST the purchase token. 409 means it backs another account — surfaced as
     * false, never as a crash, since reconnect hits it routinely on a shared device.
     */
    private suspend fun link(purchase: Purchase): Boolean =
        safeApiCall { api.linkGoogleSubscription(mapOf("purchase_token" to purchase.purchaseToken)) }
            .onSuccess(::apply)
            .getOrNull()?.active == true

    private suspend fun connect(): Boolean {
        if (client.isReady) return true
        return suspendCancellableCoroutine { cont ->
            client.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(result: BillingResult) {
                    if (cont.isActive) cont.resume(result.responseCode == BillingClient.BillingResponseCode.OK)
                }
                override fun onBillingServiceDisconnected() {
                    if (cont.isActive) cont.resume(false)
                }
            })
        }
    }

    companion object {
        const val PRODUCT_ID = "plus"
        const val BASE_PLAN_MONTHLY = "monthly"
        const val BASE_PLAN_YEARLY = "yearly"
    }
}
