package com.example.myexpense

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment

class DashboardFragment : Fragment() {

    private lateinit var db: DatabaseHelper
    private lateinit var session: SessionManager
    private lateinit var containerActivities: LinearLayout
    private lateinit var tvTotalBudget: TextView
    private lateinit var tvRemaining: TextView
    private lateinit var tvWelcome: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)
        
        db = DatabaseHelper(requireContext())
        session = SessionManager(requireContext())

        tvWelcome = view.findViewById(R.id.tv_welcome_title)
        containerActivities = view.findViewById(R.id.ll_recent_activities)
        tvTotalBudget = view.findViewById(R.id.tv_total_budget)
        tvRemaining = view.findViewById(R.id.tv_remaining)

        view.findViewById<CardView>(R.id.card_add_expense).setOnClickListener {
            startActivity(Intent(requireContext(), AddExpenseActivity::class.java))
        }

        view.findViewById<CardView>(R.id.card_add_budget).setOnClickListener {
            startActivity(Intent(requireContext(), AddBudgetActivity::class.java))
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        loadDashboardData()
    }

    private fun loadDashboardData() {
        val fullName = session.getFullName() ?: "User"
        tvWelcome.text = "Welcome back, $fullName"

        val userId = session.getUserId()
        val activities = db.getExpenses(userId)

        containerActivities.removeAllViews()
        if (activities.isEmpty()) {
            val emptyText = TextView(requireContext())
            emptyText.text = "No recent activities"
            emptyText.setPadding(32, 32, 32, 32)
            emptyText.gravity = android.view.Gravity.CENTER
            containerActivities.addView(emptyText)
        } else {
            activities.take(4).forEachIndexed { index, expense ->
                val rowView = layoutInflater.inflate(R.layout.item_expense, containerActivities, false)
                rowView.findViewById<TextView>(R.id.tv_expense_name).text = expense.name
                rowView.findViewById<TextView>(R.id.tv_expense_date).text = expense.date
                rowView.findViewById<TextView>(R.id.tv_expense_category).text = expense.category
                rowView.findViewById<TextView>(R.id.tv_expense_amount).text = expense.amount
                
                rowView.findViewById<View>(R.id.ll_expense_row).setOnClickListener {
                    val intent = Intent(requireContext(), ExpenseDetailsActivity::class.java)
                    intent.putExtra("EXPENSE_ID", expense.id)
                    intent.putExtra("EXPENSE_NAME", expense.name)
                    intent.putExtra("EXPENSE_AMOUNT", expense.amount)
                    intent.putExtra("EXPENSE_CATEGORY", expense.category)
                    intent.putExtra("EXPENSE_DATE", expense.date)
                    intent.putExtra("EXPENSE_NOTES", expense.notes)
                    startActivity(intent)
                }

                if (index == activities.size - 1 || index == 3) {
                    rowView.findViewById<View>(R.id.v_divider).visibility = View.GONE
                }
                
                containerActivities.addView(rowView)
            }
        }

        // Update Total Budget and Remaining
        val budgets = db.getBudgets(userId)
        val totalBudget = budgets.sumOf { it.amount.replace("₱", "").replace("P", "").replace(",", "").toDoubleOrNull() ?: 0.0 }
        val totalSpent = activities.sumOf { it.amount.replace("₱", "").replace(",", "").toDoubleOrNull() ?: 0.0 }
        
        tvTotalBudget.text = "₱$totalBudget"
        tvRemaining.text = "₱${totalBudget - totalSpent}"
    }
}