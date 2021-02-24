package com.example.coolerthanyou.log

import android.util.Log

/**
 * Logger implementation that prints all logs to the Android logcat.
 * Should only be used in DEBUG mode
 */
class AndroidLogger : ILogger {

    override fun v(tag: String, message: String) {
        Log.v(tag, message)
    }

    override fun d(tag: String, message: String) {
        Log.d(tag, message)
    }

    override fun i(tag: String, message: String) {
        Log.i(tag, message)
    }

    override fun w(tag: String, message: String) {
        Log.w(tag, message)
    }

    override fun w(tag: String, message: String, exception: Exception) {
        Log.w(tag, message, exception)
    }

    override fun e(tag: String, message: String) {
        Log.e(tag, message)
    }

    override fun e(tag: String, message: String, exception: Exception) {
        Log.e(tag, message, exception)
    }

    override fun wtf(tag: String, message: String) {
        Log.wtf(tag, message)
    }
}