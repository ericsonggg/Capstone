package com.example.coolerthanyou

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.coolerthanyou.dagger.ApplicationComponent

/**
 * Base [AppCompatActivity] for all activities in this project.
 */
abstract class BaseActivity : AppCompatActivity() {

    /**
     * Living [ApplicationComponent] to get the Dagger [ApplicationComponent]
     */
    internal lateinit var appComponent: ApplicationComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appComponent = (getApplicationContext() as BaseApplication).appComponent
    }
}