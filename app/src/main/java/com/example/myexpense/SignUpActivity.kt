package com.example.myexpense

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val etFullName = findViewById<EditText>(R.id.et_full_name)
        val etEmail = findViewById<EditText>(R.id.et_email)
        val etPassword = findViewById<EditText>(R.id.et_password)
        val etConfirmPassword = findViewById<EditText>(R.id.et_confirm_password)
        val btnCreateAccount = findViewById<Button>(R.id.btn_create_account)
        val tvLoginLink = findViewById<TextView>(R.id.tv_login_link)

        btnCreateAccount.setOnClickListener {
            if (validateInputs(etFullName, etEmail, etPassword, etConfirmPassword)) {
                // If validation passes, proceed to log in
                Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

        tvLoginLink?.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun validateInputs(
        etFullName: EditText,
        etEmail: EditText,
        etPassword: EditText,
        etConfirmPassword: EditText
    ): Boolean {
        val fullName = etFullName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()
        val confirmPassword = etConfirmPassword.text.toString()

        var isValid = true

        if (fullName.isEmpty()) {
            etFullName.error = getString(R.string.error_field_required)
            isValid = false
        }

        if (email.isEmpty()) {
            etEmail.error = getString(R.string.error_field_required)
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = getString(R.string.error_invalid_email)
            isValid = false
        }

        if (password.isEmpty()) {
            etPassword.error = getString(R.string.error_field_required)
            isValid = false
        } else if (password.length < 6) {
            etPassword.error = getString(R.string.error_password_too_short)
            isValid = false
        }

        if (confirmPassword.isEmpty()) {
            etConfirmPassword.error = getString(R.string.error_field_required)
            isValid = false
        } else if (password != confirmPassword) {
            etConfirmPassword.error = getString(R.string.error_passwords_dont_match)
            isValid = false
        }

        return isValid
    }
}
