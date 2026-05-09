package com.example.myexpense

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

        val session = SessionManager(this)

        // Navigate after a 2-second delay
        Handler(Looper.getMainLooper()).postDelayed({
            if (session.isLoggedIn()) {
                startActivity(Intent(this, DashboardActivity::class.java))
            } else {
                startActivity(Intent(this, WelcomeActivity::class.java))
            }
            finish()
        }, 2000)
    }
}