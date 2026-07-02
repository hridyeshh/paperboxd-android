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
}
