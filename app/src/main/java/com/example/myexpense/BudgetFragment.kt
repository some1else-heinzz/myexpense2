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
import java.util.Locale

class BudgetFragment : Fragment() {

    private lateinit var db: DatabaseHelper
    private lateinit var session: SessionManager
    private lateinit var rvBudgets: RecyclerView
    private lateinit var tvTotalBudget: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_budget, container, false)

        db = DatabaseHelper(requireContext())
        session = SessionManager(requireContext())

        rvBudgets = view.findViewById(R.id.rv_budgets)
        tvTotalBudget = view.findViewById(R.id.tv_total_budget)
        rvBudgets.layoutManager = LinearLayoutManager(context)

        view.findViewById<ImageView>(R.id.iv_add_budget).setOnClickListener {
            startActivity(Intent(requireContext(), AddBudgetActivity::class.java))
        }

        return view
    }

    private fun loadBudgetData() {
        val userId = session.getUserId()
        val budgets = db.getBudgets(userId)
        
        val totalBudget = budgets.sumOf { it.amount.replace("₱", "").replace("P", "").replace(",", "").toDoubleOrNull() ?: 0.0 }
        tvTotalBudget.text = "₱${String.format(Locale.getDefault(), "%,.1f", totalBudget)}"

        rvBudgets.adapter = BudgetAdapter(budgets)
    }

    override fun onResume() {
        super.onResume()
        loadBudgetData()
    }
}