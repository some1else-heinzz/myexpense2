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
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)
        val containerActivities = view.findViewById<LinearLayout>(R.id.ll_recent_activities)

        view.findViewById<CardView>(R.id.card_add_expense).setOnClickListener {
            startActivity(Intent(requireContext(), AddExpenseActivity::class.java))
        }

        view.findViewById<CardView>(R.id.card_add_budget).setOnClickListener {
            startActivity(Intent(requireContext(), AddBudgetActivity::class.java))
        }

        val activities = listOf(
            Expense("Groceries", "April 8, 2026", "Food", "₱2,000.00"),
            Expense("Coffee", "April 7, 2026", "Food", "₱100.00"),
            Expense("Lunch", "April 7, 2026", "Food", "₱65.00"),
            Expense("Tricycle", "April 7, 2026", "Transportation", "₱10.00")
        )

        activities.forEachIndexed { index, expense ->
            val row = layoutInflater.inflate(R.layout.item_expense, containerActivities, false)
            row.findViewById<TextView>(R.id.tv_expense_name).text = expense.name
            row.findViewById<TextView>(R.id.tv_expense_date).text = expense.date
            row.findViewById<TextView>(R.id.tv_expense_category).text = expense.category
            row.findViewById<TextView>(R.id.tv_expense_amount).text = expense.amount
            
            // Step 4: Add click listener to start EditExpenseActivity for dashboard rows
            row.setOnClickListener {
                val intent = Intent(requireContext(), EditExpenseActivity::class.java)
                intent.putExtra("EXPENSE_NAME", expense.name)
                startActivity(intent)
            }

            // Remove divider for the last item
            if (index == activities.size - 1) {
                row.findViewById<View>(R.id.v_divider).visibility = View.GONE
            }
            
            containerActivities.addView(row)
        }

        return view
    }
}