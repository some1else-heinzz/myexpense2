package com.example.myexpense

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
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
    private lateinit var llSearchBar: View
    private lateinit var etSearch: EditText

    private var currentFilter: String = "All"
    private var searchQuery: String = ""
    
    private val legacyFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
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
        llSearchBar = view.findViewById(R.id.ll_search_bar)
        etSearch = view.findViewById(R.id.et_search)

        setupCategoryChips()
        loadExpenses()

        view.findViewById<ImageView>(R.id.iv_search).setOnClickListener {
            if (llSearchBar.visibility == View.VISIBLE) {
                llSearchBar.visibility = View.GONE
                etSearch.text.clear()
                searchQuery = ""
                loadExpenses()
            } else {
                llSearchBar.visibility = View.VISIBLE
                etSearch.requestFocus()
            }
        }

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchQuery = s.toString()
                loadExpenses()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        view.findViewById<Button>(R.id.btn_add_expense).setOnClickListener {
            startActivity(Intent(requireContext(), AddExpenseActivity::class.java))
        }

        return view
    }

    private fun setupCategoryChips() {
        val userId = session.getUserId()
        val categories = db.getCategories(userId)

        chipGroup.removeAllViews()
        
        val allChip = layoutInflater.inflate(R.layout.layout_chip_item, chipGroup, false) as Chip
        allChip.text = "All"
        allChip.id = View.generateViewId()
        allChip.isChecked = (currentFilter == "All")
        allChip.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                currentFilter = "All"
                loadExpenses()
            }
        }
        chipGroup.addView(allChip)

        for (category in categories) {
            val chip = layoutInflater.inflate(R.layout.layout_chip_item, chipGroup, false) as Chip
            chip.text = category.name
            chip.id = View.generateViewId()
            chip.isChecked = (currentFilter == category.name)
            chip.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    currentFilter = category.name
                    loadExpenses()
                }
            }
            chipGroup.addView(chip)
        }
    }

    private fun loadExpenses() {
        val userId = session.getUserId()
        // Get expenses from DB based on category filter
        val allExpenses = db.getExpenses(userId, if (currentFilter == "All") null else currentFilter)
        
        // Filter by search query locally
        val filteredExpenses = if (searchQuery.isEmpty()) {
            allExpenses
        } else {
            allExpenses.filter { it.name.contains(searchQuery, ignoreCase = true) }
        }

        val categories = db.getCategories(userId).associateBy { it.name }
        val targetFormat = SimpleDateFormat(session.getDateFormat(), Locale.getDefault())

        llMonthCardsContainer.removeAllViews()

        if (filteredExpenses.isEmpty()) {
            llEmptyState.visibility = View.VISIBLE
            nsvExpenseList.visibility = View.GONE
            val tvEmptyDesc = llEmptyState.findViewById<TextView>(R.id.tv_empty_desc)
            if (searchQuery.isNotEmpty()) {
                tvEmptyDesc.text = "No expenses match \"$searchQuery\""
            } else {
                tvEmptyDesc.text = "Start adding your first expense to track your spending."
            }
        } else {
            llEmptyState.visibility = View.GONE
            nsvExpenseList.visibility = View.VISIBLE

            val groupedExpenses = filteredExpenses.groupBy { expense ->
                try {
                    val date = legacyFormat.parse(expense.date)
                    if (date != null) monthYearFormat.format(date) else "Unknown Date"
                } catch (e: Exception) {
                    "Unknown Date"
                }
            }

            for ((monthYear, monthExpenses) in groupedExpenses) {
                val monthCardView = layoutInflater.inflate(R.layout.item_month_card, llMonthCardsContainer, false)
                val tvMonthHeader = monthCardView.findViewById<TextView>(R.id.tv_month_header)
                val tvMonthTotal = monthCardView.findViewById<TextView>(R.id.tv_month_total)
                val llItemsContainer = monthCardView.findViewById<LinearLayout>(R.id.ll_expense_items_container)

                tvMonthHeader.text = monthYear
                
                var totalAmount = 0.0
                monthExpenses.forEachIndexed { index, expense ->
                    val amount = expense.amount.replace("₱", "").replace(",", "").toDoubleOrNull() ?: 0.0
                    totalAmount += amount

                    val rowView = layoutInflater.inflate(R.layout.item_expense, llItemsContainer, false)
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
        loadExpenses()
    }
}