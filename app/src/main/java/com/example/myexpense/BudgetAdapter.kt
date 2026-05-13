package com.example.myexpense

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale

class BudgetAdapter(
    private val budgets: List<Budget>,
    private val expenseTotals: Map<String, Double>
) : RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder>() {

    class BudgetViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tv_category_name)
        val tvAmount: TextView = view.findViewById(R.id.tv_category_budget)
        val pbBudget: ProgressBar = view.findViewById(R.id.pb_budget)
        val tvStatus: TextView = view.findViewById(R.id.tv_budget_status)
        val container: View = view.findViewById(R.id.ll_budget_container)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_budget, parent, false)
        return BudgetViewHolder(view)
    }

    override fun onBindViewHolder(holder: BudgetViewHolder, position: Int) {
        val budget = budgets[position]
        val context = holder.itemView.context
        val session = SessionManager(context)
        val currency = session.getCurrency()
        
        val budgetAmount = budget.amount.replace("₱", "").replace("$", "").replace("€", "").replace("£", "").replace("¥", "").replace("P", "").replace(",", "").toDoubleOrNull() ?: 0.0
        val spentAmount = expenseTotals[budget.category] ?: 0.0
        
        holder.tvName.text = budget.category
        holder.tvAmount.text = "$currency${String.format(Locale.getDefault(), "%,.2f", budgetAmount)}"
        
        // Progress Bar
        val progress = if (budgetAmount > 0) ((spentAmount / budgetAmount) * 100).toInt() else 0
        holder.pbBudget.progress = progress.coerceAtMost(100)
        
        // Status Text
        val spentFormatted = String.format(Locale.getDefault(), "$currency%,.2f", spentAmount)
        val totalFormatted = String.format(Locale.getDefault(), "$currency%,.2f", budgetAmount)
        holder.tvStatus.text = "$spentFormatted spent of $totalFormatted"
        
        // Color coding for progress
        if (progress >= 90) {
            holder.pbBudget.progressTintList = android.content.res.ColorStateList.valueOf(context.getColor(R.color.red_delete))
        } else {
            holder.pbBudget.progressTintList = null // Uses default from XML
        }

        holder.container.setOnClickListener {
            val intent = Intent(context, BudgetDetailsActivity::class.java)
            intent.putExtra("BUDGET_ID", budget.id)
            intent.putExtra("BUDGET_CATEGORY", budget.category)
            intent.putExtra("BUDGET_AMOUNT", budget.amount)
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = budgets.size
}