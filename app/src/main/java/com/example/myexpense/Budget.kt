package com.example.myexpense

data class Budget(
    val category: String,
    val amount: String,
    val isSelected: Boolean = false
)