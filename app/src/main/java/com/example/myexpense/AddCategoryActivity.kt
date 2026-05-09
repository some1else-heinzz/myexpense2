package com.example.myexpense

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class AddCategoryActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_category)

        db = DatabaseHelper(this)
        session = SessionManager(this)

        val etName = findViewById<EditText>(R.id.et_category_name)

        findViewById<ImageView>(R.id.iv_back).setOnClickListener {
            showDiscardDialog()
        }

        findViewById<Button>(R.id.btn_save_category).setOnClickListener {
            val name = etName.text.toString().trim()
            if (name.isNotEmpty()) {
                val userId = session.getUserId()
                if (db.addCategory(userId, name)) {
                    Toast.makeText(this, "Category saved", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Failed to save category", Toast.LENGTH_SHORT).show()
                }
            } else {
                etName.error = "Name cannot be empty"
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showDiscardDialog()
            }
        })
    }

    private fun showDiscardDialog() {
        if (findViewById<EditText>(R.id.et_category_name).text.isNotEmpty()) {
            AlertDialog.Builder(this)
                .setTitle("Discard Changes?")
                .setMessage("Are you sure you want to discard your changes?")
                .setPositiveButton("Discard") { _, _ ->
                    finish()
                }
                .setNegativeButton("Keep Editing", null)
                .show()
        } else {
            finish()
        }
    }
}