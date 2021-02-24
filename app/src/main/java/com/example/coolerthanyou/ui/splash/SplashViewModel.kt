package com.example.coolerthanyou.ui.splash

import com.example.coolerthanyou.BaseViewModel
import javax.inject.Inject

/**
 * View model for [SplashViewModel]
 */
class SplashViewModel @Inject constructor() : BaseViewModel() {
    private val tasks: MutableSet<String> = mutableSetOf()

    /**
     * Add an ongoing task to track
     *
     * @param id    ID of the ongoing task
     */
    internal fun addTask(id: String) {
        tasks.add(id)
    }

    /**
     * Remove a finished task
     *
     * @param id    ID of the finished task
     */
    internal fun removeTask(id: String) {
        tasks.remove(id)
    }

    /**
     * Check whether the activity can be finished
     *
     * @return  True if ready, false otherwise
     */
    internal fun isFinished(): Boolean = tasks.isEmpty()
}