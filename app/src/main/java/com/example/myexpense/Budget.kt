package com.example.myexpense

data class Budget(
    val id: Int = 0,
    val category: String,
    val amount: String,
    val isSelected: Boolean = false
)