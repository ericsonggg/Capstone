package com.example.coolerthanyou

import androidx.appcompat.app.AppCompatActivity
import com.example.coolerthanyou.dagger.ViewModelFactory
import javax.inject.Inject

/**
 * Base [AppCompatActivity] for all activities in this project.
 */
abstract class BaseActivity : AppCompatActivity() {

    @Inject
    internal lateinit var viewModelFactory: ViewModelFactory
}