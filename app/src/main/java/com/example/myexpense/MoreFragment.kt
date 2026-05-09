package com.example.myexpense

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment

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
        val supportGroup = view.findViewById<LinearLayout>(R.id.ll_support_group)

        // Account Section
        addOption(accountGroup, R.drawable.ic_person, "Profile", "Edit name and password") {
            startActivity(Intent(requireContext(), ProfileActivity::class.java))
        }
        addOption(accountGroup, R.drawable.ic_logout, "Logout", "Sign out of your account", isLast = true) {
            showLogoutDialog()
        }

        // Manage Section
        addOption(manageGroup, R.drawable.ic_categories, "Categories", "Manage expense categories") {
            startActivity(Intent(requireContext(), CategoriesActivity::class.java))
        }
        addOption(manageGroup, R.drawable.ic_payment, "Payment Methods", "Manage your payment methods")
        addOption(manageGroup, R.drawable.ic_budget, "Budgets", "View and manage your budgets", isLast = true)

        // Preferences Section
        addOption(prefGroup, R.drawable.ic_currency, "Currency", "USD - US Dollar")
        addOption(prefGroup, R.drawable.ic_date, "Date Format", "MM/DD/YYYY")
        addOption(prefGroup, R.drawable.ic_theme, "Theme", "Light", isLast = true)

        // Support Section
        addOption(supportGroup, R.drawable.ic_help, "Help & FAQ", "Get help and find answers")
        addOption(supportGroup, R.drawable.ic_contact, "Contact Us", "We'd love to hear from you")
        addOption(supportGroup, R.drawable.ic_about, "About", "Version 1.0.0", isLast = true)

        return view
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
        onClick: (() -> Unit)? = null
    ) {
        val view = layoutInflater.inflate(R.layout.item_more_option, parent, false)
        view.findViewById<ImageView>(R.id.iv_option_icon).setImageResource(iconRes)
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