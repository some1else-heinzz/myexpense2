// OnboardingActivity.kt
package com.example.myexpense

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class OnboardingActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var btnNext: Button
    private lateinit var tvSkip: TextView
    private lateinit var dotsLayout: LinearLayout
    private val dots = mutableListOf<ImageView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        viewPager = findViewById(R.id.viewpager_onboarding)
        btnNext = findViewById(R.id.btn_next)
        tvSkip = findViewById(R.id.tv_skip)
        dotsLayout = findViewById(R.id.ll_dots)

        val pages = listOf(
            Triple(R.drawable.get_started_1, "Track Your Expenses Daily",
                "Add and organize your expenses in seconds. Keep everything simple and clear."),
            Triple(R.drawable.get_started_2, "Set Budgets That Work",
                "Create budgets for different categories and manage your spending within limits"),
            Triple(R.drawable.get_started_3, "Start Your Journey to Better Finances",
                "Simple tools help you spend smarter and achieve your financial goals")
        )

        val adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = pages.size
            override fun createFragment(position: Int): Fragment {
                val (img, title, desc) = pages[position]
                return OnboardFragment.newInstance(img, title, desc)
            }
        }
        viewPager.adapter = adapter

        setupDots(pages.size)
        updateDots(0)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateDots(position)
                if (position == pages.size - 1) {
                    btnNext.text = "Let's Go!"
                } else {
                    btnNext.text = "Next"
                }
            }
        })

        btnNext.setOnClickListener {
            if (viewPager.currentItem < pages.size - 1) {
                viewPager.currentItem += 1
            } else {
                goToSignUp()
            }
        }

        tvSkip.setOnClickListener { goToSignUp() }
    }

    private fun setupDots(count: Int) {
        dots.clear()
        dotsLayout.removeAllViews()
        for (i in 0 until count) {
            val dot = ImageView(this)
            dot.layoutParams = LinearLayout.LayoutParams(16, 16).apply {
                setMargins(6, 0, 6, 0)
            }
            dots.add(dot)
            dotsLayout.addView(dot)
        }
    }

    private fun updateDots(position: Int) {
        for (i in dots.indices) {
            dots[i].setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    if (i == position) R.drawable.dot_active else R.drawable.dot_inactive
                )
            )
        }
    }

    private fun goToSignUp() {
        startActivity(Intent(this, SignUpActivity::class.java))
        finish()
    }
}