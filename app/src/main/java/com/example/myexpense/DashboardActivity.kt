package com.example.myexpense

import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class DashboardActivity : AppCompatActivity() {

    private val dashboardFragment = DashboardFragment()
    private val expensesFragment = ExpensesFragment()
    private val budgetFragment = BudgetFragment()
    private val moreFragment = MoreFragment()
    private var activeFragment: Fragment = dashboardFragment
    private lateinit var bottomNav: BottomNavigationView

    private var backPressedTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNav = findViewById(R.id.bottom_nav)

        // Pre-add fragments and hide others to make switching instantaneous
        supportFragmentManager.beginTransaction().add(R.id.fragment_container, moreFragment, "4").hide(moreFragment).commit()
        supportFragmentManager.beginTransaction().add(R.id.fragment_container, budgetFragment, "3").hide(budgetFragment).commit()
        supportFragmentManager.beginTransaction().add(R.id.fragment_container, expensesFragment, "2").hide(expensesFragment).commit()
        supportFragmentManager.beginTransaction().add(R.id.fragment_container, dashboardFragment, "1").commit()

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    switchFragment(dashboardFragment)
                    true
                }
                R.id.nav_expenses -> {
                    switchFragment(expensesFragment)
                    true
                }
                R.id.nav_budget -> {
                    switchFragment(budgetFragment)
                    true
                }
                R.id.nav_more -> {
                    switchFragment(moreFragment)
                    true
                }
                else -> false
            }
        }

        // Handle Back Press
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (bottomNav.selectedItemId != R.id.nav_home) {
                    // If not on Home, go back to Home
                    bottomNav.selectedItemId = R.id.nav_home
                } else {
                    // If already on Home, check for double tap to exit
                    if (backPressedTime + 2000 > System.currentTimeMillis()) {
                        finish()
                    } else {
                        Toast.makeText(baseContext, "Tap again to exit", Toast.LENGTH_SHORT).show()
                    }
                    backPressedTime = System.currentTimeMillis()
                }
            }
        })
    }

    private fun switchFragment(fragment: Fragment) {
        if (activeFragment == fragment) return
        supportFragmentManager.beginTransaction().hide(activeFragment).show(fragment).commit()
        activeFragment = fragment
    }

    fun selectTab(itemId: Int) {
        bottomNav.selectedItemId = itemId
    }
}