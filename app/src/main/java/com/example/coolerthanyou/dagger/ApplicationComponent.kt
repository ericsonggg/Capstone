package com.example.coolerthanyou.dagger

import com.example.coolerthanyou.ui.MainActivity
import com.example.coolerthanyou.ui.MainViewModel
import dagger.Component

/**
 * Dagger [Component] for the whole application.
 * This component should have injectors for all activities, and subcomponents.
 */
@Component(modules = [ViewModelFactoryModule::class, MainModule::class])
interface ApplicationComponent {

    /** [MainModule] **/
    fun inject(activity: MainActivity)
    fun inject(mainViewModel: MainViewModel)
}