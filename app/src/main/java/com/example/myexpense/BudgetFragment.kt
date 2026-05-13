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
    private lateinit var llEmptyState: View
    private lateinit var nsvBudgetContent: View

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
        llEmptyState = view.findViewById(R.id.ll_empty_state)
        nsvBudgetContent = view.findViewById(R.id.nsv_budget_content)
        rvBudgets.layoutManager = LinearLayoutManager(context)

        view.findViewById<ImageView>(R.id.iv_add_budget).setOnClickListener {
            startActivity(Intent(requireContext(), AddBudgetActivity::class.java))
        }

        return view
    }

    private fun loadBudgetData() {
        val userId = session.getUserId()
        val budgets = db.getBudgets(userId)
        
        if (budgets.isEmpty()) {
            llEmptyState.visibility = View.VISIBLE
            nsvBudgetContent.visibility = View.GONE
        } else {
            llEmptyState.visibility = View.GONE
            nsvBudgetContent.visibility = View.VISIBLE
            
            // Calculate expenses per category for this month
            val expenses = db.getExpenses(userId)
            val currency = session.getCurrency()
            
            val expenseTotals = expenses.groupBy { it.category }.mapValues { entry ->
                entry.value.sumOf { it.amount.replace("₱", "").replace("$", "").replace("€", "").replace("£", "").replace("¥", "").replace(",", "").toDoubleOrNull() ?: 0.0 }
            }

            val totalBudget = budgets.sumOf { it.amount.replace("₱", "").replace("$", "").replace("€", "").replace("£", "").replace("¥", "").replace("P", "").replace(",", "").toDoubleOrNull() ?: 0.0 }
            tvTotalBudget.text = "$currency${String.format(Locale.getDefault(), "%,.2f", totalBudget)}"

            // Add "Unbudgeted" section if there are expenses in categories without a budget
            val budgetedCategories = budgets.map { it.category }.toSet()
            val unbudgetedTotal = expenses.filter { it.category !in budgetedCategories }
                .sumOf { it.amount.replace("₱", "").replace(",", "").toDoubleOrNull() ?: 0.0 }
            
            val finalBudgets = budgets.toMutableList()
            val finalExpenseTotals = expenseTotals.toMutableMap()
            
            if (unbudgetedTotal > 0) {
                // We add a "virtual" budget item for unbudgeted spending
                val unbudgetedItem = Budget(id = -999, category = "Unbudgeted", amount = "₱0")
                finalBudgets.add(unbudgetedItem)
                finalExpenseTotals["Unbudgeted"] = unbudgetedTotal
            }

            rvBudgets.adapter = BudgetAdapter(finalBudgets, finalExpenseTotals)
        }
    }

    override fun onResume() {
        super.onResume()
        if (!isHidden) {
            loadBudgetData()
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            loadBudgetData()
        }
    }
}