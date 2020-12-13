package com.example.coolerthanyou.log

/**
 * Wrapper interface for Log.
 * In Debug mode, should print to Android Logcat
 * In Release mode, should print to file
 */
interface ILogger {

    /**
     * Verbose messages, use for debugging when you need to track every step
     *
     * @param tag       The tag (i.e. caller)
     * @param message   The messages
     */
    fun v(tag: String, message: String)

    /**
     * Debug messages, use as needed for debugging
     *
     * @param tag       The tag (i.e. caller)
     * @param message   The messages
     */
    fun d(tag: String, message: String)

    /**
     * Informative messages, use to communicate app state or flow
     * Also use for indication of success (i.e. server connection)
     *
     * @param tag       The tag (i.e. caller)
     * @param message   The messages
     */
    fun i(tag: String, message: String)

    /**
     * Warning messages, use to indicate possibility of failure or averted failures
     *
     * @param message   The messages
     */
    fun w(tag: String, message: String)

    /**
     * Error messages, use to indicate occurred failure and reason for error
     *
     * @param tag       The tag (i.e. caller)
     * @param message   The messages
     */
    fun e(tag: String, message: String)

    /**
     * WTF messages, use to indicate an error that should have never happened
     *
     * @param tag       The tag (i.e. caller)
     * @param message
     */
    fun wtf(tag: String, message: String)
}