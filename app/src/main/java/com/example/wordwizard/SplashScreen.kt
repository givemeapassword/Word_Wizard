package com.example.wordwizard

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager


@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(null)

        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        setContentView(R.layout.activity_splash_screen)

        findViewById<View>(android.R.id.content).postDelayed({
            startActivity(
                Intent(this@SplashScreen,MainActivity::class.java).
                setAction("Start")
            )
            finish()
        },1000)
    }
}