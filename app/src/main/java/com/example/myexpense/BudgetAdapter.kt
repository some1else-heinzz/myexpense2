package com.example.myexpense

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BudgetAdapter(private val budgets: List<Budget>) :
    RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder>() {

    class BudgetViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tv_expense_name)
        val tvAmount: TextView = view.findViewById(R.id.tv_expense_amount)
        val divider: View = view.findViewById(R.id.v_divider)
        
        // Repurpose category view for amount subtitle in budget
        init {
             view.findViewById<TextView>(R.id.tv_expense_category).visibility = View.GONE
             view.findViewById<TextView>(R.id.tv_expense_date).visibility = View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_expense, parent, false)
        return BudgetViewHolder(view)
    }

    override fun onBindViewHolder(holder: BudgetViewHolder, position: Int) {
        val budget = budgets[position]
        holder.tvName.text = budget.category
        holder.tvAmount.text = budget.amount

        // Hide divider for the last item
        if (position == budgets.size - 1) {
            holder.divider.visibility = View.GONE
        } else {
            holder.divider.visibility = View.VISIBLE
        }
    }

    override fun getItemCount() = budgets.size
}