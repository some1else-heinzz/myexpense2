package com.example.myexpense

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class AddBudgetActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_budget)

        db = DatabaseHelper(this)
        session = SessionManager(this)

        val etCategory = findViewById<EditText>(R.id.et_category)
        val etAmount = findViewById<EditText>(R.id.et_budget_amount)

        findViewById<ImageView>(R.id.iv_back).setOnClickListener {
            showDiscardDialog()
        }

        findViewById<Button>(R.id.btn_save_budget).setOnClickListener {
            val category = etCategory.text.toString()
            val amountStr = etAmount.text.toString()

            if (category.isNotEmpty() && amountStr.isNotEmpty()) {
                val budget = Budget(
                    category = category,
                    amount = "₱$amountStr"
                )
                
                val userId = session.getUserId()
                if (db.addBudget(userId, budget)) {
                    Toast.makeText(this, "Budget saved", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Failed to save budget", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT).show()
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