package com.example.coolerthanyou.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.example.coolerthanyou.BaseActivity
import com.example.coolerthanyou.BaseApplication
import com.example.coolerthanyou.ui.MainActivity

/**
 * First activity to load when app is started.
 */
class SplashActivity : BaseActivity() {

    private val _splashViewModel: SplashViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Inject dependencies before init
        (applicationContext as BaseApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)

        //TODO
//        setContentView(R.layout.activity_splash)

        startActivity(Intent(this, MainActivity::class.java))
    }
}