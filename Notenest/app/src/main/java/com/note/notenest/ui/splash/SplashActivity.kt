package com.note.notenest.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.note.notenest.R
import com.note.notenest.databinding.ActivitySplashBinding
import com.note.notenest.ui.main.MainActivity
import com.note.notenest.utils.CoroutinesHelper
import com.note.notenest.utils.setFullScreen
import com.note.notenest.utils.setFullScreenForNotch
import com.note.notenest.utils.transitionAnimationBundle

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_splash)




        val dataBinding: ActivitySplashBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_splash)


        CoroutinesHelper.mainWithDelay(1000) {
            Intent(this, MainActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                this.startActivity(it, transitionAnimationBundle())
            }

        }


    }

    override fun onStart() {
        super.onStart()
        setFullScreen()
        setFullScreenForNotch()
    }
}