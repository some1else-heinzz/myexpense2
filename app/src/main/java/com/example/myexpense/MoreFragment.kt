package com.example.myexpense

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import java.util.Locale

class MoreFragment : Fragment() {

    private lateinit var session: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_more, container, false)
        session = SessionManager(requireContext())

        val accountGroup = view.findViewById<LinearLayout>(R.id.ll_account_group)
        val manageGroup = view.findViewById<LinearLayout>(R.id.ll_manage_group)
        val prefGroup = view.findViewById<LinearLayout>(R.id.ll_preferences_group)
        
        // Ensure utility group exists or use manageGroup if not
        val utilityGroup = view.findViewById<LinearLayout>(R.id.ll_utility_group) ?: manageGroup

        // Account Section
        addOption(accountGroup, R.drawable.ic_person, "Profile", "Edit name and password", isLast = true) {
            startActivity(Intent(requireContext(), ProfileActivity::class.java))
        }

        // Manage Section
        addOption(manageGroup, R.drawable.ic_categories, "Categories", "Manage expense categories") {
            startActivity(Intent(requireContext(), CategoriesActivity::class.java))
        }
        addOption(manageGroup, R.drawable.ic_budget, "Budgets", "View and manage your budgets", isLast = true) {
            (requireActivity() as? DashboardActivity)?.selectTab(R.id.nav_budget)
        }

        // Preferences Section
        val currentFormat = session.getDateFormat()
        addOption(prefGroup, R.drawable.ic_date, "Date Format", currentFormat) {
            showDateFormatDialog()
        }
        
        val currentCurrency = session.getCurrency()
        addOption(prefGroup, R.drawable.ic_currency, "Currency Symbol", "Current: $currentCurrency", isLast = true) {
            showCurrencyDialog()
        }

        // Utility Section (Summary & About)
        addOption(utilityGroup, R.drawable.ic_expenses, "Spending Summary", "Generate a quick report") {
            showSpendingSummary()
        }
        
        addOption(utilityGroup, R.drawable.ic_about, "About MyExpense", "App information & support", isLast = true) {
            showAboutDialog()
        }

        // Logout Button at the bottom
        view.findViewById<Button>(R.id.btn_logout_bottom).setOnClickListener {
            showLogoutDialog()
        }

        return view
    }

    private fun showCurrencyDialog() {
        val currencies = arrayOf("₱ (PHP)", "$ (USD)", "€ (EUR)", "£ (GBP)", "¥ (JPY)")
        val symbols = arrayOf("₱", "$", "€", "£", "¥")
        
        AlertDialog.Builder(requireContext())
            .setTitle("Choose Currency Symbol")
            .setItems(currencies) { _, which ->
                session.setCurrency(symbols[which])
                refreshFragment()
            }
            .show()
    }

    private fun showSpendingSummary() {
        val db = DatabaseHelper(requireContext())
        val userId = session.getUserId()
        val expenses = db.getExpenses(userId)
        val currency = session.getCurrency()
        
        if (expenses.isEmpty()) {
            Toast.makeText(requireContext(), "No data to summarize", Toast.LENGTH_SHORT).show()
            return
        }

        val total = expenses.sumOf { it.amount.replace("₱", "").replace("$", "").replace("€", "").replace("£", "").replace("¥", "").replace(",", "").toDoubleOrNull() ?: 0.0 }
        val topCategory = expenses.groupBy { it.category }.maxByOrNull { it.value.size }?.key ?: "N/A"
        
        val report = """
            --- Spending Report ---
            Total Expenses: $currency${String.format(Locale.getDefault(), "%,.2f", total)}
            Transactions: ${expenses.size}
            Top Category: $topCategory
            -----------------------
            Summary generated successfully.
        """.trimIndent()

        AlertDialog.Builder(requireContext())
            .setTitle("Quick Summary")
            .setMessage(report)
            .setPositiveButton("Close", null)
            .show()
    }

    private fun showAboutDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("About MyExpense")
            .setMessage("MyExpense v1.0\n\nA modern, minimalist budget tracker designed to help you take control of your finances.\n\nDeveloped for Portfolio.")
            .setPositiveButton("OK", null)
            .show()
    }

    private fun refreshFragment() {
        parentFragmentManager.beginTransaction().replace(R.id.fragment_container, MoreFragment()).commit()
    }

    private fun showDateFormatDialog() {
        val formats = arrayOf("MMMM d, yyyy", "dd/MM/yyyy", "MM/dd/yyyy", "yyyy-MM-dd")
        
        AlertDialog.Builder(requireContext())
            .setTitle("Choose Date Format")
            .setItems(formats) { _, which ->
                val selectedFormat = formats[which]
                session.setDateFormat(selectedFormat)
                refreshFragment()
            }
            .show()
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout") { _, _ ->
                session.logout()
                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun addOption(
        parent: LinearLayout,
        iconRes: Int,
        title: String,
        subtitle: String,
        isLast: Boolean = false,
        iconColorRes: Int = R.color.green_primary,
        onClick: (() -> Unit)? = null
    ) {
        val view = layoutInflater.inflate(R.layout.item_more_option, parent, false)
        val ivIcon = view.findViewById<ImageView>(R.id.iv_option_icon)
        ivIcon.setImageResource(iconRes)
        ivIcon.setColorFilter(resources.getColor(iconColorRes, null))

        view.findViewById<TextView>(R.id.tv_option_title).text = title
        view.findViewById<TextView>(R.id.tv_option_subtitle).text = subtitle
        
        if (isLast) {
            view.findViewById<View>(R.id.v_divider).visibility = View.GONE
        }

        view.setOnClickListener {
            onClick?.invoke()
        }
        
        parent.addView(view)
    }
}