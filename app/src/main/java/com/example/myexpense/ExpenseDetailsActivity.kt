package com.example.myexpense

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ExpenseDetailsActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var session: SessionManager
    private var expenseId: Int = -1
    private var expenseName: String? = null
    private var expenseAmount: String? = null
    private var expenseCategory: String? = null
    private var expenseDate: String? = null
    private var expenseNotes: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense_details)

        db = DatabaseHelper(this)
        session = SessionManager(this)

        // Get data from intent
        expenseId = intent.getIntExtra("EXPENSE_ID", -1)
        expenseName = intent.getStringExtra("EXPENSE_NAME")
        expenseAmount = intent.getStringExtra("EXPENSE_AMOUNT")
        expenseCategory = intent.getStringExtra("EXPENSE_CATEGORY")
        expenseDate = intent.getStringExtra("EXPENSE_DATE")
        expenseNotes = intent.getStringExtra("EXPENSE_NOTES")

        // Display data
        findViewById<TextView>(R.id.tv_detail_amount).text = expenseAmount
        findViewById<TextView>(R.id.tv_detail_name).text = expenseName
        findViewById<TextView>(R.id.tv_detail_date).text = expenseDate
        findViewById<TextView>(R.id.tv_detail_category).text = expenseCategory
        
        val tvNotes = findViewById<TextView>(R.id.tv_detail_notes)
        if (!expenseNotes.isNullOrBlank()) {
            tvNotes.text = expenseNotes
        } else {
            tvNotes.text = "No notes provided"
        }

        // Set Icon
        val userId = session.getUserId()
        val categories = db.getCategories(userId).associateBy { it.name }
        val catInfo = categories[expenseCategory]
        if (catInfo != null) {
            val ivIcon = findViewById<ImageView>(R.id.iv_preview_icon)
            val flIconBg = findViewById<FrameLayout>(R.id.fl_preview_bg)
            
            val resId = resources.getIdentifier(catInfo.iconResName, "drawable", packageName)
            if (resId != 0) ivIcon.setImageResource(resId)
            
            try {
                val color = Color.parseColor(catInfo.colorHex)
                ivIcon.setColorFilter(color)
                val alphaColor = Color.argb(30, Color.red(color), Color.green(color), Color.blue(color))
                val shape = GradientDrawable()
                shape.shape = GradientDrawable.OVAL
                shape.setColor(alphaColor)
                flIconBg.background = shape
            } catch (e: Exception) {}
        }

        findViewById<ImageView>(R.id.iv_back).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btn_edit_expense).setOnClickListener {
            val intent = Intent(this, EditExpenseActivity::class.java)
            intent.putExtra("EXPENSE_ID", expenseId)
            intent.putExtra("EXPENSE_NAME", expenseName)
            intent.putExtra("EXPENSE_AMOUNT", expenseAmount)
            intent.putExtra("EXPENSE_CATEGORY", expenseCategory)
            intent.putExtra("EXPENSE_DATE", expenseDate)
            intent.putExtra("EXPENSE_NOTES", expenseNotes)
            startActivity(intent)
            finish()
        }

        findViewById<Button>(R.id.btn_delete_expense).setOnClickListener {
            showDeleteDialog()
        }
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