package com.example.coolerthanyou.dagger

import com.example.coolerthanyou.ui.MainActivity
import dagger.Component

/**
 * Dagger [Component] for the whole application.
 * This component should have injectors for all activities, and subcomponents.
 */
@Component
interface ApplicationComponent {

    fun inject(activity: MainActivity)
}