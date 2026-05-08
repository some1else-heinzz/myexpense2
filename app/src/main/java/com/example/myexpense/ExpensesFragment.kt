package com.example.myexpense

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ExpensesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_expenses, container, false)

        val rvExpenses = view.findViewById<RecyclerView>(R.id.rv_expenses)
        val llEmptyState = view.findViewById<LinearLayout>(R.id.ll_empty_state)
        val cvExpenseList = view.findViewById<View>(R.id.cv_expense_list)

        rvExpenses.layoutManager = LinearLayoutManager(context)

        // Mock data - in the future, this list will come from your database
        val expenses = listOf(
            Expense("Groceries", "April 8, 2026", "Food", "₱2,000.00"),
            Expense("Coffee", "April 7, 2026", "Food", "₱100.00"),
            Expense("Lunch", "April 7, 2026", "Food", "₱65.00"),
            Expense("Tricycle", "April 7, 2026", "Transportation", "₱10.00"),
            Expense("Rent", "April 6, 2026", "Payment", "₱1,800.00"),
            Expense("Multicab", "April 6, 2026", "Transportation", "₱25.00")
        )

        // DATABASE READY LOGIC:
        // Toggle visibility based on whether the expenses list is empty
        if (expenses.isEmpty()) {
            llEmptyState.visibility = View.VISIBLE
            cvExpenseList.visibility = View.GONE
        } else {
            llEmptyState.visibility = View.GONE
            cvExpenseList.visibility = View.VISIBLE
            rvExpenses.adapter = ExpenseAdapter(expenses)
        }

        view.findViewById<Button>(R.id.btn_add_expense).setOnClickListener {
            startActivity(Intent(requireContext(), AddExpenseActivity::class.java))
        }

        return view
    }
}