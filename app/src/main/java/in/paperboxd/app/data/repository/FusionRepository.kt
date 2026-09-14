package `in`.paperboxd.app.data.repository

import `in`.paperboxd.app.data.remote.ApiError
import `in`.paperboxd.app.data.remote.ApiService
import `in`.paperboxd.app.data.remote.safeApiCall
import `in`.paperboxd.app.domain.model.FusionInvite
import `in`.paperboxd.app.domain.model.FusionInvitePreview
import `in`.paperboxd.app.domain.model.FusionList
import `in`.paperboxd.app.domain.model.FusionStory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FusionRepository @Inject constructor(
    private val api: ApiService
) {
    suspend fun list(): Result<FusionList> = safeApiCall { api.fusions() }

    /** Returns the live link, making one if needed. */
    suspend fun createInvite(): Result<FusionInvite> = safeApiCall { api.createFusionInvite() }

    suspend fun cancelInvite(token: String): Result<Unit> = safeApiCall {
        val r = api.cancelFusionInvite(token)
        if (!r.isSuccessful) throw ApiError.fromResponse(r.code(), r.errorBody()?.string())
    }

    suspend fun preview(token: String): Result<FusionInvitePreview> = safeApiCall { api.fusionInvite(token) }

    /** The invitee's one tap on Fuse. Returns the Fusion id. */
    suspend fun accept(token: String): Result<String> = safeApiCall { api.acceptFusionInvite(token).fusionId }

    /** The first open builds the story server-side and can take a few seconds. */
    suspend fun story(id: String): Result<FusionStory> = safeApiCall { api.fusion(id) }
}
