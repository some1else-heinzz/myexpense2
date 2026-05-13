package com.example.myexpense

import android.app.DatePickerDialog
import android.app.Dialog
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

class EditExpenseActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var session: SessionManager
    private var expenseId: Int = -1
    private var selectedDate: Calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_expense)

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
        val ivCalendar = findViewById<ImageView>(R.id.iv_calendar)

        // Date Picker logic
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

        // Show dropdown immediately on click
        etCategory.setOnClickListener {
            etCategory.showDropDown()
        }

        // Populate fields if data is passed
        expenseId = intent.getIntExtra("EXPENSE_ID", -1)
        val expenseName = intent.getStringExtra("EXPENSE_NAME")
        val expenseAmount = intent.getStringExtra("EXPENSE_AMOUNT")
        val expenseCategory = intent.getStringExtra("EXPENSE_CATEGORY")
        val expenseDate = intent.getStringExtra("EXPENSE_DATE")
        val expenseNotes = intent.getStringExtra("EXPENSE_NOTES")

        etDescription.setText(expenseName)
        etAmount.setText(expenseAmount?.replace("₱", "")?.replace(",", ""))
        etCategory.setText(expenseCategory, false)
        etNotes.setText(expenseNotes)
        
        if (!expenseDate.isNullOrEmpty()) {
            tvDate.text = expenseDate
            try {
                dateFormat.parse(expenseDate)?.let {
                    selectedDate.time = it
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        findViewById<ImageView>(R.id.iv_back).setOnClickListener {
            showDiscardDialog()
        }

        findViewById<Button>(R.id.btn_save_changes).setOnClickListener {
            val category = etCategory.text.toString()
            val description = etDescription.text.toString()
            val amountStr = etAmount.text.toString()
            val notes = etNotes.text.toString()

            if (category.isNotEmpty() && description.isNotEmpty() && amountStr.isNotEmpty()) {
                val currency = session.getCurrency()
                val updatedExpense = Expense(
                    id = expenseId,
                    name = description,
                    date = tvDate.text.toString(),
                    category = category,
                    amount = "$currency$amountStr",
                    notes = notes
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