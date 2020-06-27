package com.example.coolerthanyou.dagger

import androidx.lifecycle.ViewModel
import com.example.coolerthanyou.ui.MainViewModel
import com.example.coolerthanyou.ui.control.ControlViewModel
import com.example.coolerthanyou.ui.gallery.GalleryViewModel
import com.example.coolerthanyou.ui.home.HomeViewModel
import com.example.coolerthanyou.ui.slideshow.SlideshowViewModel
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
     * Bind the [GalleryViewModel] as a "value" to be used in the [Map] by [ViewModelFactory]
     */
    @Binds
    @IntoMap
    @ViewModelKey(GalleryViewModel::class)
    abstract fun bindGalleryViewModel(galleryViewModel: GalleryViewModel): ViewModel

    /**
     * Bind the [SlideshowViewModel] as a "value" to be used in the [Map] by [ViewModelFactory]
     */
    @Binds
    @IntoMap
    @ViewModelKey(SlideshowViewModel::class)
    abstract fun bindSlideshowViewModel(slideshowViewModel: SlideshowViewModel): ViewModel
}