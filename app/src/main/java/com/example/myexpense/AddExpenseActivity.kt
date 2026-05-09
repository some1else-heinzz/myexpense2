package com.example.myexpense

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class AddExpenseActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        db = DatabaseHelper(this)
        session = SessionManager(this)

        val etCategory = findViewById<AutoCompleteTextView>(R.id.et_category)
        val etDescription = findViewById<EditText>(R.id.et_description)
        val etAmount = findViewById<EditText>(R.id.et_amount)
        val etPayment = findViewById<EditText>(R.id.et_payment_method)

        // Populate Category Selection
        val userId = session.getUserId()
        val categories = db.getCategories(userId).map { it.name }
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories)
        etCategory.setAdapter(adapter)
        
        // Show dropdown immediately on click
        etCategory.setOnClickListener {
            etCategory.showDropDown()
        }

        findViewById<ImageView>(R.id.iv_back).setOnClickListener {
            showDiscardDialog()
        }

        findViewById<Button>(R.id.btn_save_expense).setOnClickListener {
            val category = etCategory.text.toString()
            val description = etDescription.text.toString()
            val amountStr = etAmount.text.toString()

            if (category.isNotEmpty() && description.isNotEmpty() && amountStr.isNotEmpty()) {
                val expense = Expense(
                    name = description,
                    date = "April 9, 2026", // You can update this to use a real date picker later
                    category = category,
                    amount = "₱$amountStr"
                )
                
                if (db.addExpense(userId, expense)) {
                    Toast.makeText(this, "Expense saved", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Failed to save expense", Toast.LENGTH_SHORT).show()
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