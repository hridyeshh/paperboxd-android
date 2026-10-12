package `in`.paperboxd.app.ui.screens.plus

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.paperboxd.app.data.billing.PlusBilling
import `in`.paperboxd.app.ui.navigation.PipFace
import `in`.paperboxd.app.ui.screens.jazy.hardShadow
import `in`.paperboxd.app.ui.screens.scan.MonoLabel
import `in`.paperboxd.app.ui.screens.scan.SK
import kotlinx.coroutines.launch

/**
 * The Plus paywall, in Jazy's own kit — iOS twin: `PlusPaywallView`. Shown
 * over the Jazy entry screen whenever the reader is not subscribed.
 * [onUnlocked] fires once the backend confirms the purchase.
 */
enum class PlusFeature(val key: String, val headline: String, val body: String) {
    Jazy(
        "jazy",
        "Ask Jazy anything about your next read.",
        "Jazy reads your shelf, your ratings and your thoughts, then talks you to the right book. Plus keeps the conversation open."
    ),
    Scan(
        "scan",
        "Scan every cover you wonder about.",
        "Free readers get 3 scans a month. Plus makes Scan & Know unlimited, and opens Ask Jazy too."
    ),
    Taste(
        "taste",
        "See the whole shape of your taste.",
        "Your taste axes, how they shift, and the patterns behind your ratings — the full mirror, on Plus."
    )
}

@Composable
fun PlusPaywallSheet(
    billing: PlusBilling,
    onUnlocked: () -> Unit,
    onDismiss: () -> Unit,
    feature: PlusFeature = PlusFeature.Jazy
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val plans by billing.plans.collectAsState()
    val plansUnavailable by billing.plansUnavailable.collectAsState()
    var selected by remember { mutableStateOf(PlusBilling.BASE_PLAN_YEARLY) }
    var message by remember { mutableStateOf<String?>(null) }
    var busy by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        billing.track("paywall_viewed", feature.key)
        billing.loadPlans()
    }
    val trialDays = plans.firstOrNull { it.basePlanId == selected }?.trialDays ?: 0
    LaunchedEffect(Unit) {
        billing.purchaseResults.collect { linked ->
            busy = false
            if (linked) onUnlocked() else message = "Purchase not completed."
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.34f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss
            )
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 28.dp)
                .padding(end = 5.dp, bottom = 5.dp)
                .hardShadow(SK.ink, 5.dp)
                .background(SK.panel)
                .border(2.dp, SK.ink)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {}
                )
                .padding(20.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                MonoLabel("PaperBoxd Plus", size = 9.5f, tracking = 2.2f, color = SK.faint)
                Spacer(Modifier.weight(1f))
                PipFace(modifier = Modifier.size(30.dp, 32.dp).offset(y = (-4).dp))
            }

            Text(
                feature.headline,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 23.sp,
                lineHeight = 28.sp,
                color = SK.ink,
                modifier = Modifier.padding(top = 10.dp)
            )

            Text(
                feature.body,
                fontSize = 13.5.sp,
                lineHeight = 20.sp,
                color = SK.sub,
                modifier = Modifier.padding(top = 12.dp)
            )

            Box(
                Modifier
                    .padding(vertical = 16.dp)
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(SK.ink.copy(alpha = 0.14f))
            )

            if (plans.isEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(96.dp)
                        .clickable(
                            enabled = plansUnavailable,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { scope.launch { billing.loadPlans() } }
                ) {
                    if (plansUnavailable) {
                        MonoLabel("Plans unavailable · Tap to retry", size = 10f, tracking = 1.6f, color = SK.accent)
                    } else {
                        CircularProgressIndicator(color = SK.ink, strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
                        MonoLabel("Loading plans", size = 10f, tracking = 1.6f, color = SK.faint)
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    plans.forEach { plan ->
                        val isOn = plan.basePlanId == selected
                        val yearly = plan.basePlanId == PlusBilling.BASE_PLAN_YEARLY
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .background(if (isOn) SK.ink else SK.bgSoft)
                                .border(if (isOn) 2.dp else 1.5.dp, SK.ink)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { selected = plan.basePlanId }
                                .padding(horizontal = 14.dp)
                        ) {
                            Column {
                                MonoLabel(
                                    if (yearly) "Yearly" else "Monthly",
                                    size = 10f, tracking = 1.6f,
                                    color = if (isOn) SK.panel else SK.ink,
                                    weight = FontWeight.SemiBold
                                )
                                if (yearly) {
                                    MonoLabel(
                                        "Two months free", size = 8.5f, tracking = 1.2f,
                                        color = if (isOn) SK.panel.copy(alpha = 0.7f) else SK.faint
                                    )
                                }
                            }
                            Spacer(Modifier.weight(1f))
                            Text(
                                "${plan.formattedPrice} / ${if (yearly) "yr" else "mo"}",
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 17.sp,
                                color = if (isOn) SK.panel else SK.ink
                            )
                        }
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(top = 20.dp, end = 4.dp, bottom = 4.dp)
                    .fillMaxWidth()
                    .hardShadow(SK.ink.copy(alpha = 0.78f))
                    .background(SK.ink)
                    .border(2.dp, SK.ink)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        enabled = !busy && plans.isNotEmpty()
                    ) {
                        val plan = plans.firstOrNull { it.basePlanId == selected } ?: return@clickable
                        val activity = context as? Activity ?: return@clickable
                        billing.track("premium_cta_clicked", feature.key, mapOf("plan" to plan.basePlanId))
                        busy = true
                        message = null
                        billing.launch(activity, plan)
                    }
                    .height(52.dp)
            ) {
                MonoLabel(
                    if (busy) "Opening Google Play" else if (trialDays > 0) "Start $trialDays-day free trial" else "Start Plus",
                    size = 12f, tracking = 1.4f, color = SK.panel, weight = FontWeight.SemiBold
                )
            }

            message?.let {
                Text(it, fontSize = 12.sp, color = SK.accent, modifier = Modifier.padding(top = 8.dp))
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.padding(top = 14.dp)
            ) {
                MonoLabel(
                    "Restore purchases", size = 9.5f, tracking = 1.2f, color = SK.faint,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        scope.launch {
                            busy = true
                            message = null
                            val ok = billing.restore()
                            busy = false
                            if (ok) onUnlocked() else message = "No Plus subscription found for this Google account."
                        }
                    }
                )
                MonoLabel(
                    "Terms", size = 9.5f, tracking = 1.2f, color = SK.faint,
                    modifier = Modifier.clickable { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://paperboxd.in/terms"))) }
                )
                MonoLabel(
                    "Privacy", size = 9.5f, tracking = 1.2f, color = SK.faint,
                    modifier = Modifier.clickable { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://paperboxd.in/privacy"))) }
                )
            }

            // Play policy: price, period and auto-renew terms stated where the
            // subscription is sold.
            Text(
                (if (trialDays > 0) "Free for $trialDays days, then " else "") +
                    "auto-renews until cancelled. Charged to your Google Play account at confirmation; " +
                    "manage or cancel in Play Store › Payments & subscriptions.",
                fontSize = 10.5.sp,
                lineHeight = 14.sp,
                color = SK.faint,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }
}
