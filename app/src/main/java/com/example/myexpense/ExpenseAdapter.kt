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

class ExpenseAdapter(private val expenses: List<Expense>) :
    RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    private var categoryCache: Map<String, Category>? = null

    class ExpenseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val row: View = view.findViewById(R.id.ll_expense_row)
        val ivIcon: ImageView = view.findViewById(R.id.iv_expense_category_icon)
        val flIconBg: FrameLayout = view.findViewById(R.id.fl_expense_icon_bg)
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
        val context = holder.itemView.context
        
        holder.tvName.text = expense.name
        holder.tvDate.text = expense.date
        holder.tvCategory.text = expense.category
        holder.tvAmount.text = expense.amount

        // Load Category Info for Icon/Color
        if (categoryCache == null) {
            val db = DatabaseHelper(context)
            val session = SessionManager(context)
            categoryCache = db.getCategories(session.getUserId()).associateBy { it.name }
        }

        val category = categoryCache?.get(expense.category)
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

        // Pass ID and details to details screen
        holder.row.setOnClickListener {
            val intent = Intent(context, ExpenseDetailsActivity::class.java)
            intent.putExtra("EXPENSE_ID", expense.id)
            intent.putExtra("EXPENSE_NAME", expense.name)
            intent.putExtra("EXPENSE_AMOUNT", expense.amount)
            intent.putExtra("EXPENSE_CATEGORY", expense.category)
            intent.putExtra("EXPENSE_DATE", expense.date)
            intent.putExtra("EXPENSE_NOTES", expense.notes)
            context.startActivity(intent)
        }

        if (position == expenses.size - 1) {
            holder.divider.visibility = View.GONE
        } else {
            holder.divider.visibility = View.VISIBLE
        }
    }

    override fun getItemCount() = expenses.size
}