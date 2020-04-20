package com.example.coolerthanyou

import android.app.Application
import com.example.coolerthanyou.dagger.ApplicationComponent
import com.example.coolerthanyou.dagger.DaggerApplicationComponent

/**
 * Base [Application] for this app.
 * Creates a new Dagger instance.
 */
class BaseApplication : Application() {
f
    internal val appComponent: ApplicationComponent = DaggerApplicationComponent.create()
}