package com.example.myexpense

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
        rvExpenses.layoutManager = LinearLayoutManager(context)

        val expenses = listOf(
            Expense("Groceries", "April 8, 2026", "Food", "₱2,000.00"),
            Expense("Coffee", "April 7, 2026", "Food", "₱100.00"),
            Expense("Lunch", "April 7, 2026", "Food", "₱65.00"),
            Expense("Tricycle", "April 7, 2026", "Transportation", "₱10.00"),
            Expense("Rent", "April 6, 2026", "Payment", "₱1,800.00"),
            Expense("Multicab", "April 6, 2026", "Transportation", "₱25.00")
        )

        rvExpenses.adapter = ExpenseAdapter(expenses)

        view.findViewById<Button>(R.id.btn_add_expense).setOnClickListener {
            startActivity(Intent(requireContext(), AddExpenseActivity::class.java))
        }

        return view
    }
}