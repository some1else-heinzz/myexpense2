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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddBudgetActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var session: SessionManager
    private var startDate: Calendar = Calendar.getInstance()
    private var endDate: Calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_budget)

        db = DatabaseHelper(this)
        session = SessionManager(this)

        val etCategory = findViewById<AutoCompleteTextView>(R.id.et_category)
        val tvCurrencySymbol = findViewById<TextView>(R.id.tv_currency_symbol)
        val etAmount = findViewById<EditText>(R.id.et_budget_amount)

        // Set current currency
        tvCurrencySymbol.text = session.getCurrency()

        val tvStartDate = findViewById<TextView>(R.id.tv_start_date)
        val tvEndDate = findViewById<TextView>(R.id.tv_end_date)
        val llStartDate = findViewById<View>(R.id.ll_start_date_container)
        val llEndDate = findViewById<View>(R.id.ll_end_date_container)

        // Set default dates
        tvStartDate.text = dateFormat.format(startDate.time)
        tvEndDate.text = dateFormat.format(endDate.time)

        // Start Date Picker
        val startPickerListener = View.OnClickListener {
            DatePickerDialog(this, { _, year, month, day ->
                startDate.set(year, month, day)
                tvStartDate.text = dateFormat.format(startDate.time)
            }, startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.get(Calendar.DAY_OF_MONTH)).show()
        }
        tvStartDate.setOnClickListener(startPickerListener)
        llStartDate.setOnClickListener(startPickerListener)

        // End Date Picker
        val endPickerListener = View.OnClickListener {
            DatePickerDialog(this, { _, year, month, day ->
                endDate.set(year, month, day)
                tvEndDate.text = dateFormat.format(endDate.time)
            }, endDate.get(Calendar.YEAR), endDate.get(Calendar.MONTH), endDate.get(Calendar.DAY_OF_MONTH)).show()
        }
        tvEndDate.setOnClickListener(endPickerListener)
        llEndDate.setOnClickListener(endPickerListener)

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

        findViewById<Button>(R.id.btn_save_budget).setOnClickListener {
            val category = etCategory.text.toString()
            val amountStr = etAmount.text.toString()

            if (category.isNotEmpty() && amountStr.isNotEmpty()) {
                val currency = session.getCurrency()
                val budget = Budget(
                    category = category,
                    amount = "$currency$amountStr"
                )
                
                if (db.addBudget(userId, budget)) {
                    // Check if category exists, if not, create it
                    val existingCategories = db.getCategories(userId)
                    if (existingCategories.none { it.name.equals(category, ignoreCase = true) }) {
                        db.addCategory(userId, Category(category, "ic_others", "#9E9E9E"))
                        Toast.makeText(this, "Budget saved and Category '$category' created", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Budget saved", Toast.LENGTH_SHORT).show()
                    }
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