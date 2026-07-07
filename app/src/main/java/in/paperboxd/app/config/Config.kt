package `in`.paperboxd.app.config

import `in`.paperboxd.app.BuildConfig

/**
 * Centralised configuration. Mirrors iOS Config.swift: debug + release both hit
 * Railway prod. Flip [BASE_URL] to a local address for backend development.
 */
object Config {
    const val BASE_URL: String = "https://paperboxd-backend-production-d9e0.up.railway.app"

    val userAgent: String = "PaperBoxd-Android/${BuildConfig.VERSION_NAME} (Android)"

    /** EncryptedSharedPreferences file name. Stable across builds. */
    const val SECURE_PREFS_NAME: String = "in.paperboxd.app.secure"

    /**
     * Web-type OAuth client ID passed to Credential Manager as the serverClientId
     * for Google Sign-In. Sourced from local.properties via BuildConfig; must be
     * present in the backend's GOOGLE_OAUTH_ALLOWED_AUDIENCES allowlist.
     */
    const val GOOGLE_WEB_CLIENT_ID: String = BuildConfig.GOOGLE_WEB_CLIENT_ID
}
