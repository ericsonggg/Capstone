package com.example.coolerthanyou.log

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import java.util.*

/**
 * Logger implementation that prints logs to a file.
 * Should be used in RELEASE mode.
 *
 * TODO: Add log scoping (i.e. ignore verbose, etc)
 */
class FileLogger(applicationContext: Context) : ILogger {

    private var isBound = false;
    private lateinit var logService: LogService
    private val tempQueue: Queue<String> = LinkedList()

    private val serviceConn = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            logService = (p1 as LogService.LogServiceBinder).getService()

            // Expunge all stored logs to the service
            while (!tempQueue.isEmpty()) {
                logService.saveLog(tempQueue.remove())
            }

            isBound = true;
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            isBound = false;
        }
    }

    init {
        // Bind the LogService when FileLogger is created
        Intent(applicationContext, LogService::class.java).also { intent ->
            applicationContext.bindService(intent, serviceConn, Context.BIND_AUTO_CREATE)
        }
    }

    override fun v(tag: String, message: String) {
        tryToWriteLog(buildLogString("V", tag, message, null))
    }

    override fun d(tag: String, message: String) {
        tryToWriteLog(buildLogString("D", tag, message, null))
    }

    override fun i(tag: String, message: String) {
        tryToWriteLog(buildLogString("I", tag, message, null))
    }

    override fun w(tag: String, message: String) {
        tryToWriteLog(buildLogString("W", tag, message, null))
    }

    override fun w(tag: String, message: String, exception: Exception) {
        tryToWriteLog(buildLogString("w", tag, message, exception))
    }

    override fun e(tag: String, message: String) {
        tryToWriteLog(buildLogString("E", tag, message, null))
    }

    override fun e(tag: String, message: String, exception: Exception) {
        tryToWriteLog(buildLogString("E", tag, message, exception))
    }

    override fun wtf(tag: String, message: String) {
        tryToWriteLog(buildLogString("WTF", tag, message, null))
    }

    /**
     * Helper method to build the log string.
     *
     * @param level     The log level (i.e. d, i, w, e)
     * @param tag       The tag
     * @param message   The log message
     * @param exception A nullable exception
     * @return          A formatted string representing the whole log message
     */
    private fun buildLogString(level: String, tag: String, message: String, exception: Exception?): String {
        return StringBuilder().apply {
            append(Calendar.getInstance().time)
            append(" ")
            append(level)
            append("/")
            append(tag)
            append(": ")
            append(message)
            if (exception != null) {
                append("\n Exception: ")
                append(exception.toString())
            }
            append("\n")
        }.toString()
    }

    /**
     * If the [LogService] is bound, then send the log string there.
     * If not, temporarily save it in [tempQueue]
     *
     * @param log   The log message
     */
    private fun tryToWriteLog(log: String) {
        if (isBound) {
            logService.saveLog(log)
        } else {
            tempQueue.add(log)
        }
    }
}