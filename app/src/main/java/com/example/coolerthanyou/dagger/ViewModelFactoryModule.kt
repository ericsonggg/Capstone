package com.example.coolerthanyou.dagger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module

/**
 * Dagger module specifically for creating new [ViewModel]s. Works in conjunction with [ViewModelFactory].
 *
 * From (https://medium.com/@marco_cattaneo/android-viewmodel-and-factoryprovider-good-way-to-manage-it-with-dagger-2-d9e20a07084c)
 */
@Module
abstract class ViewModelFactoryModule {

    /**
     * Behind-the-scenes Dagger method to bind the [ViewModelFactory]
     */
    @Binds
    abstract fun bindViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory
}