package com.example.coolerthanyou.dagger

import com.example.coolerthanyou.ui.MainActivity
import com.example.coolerthanyou.ui.control.ControlFragment
import com.example.coolerthanyou.ui.home.HomeFragment
import dagger.Component

/**
 * Dagger [Component] for the whole application.
 * This component should have injectors for all activities, and subcomponents.
 *
 * TODO: split off injects for submodules
 */
@Component(modules = [ViewModelFactoryModule::class, MainModule::class])
interface ApplicationComponent {

    /** [MainModule] **/
    fun inject(activity: MainActivity)

    fun inject(fragment: HomeFragment)
    fun inject(fragment: ControlFragment)
}