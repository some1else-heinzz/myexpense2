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
        val etCategory = findViewById<AutoCompleteTextView>(R.id.et_category)
        val etDescription = findViewById<EditText>(R.id.et_description)
        val etAmount = findViewById<EditText>(R.id.et_amount)
        val etPayment = findViewById<EditText>(R.id.et_payment_method)
        val etNotes = findViewById<EditText>(R.id.et_notes)
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
        ivCalendar?.setOnClickListener(dateClickListener)

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
            val notes = etNotes.text.toString()

            if (category.isNotEmpty() && description.isNotEmpty() && amountStr.isNotEmpty()) {
                val expense = Expense(
                    name = description,
                    date = tvDate.text.toString(),
                    category = category,
                    amount = "₱$amountStr",
                    notes = notes
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