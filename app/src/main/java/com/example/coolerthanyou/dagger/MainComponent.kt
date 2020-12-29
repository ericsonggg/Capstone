package com.example.coolerthanyou.dagger

import com.example.coolerthanyou.ui.MainActivity
import com.example.coolerthanyou.ui.control.ControlFragment
import com.example.coolerthanyou.ui.gallery.GalleryFragment
import com.example.coolerthanyou.ui.home.HomeFragment
import com.example.coolerthanyou.ui.slideshow.SlideshowFragment
import dagger.Subcomponent

/**
 * Dagger [Subcomponent] for all pieces rooted at the [MainActivity]
 */
@Subcomponent(modules = [MainModule::class])
interface MainComponent {

    /**
     * [Subcomponent] factory required for Dagger parent component.
     */
    @Subcomponent.Factory
    interface Factory {
        fun create(): MainComponent
    }

    /** Activities **/
    fun inject(activity: MainActivity)

    /** Fragments **/
    fun inject(fragment: HomeFragment)
    fun inject(fragment: ControlFragment)
    fun inject(fragment: GalleryFragment)
    fun inject(fragment: SlideshowFragment)
}