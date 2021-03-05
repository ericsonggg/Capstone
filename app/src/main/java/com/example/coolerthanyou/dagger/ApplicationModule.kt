package com.example.coolerthanyou.dagger

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.coolerthanyou.AppConfiguration
import com.example.coolerthanyou.log.AndroidLogger
import com.example.coolerthanyou.log.FileLogger
import com.example.coolerthanyou.log.ILogger
import com.example.coolerthanyou.mock.Mocker
import com.example.coolerthanyou.ui.splash.SplashViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import javax.inject.Provider
import javax.inject.Singleton

/**
 * [Module] for all parts critical to the application (except Room)
 * Includes logger classes
 * Includes [ViewModelFactory] classes (from https://medium.com/@marco_cattaneo/android-viewmodel-and-factoryprovider-good-way-to-manage-it-with-dagger-2-d9e20a07084c)
 */
@Module(subcomponents = [MainComponent::class])
abstract class ApplicationModule {

    companion object {
        /**
         * Dynamically provide a [ILogger] depending on what the configuration's debug status is.
         *
         * @return  [AndroidLogger] if in DEBUG mode, else [FileLogger] if in RELEASE mode.
         */
        @JvmStatic
        @Provides
        @Singleton
        fun provideLogger(conf: AppConfiguration, androidLogger: Provider<AndroidLogger>, fileLogger: Provider<FileLogger>): ILogger {
            return if (conf.isDebug) androidLogger.get() else fileLogger.get()
        }

        /**
         * Create new [AndroidLogger] for provisions.
         * [provideLogger] should be a singleton so this does not need to.
         */
        @JvmStatic
        @Provides
        fun bindAndroidLogger(): AndroidLogger = AndroidLogger()

        /**
         * Create new [FileLogger] for provisions.
         * [provideLogger] should be a singleton so this does not need to.
         */
        @JvmStatic
        @Provides
        fun bindFileLogger(appContext: Context): FileLogger = FileLogger(appContext)

        /**
         * Provide a mocker
         */
        @JvmStatic
        @Provides
        @Singleton
        fun provideMocker(): Mocker = Mocker()
    }

    /**
     * Bind the [ViewModelFactory] as regular [ViewModelProvider.Factory]
     */
    @Binds
    @Singleton
    abstract fun bindViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory

    /**
     * Bind the [SplashViewModel] as a "value" to be used in the [Map] by [ViewModelFactory]
     */
    @Binds
    @IntoMap
    @ViewModelKey(SplashViewModel::class)
    abstract fun bindSlideshowViewModel(splashViewModel: SplashViewModel): ViewModel
}