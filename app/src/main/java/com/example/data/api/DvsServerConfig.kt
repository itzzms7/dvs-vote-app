package com.example.data.api

/**
 * Single source of truth for the Django backend server URL.
 * You can provide the server URL here in chat, and it will be updated here.
 * Defaults to localhost / Android emulator host (10.0.2.2:8000) for standard development.
 */
object DvsServerConfig {
    /**
     * Set to true if reviewer/developer testing shortcuts and failure triggers are needed on the UI.
     * Defaulted to false so the app displays a clean, real-world interface.
     */
    const val SHOW_DEV_TEST_ACTIONS: Boolean = false

    /**
     * Configurable Django server base URL.
     * Hardcoded here and easily updated when the server address is provided.
     */
    var serverBaseUrl: String = "http://10.129.93.18:8000"
        private set

    fun setServerUrl(url: String) {
        serverBaseUrl = url.trimEnd('/')
    }
}
