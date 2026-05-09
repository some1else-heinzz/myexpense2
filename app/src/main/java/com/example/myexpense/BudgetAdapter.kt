package com.example.myexpense

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BudgetAdapter(private val budgets: List<Budget>) :
    RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder>() {

    private var categoryCache: Map<String, Category>? = null

    class BudgetViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val row: View = view.findViewById(R.id.ll_expense_row)
        val ivIcon: ImageView = view.findViewById(R.id.iv_expense_category_icon)
        val flIconBg: FrameLayout = view.findViewById(R.id.fl_expense_icon_bg)
        val tvName: TextView = view.findViewById(R.id.tv_expense_name)
        val tvAmount: TextView = view.findViewById(R.id.tv_expense_amount)
        val divider: View = view.findViewById(R.id.v_divider)
        
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
        val context = holder.itemView.context
        
        holder.tvName.text = budget.category
        holder.tvAmount.text = budget.amount

        // Load Category Info for Icon/Color
        if (categoryCache == null) {
            val db = DatabaseHelper(context)
            val session = SessionManager(context)
            categoryCache = db.getCategories(session.getUserId()).associateBy { it.name }
        }

        val category = categoryCache?.get(budget.category)
        if (category != null) {
            val resId = context.resources.getIdentifier(category.iconResName, "drawable", context.packageName)
            if (resId != 0) holder.ivIcon.setImageResource(resId)
            
            try {
                val color = Color.parseColor(category.colorHex)
                holder.ivIcon.setColorFilter(color)
                val alphaColor = Color.argb(30, Color.red(color), Color.green(color), Color.blue(color))
                val shape = GradientDrawable()
                shape.shape = GradientDrawable.OVAL
                shape.setColor(alphaColor)
                holder.flIconBg.background = shape
            } catch (e: Exception) {}
        }

        holder.row.setOnClickListener {
            val intent = Intent(context, BudgetDetailsActivity::class.java)
            intent.putExtra("BUDGET_ID", budget.id)
            intent.putExtra("BUDGET_CATEGORY", budget.category)
            intent.putExtra("BUDGET_AMOUNT", budget.amount)
            context.startActivity(intent)
        }

        if (position == budgets.size - 1) {
            holder.divider.visibility = View.GONE
        } else {
            holder.divider.visibility = View.VISIBLE
        }
    }

    override fun getItemCount() = budgets.size
}