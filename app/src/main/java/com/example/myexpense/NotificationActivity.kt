package com.example.myexpense

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class NotificationActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var session: SessionManager
    private lateinit var rvNotifications: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        db = DatabaseHelper(this)
        session = SessionManager(this)

        rvNotifications = findViewById(R.id.rv_notifications)
        rvNotifications.layoutManager = LinearLayoutManager(this)

        findViewById<ImageView>(R.id.iv_back).setOnClickListener {
            finish()
        }

        findViewById<TextView>(R.id.tv_clear_all).setOnClickListener {
            val userId = session.getUserId()
            if (db.clearNotifications(userId)) {
                loadNotifications()
                Toast.makeText(this, "Notifications cleared", Toast.LENGTH_SHORT).show()
            }
        }

        loadNotifications()
    }

    private fun loadNotifications() {
        val userId = session.getUserId()
        val notifications = db.getNotifications(userId)
        rvNotifications.adapter = NotificationAdapter(notifications)
    }
}