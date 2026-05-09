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
        val etAmount = findViewById<EditText>(R.id.et_budget_amount)
        val tvStartDate = findViewById<TextView>(R.id.tv_start_date)
        val tvEndDate = findViewById<TextView>(R.id.tv_end_date)

        // Set default dates
        tvStartDate.text = dateFormat.format(startDate.time)
        tvEndDate.text = dateFormat.format(endDate.time)

        // Start Date Picker
        tvStartDate.setOnClickListener {
            DatePickerDialog(this, { _, year, month, day ->
                startDate.set(year, month, day)
                tvStartDate.text = dateFormat.format(startDate.time)
            }, startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.get(Calendar.DAY_OF_MONTH)).show()
        }

        // End Date Picker
        tvEndDate.setOnClickListener {
            DatePickerDialog(this, { _, year, month, day ->
                endDate.set(year, month, day)
                tvEndDate.text = dateFormat.format(endDate.time)
            }, endDate.get(Calendar.YEAR), endDate.get(Calendar.MONTH), endDate.get(Calendar.DAY_OF_MONTH)).show()
        }

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
                val budget = Budget(
                    category = category,
                    amount = "₱$amountStr"
                )
                
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