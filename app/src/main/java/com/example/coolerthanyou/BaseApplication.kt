package com.example.coolerthanyou

import android.app.Application
import com.example.coolerthanyou.dagger.ApplicationComponent
import com.example.coolerthanyou.dagger.DaggerApplicationComponent
import com.example.coolerthanyou.dagger.RoomModule

/**
 * Base [Application] for this app.
 * Creates a new Dagger instance.
 */
class BaseApplication : Application() {
    internal val appComponent: ApplicationComponent =
        DaggerApplicationComponent.builder()
            .roomModule(RoomModule(this))
            .build()
}