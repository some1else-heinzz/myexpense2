package com.example.myexpense

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class BudgetFragment : Fragment() {

    private lateinit var db: DatabaseHelper
    private lateinit var session: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_budget, container, false)

        db = DatabaseHelper(requireContext())
        session = SessionManager(requireContext())

        val rvBudgets = view.findViewById<RecyclerView>(R.id.rv_budgets)
        rvBudgets.layoutManager = LinearLayoutManager(context)

        val userId = session.getUserId()
        val budgets = db.getBudgets(userId)
        val totalBudget = budgets.sumOf { it.amount.replace("₱", "").replace("P", "").replace(",", "").toDoubleOrNull() ?: 0.0 }
        
        view.findViewById<TextView>(R.id.tv_total_budget).text = "₱$totalBudget"

        if (budgets.isEmpty()) {
            // Optional: Add empty state for budget
        } else {
            rvBudgets.adapter = BudgetAdapter(budgets)
        }

        view.findViewById<ImageView>(R.id.iv_add_budget).setOnClickListener {
            startActivity(Intent(requireContext(), AddBudgetActivity::class.java))
        }

        return view
    }
}