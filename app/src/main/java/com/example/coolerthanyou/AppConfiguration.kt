package com.example.coolerthanyou

/**
 * Data class for the app configuration
 *
 * @property isDebug    True if in DEBUG mode. False if not.
 */
data class AppConfiguration(val isDebug: Boolean) {

    // startup mocks
    val runStartupMocks: Boolean = true //master key
    val runStartupClearDatabase = true
    val runStartupAddFreezers: Boolean = true
    val runStartupAddRecords: Boolean = true
    val runStartupAddAlerts: Boolean = true
}