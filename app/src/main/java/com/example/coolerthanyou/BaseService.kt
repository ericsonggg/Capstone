package com.example.coolerthanyou

import android.app.Service
import com.example.coolerthanyou.log.ILogger
import javax.inject.Inject

/**
 * Base [Service] for all services in this project
 */
abstract class BaseService : Service() {

    @Inject
    protected lateinit var logger: ILogger
}