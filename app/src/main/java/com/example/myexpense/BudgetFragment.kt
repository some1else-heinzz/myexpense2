package com.example.myexpense

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class BudgetFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_budget, container, false)

        val rvBudgets = view.findViewById<RecyclerView>(R.id.rv_budgets)
        rvBudgets.layoutManager = LinearLayoutManager(context)

        val budgets = listOf(
            Budget("Food", "P2,500.00"),
            Budget("Transportation", "P500.00"),
            Budget("Payment", "P2,000.00"),
            Budget("Miscellaneous", "P00.00")
        )

        rvBudgets.adapter = BudgetAdapter(budgets)

        view.findViewById<ImageView>(R.id.iv_add_budget).setOnClickListener {
            startActivity(Intent(requireContext(), AddBudgetActivity::class.java))
        }

        return view
    }
}