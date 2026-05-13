package com.example.myexpense

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class EditBudgetActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var session: SessionManager
    private var budgetId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_budget)

        db = DatabaseHelper(this)
        session = SessionManager(this)

        val etCategory = findViewById<AutoCompleteTextView>(R.id.et_category)
        val tvCurrencySymbol = findViewById<TextView>(R.id.tv_currency_symbol)
        val etAmount = findViewById<EditText>(R.id.et_budget_amount)

        // Set current currency
        tvCurrencySymbol.text = session.getCurrency()

        budgetId = intent.getIntExtra("BUDGET_ID", -1)
        val oldCategory = intent.getStringExtra("BUDGET_CATEGORY")
        val oldAmount = intent.getStringExtra("BUDGET_AMOUNT")

        etCategory.setText(oldCategory, false)
        etAmount.setText(oldAmount?.replace("₱", "")?.replace("P", "")?.replace(",", ""))

        // Populate Category Selection
        val userId = session.getUserId()
        val categories = db.getCategories(userId).map { it.name }
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories)
        etCategory.setAdapter(adapter)

        etCategory.setOnClickListener {
            etCategory.showDropDown()
        }

        findViewById<ImageView>(R.id.iv_back).setOnClickListener {
            showDiscardDialog()
        }

        findViewById<Button>(R.id.btn_save_changes).setOnClickListener {
            val category = etCategory.text.toString()
            val amountStr = etAmount.text.toString()

            if (category.isNotEmpty() && amountStr.isNotEmpty()) {
                val currency = session.getCurrency()
                val budget = Budget(
                    id = budgetId,
                    category = category,
                    amount = "$currency$amountStr"
                )
                
                if (db.updateBudget(userId, budget)) {
                    // Sync category
                    val existingCategories = db.getCategories(userId)
                    if (existingCategories.none { it.name.equals(category, ignoreCase = true) }) {
                        db.addCategory(userId, Category(category, "ic_others", "#9E9E9E"))
                        Toast.makeText(this, "Budget updated and Category created", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Budget updated", Toast.LENGTH_SHORT).show()
                    }
                    finish()
                } else {
                    Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show()
                }
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