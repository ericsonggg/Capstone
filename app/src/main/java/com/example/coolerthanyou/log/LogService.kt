package com.example.coolerthanyou.log

import android.content.Context
import android.content.Intent
import android.os.*
import com.example.coolerthanyou.BaseApplication
import com.example.coolerthanyou.BaseService
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.OutputStreamWriter
import java.util.*
import kotlin.math.max

/**
 * Android long-life [Service] to write log messages to a file.
 * Should only be created if mode is RELEASE.
 *
 * TODO: change file append number to preference
 */
class LogService : BaseService() {
    private val logTag: String = "LogService"
    private val threadName: String = "LogServiceHandlerThread"

    private lateinit var serviceLooper: Looper
    private lateinit var handler: LogServiceHandler
    private val binder: LogServiceBinder = LogServiceBinder()

    private val logQueue: Queue<String> = LinkedList()  //queue usage does not need a thread safe implementation
    private var clockStarted: Boolean = false

    companion object {
        const val MESSAGE_TYPE_RESCHEDULE = 1
        const val MESSAGE_TYPE_FLUSH = 0
    }

    /**
     * Local [Binder] implementation for [LogService]
     * Provides a reference to the parent service.
     */
    inner class LogServiceBinder : Binder() {
        /**
         * Get the parent [LogService] of this binder
         *
         * @return  parent [LogService]
         */
        fun getService(): LogService = this@LogService
    }

    /**
     * Specific [Handler] for [LogService]
     * Process [logQueue] entries into a log file, with [maxFileSize] MB limits per file.
     *
     * @constructor
     *
     * @param looper    Looper for the thread that it is handling
     */
    private inner class LogServiceHandler(looper: Looper) : Handler(looper) {
        private val logFileName: String = "Log_"
        private val logFileExtension: String = ".txt"
        private val maxFileSize: Int = 800 //in MB
        private val filePath: String = filesDir.path
        private val loopDelay: Long = 600 //in seconds

        private var fileAppend: Int = 0

        /**
         * For any message received, write the queue to disk.
         */
        override fun handleMessage(msg: Message) {
            val queueSize = logQueue.size   // snapshot size since queue is not threadsafe

            // skip processing if there are no logs to write, and if a proper log file was found
            if (queueSize > 0 && findLogFile()) {
                try {
                    val outStream = OutputStreamWriter(openFileOutput(getFileName(), Context.MODE_APPEND))
                    for (i in 1..queueSize) {
                        outStream.write(logQueue.remove())
                    }
                    outStream.close()
                } catch (e: IOException) {
                    logger.e(logTag, "Failed to write log to file", e)
                }
            }

            // Kill the thread for flush messages. Send delayed message for reschedule messages.
            when (msg.what) {
                MESSAGE_TYPE_FLUSH -> looper.quit()
                MESSAGE_TYPE_RESCHEDULE -> {
                    handler.sendMessageDelayed(handler.obtainMessage().also {
                        it.what = MESSAGE_TYPE_RESCHEDULE
                    }, loopDelay * 1000)
                }
            }
        }

        /**
         * Generate the filename based on the current [fileAppend] value
         *
         * @return  A string file name (no path)
         */
        private fun getFileName(): String = logFileName + fileAppend + logFileExtension

        /**
         * Find the most recent log file to append to, with Log_0.txt being the oldest.
         * If the most recent log file's size is greater than [maxFileSize], then make a new file.
         */
        private fun findLogFile(): Boolean {
            var file: File? = null

            // Find last log file
            while (true) {
                try {
                    file = File(filePath, getFileName())
                    // increment fileAppend if current file is too big
                    fileAppend++
                } catch (e: FileNotFoundException) {
                    // File not found, so this append value is the most recent one.
                    fileAppend = max(0, fileAppend - 1)
                    break
                } catch (e: IOException) {
                    logger.e(logTag, "Failed to find log file", e)
                    return false // try not to repeat the failure
                }
            }

            // Check if the previously existing file is over the size limit
            if (file != null && (file.length() / 1000000) > maxFileSize) {
                fileAppend++
            }
            return true
        }
    }

    /**
     * Queue a log message to be saved to file.
     *
     * @param message   Log message to be saved
     */
    fun saveLog(message: String) {
        logQueue.add(message)
    }

    override fun onCreate() {
        //Inject dependencies
        (applicationContext as BaseApplication).appComponent.inject(this)
        super.onCreate()

        logger.d(logTag, "started onCreate")
        HandlerThread(threadName, Process.THREAD_PRIORITY_BACKGROUND).apply {
            start()

            serviceLooper = looper
            handler = LogServiceHandler(looper)
        }
        logger.d(logTag, "completed onCreate")
    }

    override fun onBind(p0: Intent): IBinder {
        logger.d(logTag, "started onBind")
        if (!clockStarted) {
            handler.sendMessage(handler.obtainMessage().also {
                it.what = MESSAGE_TYPE_RESCHEDULE
            })
            clockStarted = true
        }
        logger.d(logTag, "completed onBind")
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        logger.i(logTag, "called onUnbind")
        // Force a log flush
        handler.sendMessage(handler.obtainMessage().also {
            it.what = MESSAGE_TYPE_FLUSH
        })
        return false    // do not call onRebind
    }
}