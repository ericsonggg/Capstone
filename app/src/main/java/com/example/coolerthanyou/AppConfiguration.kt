package com.example.coolerthanyou

/**
 * Data class for the app configuration
 *
 * @property isDebug    True if in DEBUG mode. False if not.
 */
data class AppConfiguration(val isDebug: Boolean) {

    // startup mocks
    val runStartupMocks: Boolean = false //master key
    val runStartupClearDatabase = true
    val runStartupAddFreezers: Boolean = false
    val runStartupNumFreezers: Int = 3
    val runStartupAddRecords: Boolean = false
    val runStartupMaxRecords: Int = 40
    val runStartupAddAlerts: Boolean = false
    val runStartupMaxAlerts: Int = 5

    // performance tests
    val runStartupTests: Boolean = true //master key
}