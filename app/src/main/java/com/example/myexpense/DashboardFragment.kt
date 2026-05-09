package com.example.myexpense

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DashboardFragment : Fragment() {

    private lateinit var db: DatabaseHelper
    private lateinit var session: SessionManager
    private lateinit var containerActivities: LinearLayout
    private lateinit var containerFavorites: LinearLayout
    private lateinit var tvTotalBudget: TextView
    private lateinit var tvRemaining: TextView
    private lateinit var tvWelcome: TextView
    private lateinit var hsvFavorites: View
    private lateinit var tvFavoritesLabel: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)
        
        db = DatabaseHelper(requireContext())
        session = SessionManager(requireContext())

        tvWelcome = view.findViewById(R.id.tv_welcome_title)
        containerActivities = view.findViewById(R.id.ll_recent_activities)
        containerFavorites = view.findViewById(R.id.ll_favorites_container)
        tvTotalBudget = view.findViewById(R.id.tv_total_budget)
        tvRemaining = view.findViewById(R.id.tv_remaining)
        hsvFavorites = view.findViewById(R.id.hsv_favorites)
        tvFavoritesLabel = view.findViewById(R.id.tv_favorites_label)

        view.findViewById<CardView>(R.id.card_add_expense).setOnClickListener {
            startActivity(Intent(requireContext(), AddExpenseActivity::class.java))
        }

        view.findViewById<CardView>(R.id.card_add_budget).setOnClickListener {
            startActivity(Intent(requireContext(), AddBudgetActivity::class.java))
        }

        view.findViewById<TextView>(R.id.tv_view_all).setOnClickListener {
            (requireActivity() as? DashboardActivity)?.selectTab(R.id.nav_expenses)
        }

        view.findViewById<ImageView>(R.id.iv_notification).setOnClickListener {
            startActivity(Intent(requireContext(), NotificationActivity::class.java))
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        loadDashboardData()
    }

    private fun loadDashboardData() {
        val fullName = session.getFullName() ?: "User"
        tvWelcome.text = "Welcome back, $fullName"

        val userId = session.getUserId()
        
        // Process any recurring expenses for this month
        db.processRecurring(userId)

        val activities = db.getExpenses(userId)
        val categories = db.getCategories(userId).associateBy { it.name }
        val templates = db.getTemplates(userId)
        
        val targetFormat = SimpleDateFormat(session.getDateFormat(), Locale.getDefault())
        val legacyFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())

        // Load Favorites (Quick Add)
        containerFavorites.removeAllViews()
        if (templates.isEmpty()) {
            hsvFavorites.visibility = View.GONE
            tvFavoritesLabel.visibility = View.GONE
        } else {
            hsvFavorites.visibility = View.VISIBLE
            tvFavoritesLabel.visibility = View.VISIBLE
            for (temp in templates) {
                val favView = layoutInflater.inflate(R.layout.item_favorite, containerFavorites, false)
                favView.findViewById<TextView>(R.id.tv_fav_name).text = temp.first
                favView.findViewById<TextView>(R.id.tv_fav_amount).text = temp.third
                
                favView.setOnClickListener {
                    val newExpense = Expense(
                        name = temp.first,
                        category = temp.second,
                        amount = temp.third,
                        date = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(Calendar.getInstance().time),
                        notes = "Quick add from favorites"
                    )
                    if (db.addExpense(userId, newExpense)) {
                        Toast.makeText(requireContext(), "Quick Add: ${temp.first} saved!", Toast.LENGTH_SHORT).show()
                        loadDashboardData() // Refresh
                    }
                }
                containerFavorites.addView(favView)
            }
        }

        containerActivities.removeAllViews()
        if (activities.isEmpty()) {
            val emptyText = TextView(requireContext())
            emptyText.text = "No recent activities"
            emptyText.setPadding(32, 32, 32, 32)
            emptyText.gravity = android.view.Gravity.CENTER
            containerActivities.addView(emptyText)
        } else {
            activities.take(4).forEachIndexed { index, expense ->
                val rowView = layoutInflater.inflate(R.layout.item_expense, containerActivities, false)
                rowView.findViewById<TextView>(R.id.tv_expense_name).text = expense.name
                
                val displayDate = try {
                    val date = legacyFormat.parse(expense.date)
                    if (date != null) targetFormat.format(date) else expense.date
                } catch (e: Exception) {
                    expense.date
                }
                rowView.findViewById<TextView>(R.id.tv_expense_date).text = displayDate
                
                rowView.findViewById<TextView>(R.id.tv_expense_category).text = expense.category
                rowView.findViewById<TextView>(R.id.tv_expense_amount).text = expense.amount
                
                val catInfo = categories[expense.category]
                if (catInfo != null) {
                    val ivIcon = rowView.findViewById<ImageView>(R.id.iv_expense_category_icon)
                    val flIconBg = rowView.findViewById<FrameLayout>(R.id.fl_expense_icon_bg)
                    
                    val resId = resources.getIdentifier(catInfo.iconResName, "drawable", requireContext().packageName)
                    if (resId != 0) ivIcon.setImageResource(resId)
                    
                    try {
                        val color = Color.parseColor(catInfo.colorHex)
                        ivIcon.setColorFilter(color)
                        val alphaColor = Color.argb(30, Color.red(color), Color.green(color), Color.blue(color))
                        val shape = GradientDrawable()
                        shape.shape = GradientDrawable.OVAL
                        shape.setColor(alphaColor)
                        flIconBg.background = shape
                    } catch (e: Exception) {}
                }

                rowView.findViewById<View>(R.id.ll_expense_row).setOnClickListener {
                    val intent = Intent(requireContext(), ExpenseDetailsActivity::class.java)
                    intent.putExtra("EXPENSE_ID", expense.id)
                    intent.putExtra("EXPENSE_NAME", expense.name)
                    intent.putExtra("EXPENSE_AMOUNT", expense.amount)
                    intent.putExtra("EXPENSE_CATEGORY", expense.category)
                    intent.putExtra("EXPENSE_DATE", expense.date)
                    intent.putExtra("EXPENSE_NOTES", expense.notes)
                    startActivity(intent)
                }

                if (index == activities.size - 1 || index == 3) {
                    rowView.findViewById<View>(R.id.v_divider).visibility = View.GONE
                }
                
                containerActivities.addView(rowView)
            }
        }

        val budgets = db.getBudgets(userId)
        val totalBudget = budgets.sumOf { it.amount.replace("₱", "").replace("P", "").replace(",", "").toDoubleOrNull() ?: 0.0 }
        val totalSpent = activities.filter { 
            it.date.contains(SimpleDateFormat("MMMM", Locale.getDefault()).format(Calendar.getInstance().time)) &&
            it.date.contains(SimpleDateFormat("yyyy", Locale.getDefault()).format(Calendar.getInstance().time))
        }.sumOf { it.amount.replace("₱", "").replace(",", "").toDoubleOrNull() ?: 0.0 }
        
        tvTotalBudget.text = "₱${String.format(Locale.getDefault(), "%,.1f", totalBudget)}"
        tvRemaining.text = "₱${String.format(Locale.getDefault(), "%,.1f", totalBudget - totalSpent)}"
    }
}