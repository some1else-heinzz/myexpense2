package com.example.myexpense

import android.app.Dialog
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

class EditExpenseActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var session: SessionManager
    private var expenseId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_expense)

        db = DatabaseHelper(this)
        session = SessionManager(this)

        val etCategory = findViewById<AutoCompleteTextView>(R.id.et_category)
        val etDescription = findViewById<EditText>(R.id.et_description)
        val etAmount = findViewById<EditText>(R.id.et_amount)

        // Populate Category Selection
        val userId = session.getUserId()
        val categories = db.getCategories(userId).map { it.name }
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories)
        etCategory.setAdapter(adapter)

        // Show dropdown immediately on click
        etCategory.setOnClickListener {
            etCategory.showDropDown()
        }

        // Populate fields if data is passed
        expenseId = intent.getIntExtra("EXPENSE_ID", -1)
        val expenseName = intent.getStringExtra("EXPENSE_NAME")
        val expenseAmount = intent.getStringExtra("EXPENSE_AMOUNT")
        val expenseCategory = intent.getStringExtra("EXPENSE_CATEGORY")

        etDescription.setText(expenseName)
        etAmount.setText(expenseAmount?.replace("₱", "")?.replace(",", ""))
        etCategory.setText(expenseCategory, false)

        findViewById<ImageView>(R.id.iv_back).setOnClickListener {
            showDiscardDialog()
        }

        findViewById<Button>(R.id.btn_save_changes).setOnClickListener {
            val category = etCategory.text.toString()
            val description = etDescription.text.toString()
            val amountStr = etAmount.text.toString()

            if (category.isNotEmpty() && description.isNotEmpty() && amountStr.isNotEmpty()) {
                val updatedExpense = Expense(
                    id = expenseId,
                    name = description,
                    date = "April 9, 2026", // Should ideally preserve original date
                    category = category,
                    amount = "₱$amountStr"
                )
                
                if (db.updateExpense(userId, updatedExpense)) {
                    Toast.makeText(this, "Expense updated", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show()
                }
            }
        }

        findViewById<Button>(R.id.tv_delete_expense).setOnClickListener {
            showDeleteDialog()
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

    private fun showDeleteDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_delete_expense)

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
        dialog.window?.setLayout(width, android.view.ViewGroup.LayoutParams.WRAP_CONTENT)

        dialog.findViewById<Button>(R.id.btn_cancel).setOnClickListener {
            dialog.dismiss()
        }

        dialog.findViewById<Button>(R.id.btn_delete_confirm).setOnClickListener {
            val userId = session.getUserId()
            if (db.deleteExpense(userId, expenseId)) {
                Toast.makeText(this, "Expense deleted", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
                finish()
            } else {
                Toast.makeText(this, "Delete failed", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }

        dialog.show()
    }
}