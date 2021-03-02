package com.example.coolerthanyou.dagger

import androidx.lifecycle.ViewModel
import com.example.coolerthanyou.ui.MainViewModel
import com.example.coolerthanyou.ui.control.ControlViewModel
import com.example.coolerthanyou.ui.details.DetailsViewModel
import com.example.coolerthanyou.ui.home.HomeViewModel
import com.example.coolerthanyou.ui.slideshow.SlideshowViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Main [Module] for [MainActivity] and [MainComponent]
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

    /**
     * Bind the [HomeViewModel] as a "value" to be used in the [Map] by [ViewModelFactory]
     */
    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun bindHomeViewModel(homeViewModel: HomeViewModel): ViewModel

    /**
     * Bind the [ControlViewModel] as a "value" to be used in the [Map] by [ViewModelFactory]
     */
    @Binds
    @IntoMap
    @ViewModelKey(ControlViewModel::class)
    abstract fun bindControlViewModel(controlViewModel: ControlViewModel): ViewModel

    /**
     * Bind the [DetailsViewModel] as a "value" to be used in the [Map] by [ViewModelFactory]
     */
    @Binds
    @IntoMap
    @ViewModelKey(DetailsViewModel::class)
    abstract fun bindDetailsViewModel(detailsViewModel: DetailsViewModel): ViewModel

    /**
     * Bind the [SlideshowViewModel] as a "value" to be used in the [Map] by [ViewModelFactory]
     */
    @Binds
    @IntoMap
    @ViewModelKey(SlideshowViewModel::class)
    abstract fun bindSlideshowViewModel(slideshowViewModel: SlideshowViewModel): ViewModel
}