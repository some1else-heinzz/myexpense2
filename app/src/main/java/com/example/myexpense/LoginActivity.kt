package com.example.myexpense

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etUsername = findViewById<EditText>(R.id.et_username)
        val etPassword = findViewById<EditText>(R.id.et_password)
        val btnLogin = findViewById<Button>(R.id.btn_login)
        val tvSignupLink = findViewById<TextView>(R.id.tv_signup_link)

        btnLogin.setOnClickListener {
            if (validateInputs(etUsername, etPassword)) {
                // For now, any non-empty input is "valid" login
                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, DashboardActivity::class.java))
                finish()
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
        } else if (password.length < 6) {
            etPassword.error = getString(R.string.error_password_too_short)
            isValid = false
        }

        return isValid
    }
}
