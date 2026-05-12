package com.example.myexpense

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SignUpActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        db = DatabaseHelper(this)

        val etFullName = findViewById<EditText>(R.id.et_full_name)
        val etEmail = findViewById<EditText>(R.id.et_email) // Note: using email as username
        val etPassword = findViewById<EditText>(R.id.et_password)
        val etConfirmPassword = findViewById<EditText>(R.id.et_confirm_password)
        val btnCreateAccount = findViewById<Button>(R.id.btn_create_account)
        val tvLoginLink = findViewById<TextView>(R.id.tv_login_link)
        val ivPasswordMatch = findViewById<ImageView>(R.id.iv_password_match)

        val passwordWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val password = etPassword.text.toString()
                val confirmPassword = etConfirmPassword.text.toString()
                
                if (confirmPassword.isNotEmpty() && password == confirmPassword) {
                    ivPasswordMatch.visibility = View.VISIBLE
                } else {
                    ivPasswordMatch.visibility = View.GONE
                }
            }
        }

        etPassword.addTextChangedListener(passwordWatcher)
        etConfirmPassword.addTextChangedListener(passwordWatcher)

        btnCreateAccount.setOnClickListener {
            if (validateInputs(etFullName, etEmail, etPassword, etConfirmPassword)) {
                val fullName = etFullName.text.toString().trim()
                val email = etEmail.text.toString().trim()
                val password = etPassword.text.toString()

                val newUser = User(username = email, fullName = fullName, password = password)
                val success = db.addUser(newUser)

                if (success) {
                    Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Registration failed. Username might exist.", Toast.LENGTH_SHORT).show()
                }
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