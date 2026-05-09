package com.example.myexpense

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ExpenseAdapter(private val expenses: List<Expense>) :
    RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    class ExpenseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val row: View = view.findViewById(R.id.ll_expense_row)
        val tvName: TextView = view.findViewById(R.id.tv_expense_name)
        val tvDate: TextView = view.findViewById(R.id.tv_expense_date)
        val tvCategory: TextView = view.findViewById(R.id.tv_expense_category)
        val tvAmount: TextView = view.findViewById(R.id.tv_expense_amount)
        val divider: View = view.findViewById(R.id.v_divider)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenses[position]
        holder.tvName.text = expense.name
        holder.tvDate.text = expense.date
        holder.tvCategory.text = expense.category
        holder.tvAmount.text = expense.amount

        // Pass ID and details to edit screen
        holder.row.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, EditExpenseActivity::class.java)
            intent.putExtra("EXPENSE_ID", expense.id)
            intent.putExtra("EXPENSE_NAME", expense.name)
            intent.putExtra("EXPENSE_AMOUNT", expense.amount)
            intent.putExtra("EXPENSE_CATEGORY", expense.category)
            context.startActivity(intent)
        }

        // Hide divider for the last item
        if (position == expenses.size - 1) {
            holder.divider.visibility = View.GONE
        } else {
            holder.divider.visibility = View.VISIBLE
        }
    }

    override fun getItemCount() = expenses.size
}