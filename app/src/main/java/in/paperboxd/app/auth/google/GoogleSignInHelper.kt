package `in`.paperboxd.app.auth.google

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import `in`.paperboxd.app.config.Config
import javax.inject.Inject
import javax.inject.Singleton

/** Outcome of a Credential Manager Google Sign-In attempt. */
sealed interface GoogleSignInResult {
    /** Got a Google ID token — hand to the backend for verification. */
    data class Success(val idToken: String) : GoogleSignInResult
    /** User dismissed the picker. Caller should stay silent (matches iOS). */
    data object Cancelled : GoogleSignInResult
    /** Real failure (no Play Services, offline, misconfigured client). Surface it. */
    data class Failure(val message: String) : GoogleSignInResult
}

/**
 * Front half of Google Sign-In: drives the Credential Manager picker and returns
 * a Google ID token. The back half (exchanging that token for a session) already
 * lives in AuthRepository.googleAuth — this class does not touch the network or
 * token storage. Mirrors iOS GoogleOAuth: cancellation is a silent no-op, only a
 * genuine failure surfaces an error.
 */
@Singleton
class GoogleSignInHelper @Inject constructor() {

    /**
     * Launches the Google account picker and returns the resulting ID token.
     * [activityContext] must be an Activity context so the picker UI can show.
     */
    suspend fun getIdToken(activityContext: Context): GoogleSignInResult {
        if (Config.GOOGLE_WEB_CLIENT_ID.isEmpty()) {
            return GoogleSignInResult.Failure("Google sign-in is not configured.")
        }

        val option = GetSignInWithGoogleOption.Builder(Config.GOOGLE_WEB_CLIENT_ID).build()
        val request = GetCredentialRequest.Builder().addCredentialOption(option).build()

        return try {
            val response = CredentialManager.create(activityContext)
                .getCredential(activityContext, request)
            val credential = response.credential
            if (credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
                GoogleSignInResult.Success(googleCredential.idToken)
            } else {
                GoogleSignInResult.Failure("Unexpected credential type from Google.")
            }
        } catch (e: GetCredentialCancellationException) {
            GoogleSignInResult.Cancelled
        } catch (e: GetCredentialException) {
            GoogleSignInResult.Failure(e.message ?: "Google sign-in failed. Please try again.")
        }
    }
}
