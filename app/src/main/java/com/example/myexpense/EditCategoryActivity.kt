package com.example.myexpense

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class EditCategoryActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_category)

        db = DatabaseHelper(this)
        session = SessionManager(this)

        val etName = findViewById<EditText>(R.id.et_category_name)
        val oldName = intent.getStringExtra("CATEGORY_NAME") ?: ""
        etName.setText(oldName)

        findViewById<ImageView>(R.id.iv_back).setOnClickListener {
            showDiscardDialog(etName.text.toString(), oldName)
        }

        findViewById<Button>(R.id.btn_save_changes).setOnClickListener {
            val newName = etName.text.toString().trim()
            if (newName.isNotEmpty()) {
                val userId = session.getUserId()
                if (db.updateCategory(userId, oldName, newName)) {
                    Toast.makeText(this, "Category updated", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show()
                }
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showDiscardDialog(etName.text.toString(), oldName)
            }
        })
    }

    private fun showDiscardDialog(currentName: String, oldName: String) {
        if (currentName != oldName) {
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