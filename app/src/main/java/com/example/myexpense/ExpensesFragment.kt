package com.example.myexpense

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class ExpensesFragment : Fragment() {

    private lateinit var db: DatabaseHelper
    private lateinit var session: SessionManager
    private lateinit var rvExpenses: RecyclerView
    private lateinit var llEmptyState: LinearLayout
    private lateinit var cvExpenseList: View
    private lateinit var chipGroup: ChipGroup

    private var currentFilter: String = "All"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_expenses, container, false)

        db = DatabaseHelper(requireContext())
        session = SessionManager(requireContext())

        rvExpenses = view.findViewById(R.id.rv_expenses)
        llEmptyState = view.findViewById(R.id.ll_empty_state)
        cvExpenseList = view.findViewById(R.id.cv_expense_list)
        chipGroup = view.findViewById(R.id.chip_group_filter)

        rvExpenses.layoutManager = LinearLayoutManager(context)

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

        // Clear existing dynamic chips but keep "All"
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

        if (expenses.isEmpty()) {
            llEmptyState.visibility = View.VISIBLE
            cvExpenseList.visibility = View.GONE
        } else {
            llEmptyState.visibility = View.GONE
            cvExpenseList.visibility = View.VISIBLE
            rvExpenses.adapter = ExpenseAdapter(expenses)
        }
    }

    override fun onResume() {
        super.onResume()
        // Reload categories in case new ones were added
        setupCategoryChips()
        
        // Re-apply filter
        loadExpenses(currentFilter)
    }
}