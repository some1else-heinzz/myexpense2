package com.example.myexpense

data class Expense(
    val id: Int = 0,
    val name: String,
    val date: String,
    val category: String,
    val amount: String,
    val notes: String = ""
)