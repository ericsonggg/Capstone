package com.example.coolerthanyou.dagger

import androidx.lifecycle.ViewModel
import com.example.coolerthanyou.ui.MainViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Main [Module] for the app.
 * Only super-integral classes should be binded here - otherwise, put them in a respective feature module.
 */
@Module
abstract class MainModule {

    /**
     * Bind the [MainViewModel] as a "value" to be used in the [Map] by [ViewModelFactory]
     */
    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(mainViewModel: MainViewModel): ViewModel
}