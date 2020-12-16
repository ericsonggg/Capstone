package com.example.coolerthanyou.dagger

import android.content.Context
import com.example.coolerthanyou.AppConfiguration
import com.example.coolerthanyou.log.LogService
import com.example.coolerthanyou.ui.splash.SplashActivity
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

/**
 * Dagger [Component] for the whole application.
 * This component should have injectors for modules that are critical to all other modules i.e. Room
 */
@Singleton
@Component(modules = [ApplicationModule::class, RoomModule::class])
interface ApplicationComponent {

    /** Sub-component factory expose **/
    fun mainComponent(): MainComponent.Factory

    /** Activities **/
    fun inject(activity: SplashActivity)

    /** Services **/
    fun inject(service: LogService)

    /**
     * Custom builder interface to accept common configurations
     */
    @Component.Builder
    interface Builder {

        /**
         * Standard build method
         *
         * @return  Built component interface
         */
        fun build(): ApplicationComponent

        /**
         * Run-time specific app configuration
         *
         * @param conf  Configuration for the app at runtime
         * @return      Builder
         */
        @BindsInstance
        fun appConfiguration(conf: AppConfiguration): Builder

        /**
         * Context of the entire application.
         * Used to build Room databases in [ApplicationModule]
         *
         * @param appContext    Context for the app
         * @return              Builder
         */
        @BindsInstance
        fun appContext(appContext: Context): Builder
    }
}