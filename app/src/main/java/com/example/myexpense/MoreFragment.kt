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
        addOption(prefGroup, R.drawable.ic_date, "Date Format", currentFormat, isLast = true) {
            showDateFormatDialog()
        }

        // Logout Button at the bottom
        view.findViewById<Button>(R.id.btn_logout_bottom).setOnClickListener {
            showLogoutDialog()
        }

        return view
    }

    private fun showDateFormatDialog() {
        val formats = arrayOf("MMMM d, yyyy", "dd/MM/yyyy", "MM/dd/yyyy", "yyyy-MM-dd")
        
        AlertDialog.Builder(requireContext())
            .setTitle("Choose Date Format")
            .setItems(formats) { _, which ->
                val selectedFormat = formats[which]
                session.setDateFormat(selectedFormat)
                // Refresh the fragment to show new subtitle
                parentFragmentManager.beginTransaction().replace(R.id.fragment_container, MoreFragment()).commit()
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