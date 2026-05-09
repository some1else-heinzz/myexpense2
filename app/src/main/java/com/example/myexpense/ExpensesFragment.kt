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
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import java.text.SimpleDateFormat
import java.util.Locale

class ExpensesFragment : Fragment() {

    private lateinit var db: DatabaseHelper
    private lateinit var session: SessionManager
    private lateinit var llMonthCardsContainer: LinearLayout
    private lateinit var llEmptyState: LinearLayout
    private lateinit var nsvExpenseList: View
    private lateinit var chipGroup: ChipGroup

    private var currentFilter: String = "All"
    private val inputDateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
    private val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_expenses, container, false)

        db = DatabaseHelper(requireContext())
        session = SessionManager(requireContext())

        llMonthCardsContainer = view.findViewById(R.id.ll_month_cards_container)
        llEmptyState = view.findViewById(R.id.ll_empty_state)
        nsvExpenseList = view.findViewById(R.id.nsv_expense_list)
        chipGroup = view.findViewById(R.id.chip_group_filter)

        setupCategoryChips()
        loadExpenses(currentFilter)

        view.findViewById<Button>(R.id.btn_add_expense).setOnClickListener {
            startActivity(Intent(requireContext(), AddExpenseActivity::class.java))
        }

        return view
    }

    private fun setupCategoryChips() {
        val userId = session.getUserId()
        val categories = db.getCategories(userId)

        val allChip = chipGroup.findViewById<Chip>(R.id.chip_all)
        chipGroup.removeAllViews()
        chipGroup.addView(allChip)

        for (category in categories) {
            val chip = layoutInflater.inflate(R.layout.layout_chip_item, chipGroup, false) as Chip
            chip.text = category.name
            chip.id = View.generateViewId()
            chip.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    currentFilter = category.name
                    loadExpenses(currentFilter)
                }
            }
            chipGroup.addView(chip)
        }

        allChip.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                currentFilter = "All"
                loadExpenses(currentFilter)
            }
        }
    }

    private fun loadExpenses(category: String) {
        val userId = session.getUserId()
        val expenses = db.getExpenses(userId, if (category == "All") null else category)

        llMonthCardsContainer.removeAllViews()

        if (expenses.isEmpty()) {
            llEmptyState.visibility = View.VISIBLE
            nsvExpenseList.visibility = View.GONE
        } else {
            llEmptyState.visibility = View.GONE
            nsvExpenseList.visibility = View.VISIBLE

            // Group expenses by Month Year
            val groupedExpenses = expenses.groupBy { expense ->
                try {
                    val date = inputDateFormat.parse(expense.date)
                    if (date != null) monthYearFormat.format(date) else "Unknown Date"
                } catch (e: Exception) {
                    "Unknown Date"
                }
            }

            // Create a card for each month
            for ((monthYear, monthExpenses) in groupedExpenses) {
                val monthCardView = layoutInflater.inflate(R.layout.item_month_card, llMonthCardsContainer, false)
                val tvMonthHeader = monthCardView.findViewById<TextView>(R.id.tv_month_header)
                val tvMonthTotal = monthCardView.findViewById<TextView>(R.id.tv_month_total)
                val llItemsContainer = monthCardView.findViewById<LinearLayout>(R.id.ll_expense_items_container)

                tvMonthHeader.text = monthYear
                
                var totalAmount = 0.0
                monthExpenses.forEachIndexed { index, expense ->
                    // Calculate total
                    val amount = expense.amount.replace("₱", "").replace(",", "").toDoubleOrNull() ?: 0.0
                    totalAmount += amount

                    // Inflate individual expense row
                    val rowView = layoutInflater.inflate(R.layout.item_expense, llItemsContainer, false)
                    rowView.findViewById<TextView>(R.id.tv_expense_name).text = expense.name
                    rowView.findViewById<TextView>(R.id.tv_expense_date).text = expense.date
                    rowView.findViewById<TextView>(R.id.tv_expense_category).text = expense.category
                    rowView.findViewById<TextView>(R.id.tv_expense_amount).text = expense.amount
                    
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

                    // Divider visibility
                    if (index == monthExpenses.size - 1) {
                        rowView.findViewById<View>(R.id.v_divider).visibility = View.GONE
                    }

                    llItemsContainer.addView(rowView)
                }

                tvMonthTotal.text = "Total: ₱${String.format(Locale.getDefault(), "%,.2f", totalAmount)}"
                llMonthCardsContainer.addView(monthCardView)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setupCategoryChips()
        loadExpenses(currentFilter)
    }
}