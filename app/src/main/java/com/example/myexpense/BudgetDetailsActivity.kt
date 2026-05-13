package com.example.myexpense

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class BudgetDetailsActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var session: SessionManager
    private var budgetId: Int = -1
    private var categoryName: String? = null
    private var amount: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget_details)

        db = DatabaseHelper(this)
        session = SessionManager(this)

        budgetId = intent.getIntExtra("BUDGET_ID", -1)
        categoryName = intent.getStringExtra("BUDGET_CATEGORY")
        amount = intent.getStringExtra("BUDGET_AMOUNT")

        val currency = session.getCurrency()
        val cleanedAmount = amount?.replace("₱", "")?.replace("$", "")?.replace("€", "")?.replace("£", "")?.replace("¥", "")?.replace(",", "") ?: "0.00"
        findViewById<TextView>(R.id.tv_detail_amount).text = "$currency$cleanedAmount"
        findViewById<TextView>(R.id.tv_detail_category).text = categoryName

        // Load Icon
        val userId = session.getUserId()
        val categories = db.getCategories(userId).associateBy { it.name }
        val catInfo = categories[categoryName]
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

        findViewById<Button>(R.id.btn_edit_budget).setOnClickListener {
            val intent = Intent(this, EditBudgetActivity::class.java)
            intent.putExtra("BUDGET_ID", budgetId)
            intent.putExtra("BUDGET_CATEGORY", categoryName)
            intent.putExtra("BUDGET_AMOUNT", amount)
            startActivity(intent)
            finish()
        }

        findViewById<Button>(R.id.btn_delete_budget).setOnClickListener {
            showDeleteDialog()
        }
    }

    private fun showDeleteDialog() {
        AlertDialog.Builder(this)
            .setTitle("Delete Budget")
            .setMessage("Are you sure you want to delete this budget?")
            .setPositiveButton("Delete") { _, _ ->
                val userId = session.getUserId()
                if (db.deleteBudget(userId, budgetId)) {
                    Toast.makeText(this, "Budget deleted", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}