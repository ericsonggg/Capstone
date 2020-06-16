package com.example.coolerthanyou.dagger

import com.example.coolerthanyou.ui.MainActivity
import com.example.coolerthanyou.ui.control.ControlFragment
import com.example.coolerthanyou.ui.gallery.GalleryFragment
import com.example.coolerthanyou.ui.home.HomeFragment
import com.example.coolerthanyou.ui.slideshow.SlideshowFragment
import dagger.Component
import javax.inject.Singleton

/**
 * Dagger [Component] for the whole application.
 * This component should have injectors for all activities, and subcomponents.
 *
 * TODO: split off injects for submodules
 */
@Singleton
@Component(modules = [ViewModelFactoryModule::class, MainModule::class, RoomModule::class])
interface ApplicationComponent {

    /** [MainModule] **/
    fun inject(activity: MainActivity)

    fun inject(fragment: HomeFragment)
    fun inject(fragment: ControlFragment)
    fun inject(fragment: GalleryFragment)
    fun inject(fragment: SlideshowFragment)
}