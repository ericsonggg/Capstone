package com.example.coolerthanyou

import androidx.appcompat.app.AppCompatActivity
import com.example.coolerthanyou.dagger.ViewModelFactory
import com.example.coolerthanyou.log.ILogger
import javax.inject.Inject

/**
 * Base [AppCompatActivity] for all activities in this project.
 */
abstract class BaseActivity : AppCompatActivity() {

    @Inject
    protected lateinit var logger: ILogger

    @Inject
    internal lateinit var viewModelFactory: ViewModelFactory
}