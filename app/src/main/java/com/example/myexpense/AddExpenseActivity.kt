package com.example.myexpense

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
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
import com.google.android.material.switchmaterial.SwitchMaterial
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddExpenseActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var session: SessionManager
    private var selectedDate: Calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        db = DatabaseHelper(this)
        session = SessionManager(this)

        val tvDate = findViewById<TextView>(R.id.tv_date)
        val llDateContainer = findViewById<View>(R.id.ll_date_container)
        val tvCurrencySymbol = findViewById<TextView>(R.id.tv_currency_symbol)
        val etCategory = findViewById<AutoCompleteTextView>(R.id.et_category)

        // Set current currency symbol
        tvCurrencySymbol.text = session.getCurrency()

        val etDescription = findViewById<EditText>(R.id.et_description)
        val etAmount = findViewById<EditText>(R.id.et_amount)
        val etNotes = findViewById<EditText>(R.id.et_notes)
        val swFavorite = findViewById<SwitchMaterial>(R.id.sw_favorite)
        val swRecurring = findViewById<SwitchMaterial>(R.id.sw_recurring)
        val ivCalendar = findViewById<ImageView>(R.id.iv_calendar)

        // Set current date by default
        tvDate.text = dateFormat.format(selectedDate.time)

        // Date Picker
        val dateClickListener = View.OnClickListener {
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    selectedDate.set(year, month, dayOfMonth)
                    tvDate.text = dateFormat.format(selectedDate.time)
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        tvDate.setOnClickListener(dateClickListener)
        llDateContainer.setOnClickListener(dateClickListener)
        ivCalendar?.setOnClickListener(dateClickListener)

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

        findViewById<Button>(R.id.btn_save_expense).setOnClickListener {
            val category = etCategory.text.toString()
            val description = etDescription.text.toString()
            val amountStr = etAmount.text.toString()
            val notes = etNotes.text.toString()

            if (category.isNotEmpty() && description.isNotEmpty() && amountStr.isNotEmpty()) {
                val currency = session.getCurrency()
                val expense = Expense(
                    name = description,
                    date = tvDate.text.toString(),
                    category = category,
                    amount = "$currency$amountStr",
                    notes = notes
                )
                
                if (db.addExpense(userId, expense)) {
                    // Save as Favorite if toggled
                    if (swFavorite.isChecked) {
                        db.addTemplate(userId, description, category, "$currency$amountStr")
                    }

                    // Save as Recurring if toggled
                    if (swRecurring.isChecked) {
                        db.addRecurring(userId, description, category, "$currency$amountStr")
                    }

                    Toast.makeText(this, "Expense saved", Toast.LENGTH_SHORT).show()
                    checkBudgetThreshold(userId, category)
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

    private fun checkBudgetThreshold(userId: Int, category: String) {
        val budget = db.getBudgets(userId).find { it.category == category } ?: return
        val budgetAmount = budget.amount.replace("₱", "").replace("$", "").replace("€", "").replace("£", "").replace("¥", "").replace("P", "").replace(",", "").toDoubleOrNull() ?: 0.0
        if (budgetAmount <= 0) return

        val currentMonth = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Calendar.getInstance().time)
        val allExpenses = db.getExpenses(userId, category)
        val monthSpent = allExpenses.filter { it.date.contains(currentMonth.split(" ")[0]) && it.date.contains(currentMonth.split(" ")[1]) }
            .sumOf { it.amount.replace("₱", "").replace("$", "").replace("€", "").replace("£", "").replace("¥", "").replace(",", "").toDoubleOrNull() ?: 0.0 }

        val ratio = monthSpent / budgetAmount
        if (ratio >= 1.0) {
            Toast.makeText(this, "ALERT: You have EXCEEDED your $category budget!", Toast.LENGTH_LONG).show()
        } else if (ratio >= 0.8) {
            Toast.makeText(this, "WARNING: You have used 80% of your $category budget.", Toast.LENGTH_LONG).show()
        }
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