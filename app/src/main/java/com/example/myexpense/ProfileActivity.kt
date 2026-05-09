package com.example.myexpense

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        db = DatabaseHelper(this)
        session = SessionManager(this)

        val tvUsername = findViewById<TextView>(R.id.tv_username)
        val etFullName = findViewById<EditText>(R.id.et_full_name)
        val etPassword = findViewById<EditText>(R.id.et_password)

        // Load current user data
        tvUsername.text = session.getUsername()
        etFullName.setText(session.getFullName())
        // Password shouldn't be pre-filled for security, but we'll leave it blank or let user type new one

        findViewById<ImageView>(R.id.iv_back).setOnClickListener {
            showDiscardDialog()
        }

        findViewById<Button>(R.id.btn_save_profile).setOnClickListener {
            val newFullName = etFullName.text.toString().trim()
            val newPassword = etPassword.text.toString()

            if (newFullName.isNotEmpty() && newPassword.isNotEmpty()) {
                val updatedUser = User(
                    id = session.getUserId(),
                    username = session.getUsername() ?: "",
                    fullName = newFullName,
                    password = newPassword
                )

                if (db.updateUser(updatedUser)) {
                    // Update session data
                    session.createSession(updatedUser)
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showDiscardDialog()
            }
        })
    }

    private fun showDiscardDialog() {
        AlertDialog.Builder(this)
            .setTitle("Discard Changes?")
            .setMessage("Are you sure you want to discard your changes?")
            .setPositiveButton("Discard") { _, _ ->
                finish()
            }
            .setNegativeButton("Keep Editing", null)
            .show()
    }
}