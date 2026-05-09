package com.example.myexpense

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ExpensesFragment : Fragment() {

    private lateinit var db: DatabaseHelper
    private lateinit var session: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_expenses, container, false)

        db = DatabaseHelper(requireContext())
        session = SessionManager(requireContext())

        val rvExpenses = view.findViewById<RecyclerView>(R.id.rv_expenses)
        val llEmptyState = view.findViewById<LinearLayout>(R.id.ll_empty_state)
        val cvExpenseList = view.findViewById<View>(R.id.cv_expense_list)

        rvExpenses.layoutManager = LinearLayoutManager(context)

        val userId = session.getUserId()
        val expenses = db.getExpenses(userId)

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