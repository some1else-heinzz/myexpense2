package com.example.myexpense

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        db = DatabaseHelper(this)
        session = SessionManager(this)

        val etUsername = findViewById<EditText>(R.id.et_username)
        val etPassword = findViewById<EditText>(R.id.et_password)
        val btnLogin = findViewById<Button>(R.id.btn_login)
        val tvSignupLink = findViewById<TextView>(R.id.tv_signup_link)

        btnLogin.setOnClickListener {
            if (validateInputs(etUsername, etPassword)) {
                val username = etUsername.text.toString().trim()
                val password = etPassword.text.toString()

                val user = db.checkUser(username, password)
                if (user != null) {
                    session.createSession(user)
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, DashboardActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
                }
            }
        }

        tvSignupLink.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }
    }

    private fun validateInputs(etUsername: EditText, etPassword: EditText): Boolean {
        val username = etUsername.text.toString().trim()
        val password = etPassword.text.toString()

        var isValid = true

        if (username.isEmpty()) {
            etUsername.error = getString(R.string.error_field_required)
            isValid = false
        }

        if (password.isEmpty()) {
            etPassword.error = getString(R.string.error_field_required)
            isValid = false
        }

        return isValid
    }
}