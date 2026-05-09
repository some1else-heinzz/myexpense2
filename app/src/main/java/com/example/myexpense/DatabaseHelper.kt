package com.example.myexpense

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "MyExpense.db"
        private const val DATABASE_VERSION = 1

        // Users table
        private const val TABLE_USERS = "users"
        private const val COLUMN_USER_ID = "id"
        private const val COLUMN_USERNAME = "username"
        private const val COLUMN_FULLNAME = "fullname"
        private const val COLUMN_PASSWORD = "password"

        // Expenses table
        private const val TABLE_EXPENSES = "expenses"
        private const val COLUMN_EXP_ID = "id"
        private const val COLUMN_EXP_USER_ID = "user_id"
        private const val COLUMN_EXP_NAME = "name"
        private const val COLUMN_EXP_DATE = "date"
        private const val COLUMN_EXP_CATEGORY = "category"
        private const val COLUMN_EXP_AMOUNT = "amount"

        // Budgets table
        private const val TABLE_BUDGETS = "budgets"
        private const val COLUMN_BUD_ID = "id"
        private const val COLUMN_BUD_USER_ID = "user_id"
        private const val COLUMN_BUD_CATEGORY = "category"
        private const val COLUMN_BUD_AMOUNT = "amount"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createUsersTable = ("CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USERNAME + " TEXT UNIQUE,"
                + COLUMN_FULLNAME + " TEXT,"
                + COLUMN_PASSWORD + " TEXT" + ")")
        
        val createExpensesTable = ("CREATE TABLE " + TABLE_EXPENSES + "("
                + COLUMN_EXP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_EXP_USER_ID + " INTEGER,"
                + COLUMN_EXP_NAME + " TEXT,"
                + COLUMN_EXP_DATE + " TEXT,"
                + COLUMN_EXP_CATEGORY + " TEXT,"
                + COLUMN_EXP_AMOUNT + " TEXT" + ")")

        val createBudgetsTable = ("CREATE TABLE " + TABLE_BUDGETS + "("
                + COLUMN_BUD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_BUD_USER_ID + " INTEGER,"
                + COLUMN_BUD_CATEGORY + " TEXT,"
                + COLUMN_BUD_AMOUNT + " TEXT" + ")")

        db.execSQL(createUsersTable)
        db.execSQL(createExpensesTable)
        db.execSQL(createBudgetsTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_EXPENSES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_BUDGETS")
        onCreate(db)
    }

    // User Operations
    fun addUser(user: User): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_USERNAME, user.username)
        values.put(COLUMN_FULLNAME, user.fullName)
        values.put(COLUMN_PASSWORD, user.password)
        val success = db.insert(TABLE_USERS, null, values)
        db.close()
        return success != -1L
    }

    fun checkUser(username: String, password: String): User? {
        val db = this.readableDatabase
        val cursor = db.query(TABLE_USERS, null, "$COLUMN_USERNAME=? AND $COLUMN_PASSWORD=?", arrayOf(username, password), null, null, null)
        var user: User? = null
        if (cursor.moveToFirst()) {
            user = User(
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FULLNAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD))
            )
        }
        cursor.close()
        return user
    }

    // Expense Operations
    fun addExpense(userId: Int, expense: Expense): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_EXP_USER_ID, userId)
        values.put(COLUMN_EXP_NAME, expense.name)
        values.put(COLUMN_EXP_DATE, expense.date)
        values.put(COLUMN_EXP_CATEGORY, expense.category)
        values.put(COLUMN_EXP_AMOUNT, expense.amount)
        val success = db.insert(TABLE_EXPENSES, null, values)
        db.close()
        return success != -1L
    }

    fun getExpenses(userId: Int): List<Expense> {
        val list = mutableListOf<Expense>()
        val db = this.readableDatabase
        val cursor = db.query(TABLE_EXPENSES, null, "$COLUMN_EXP_USER_ID=?", arrayOf(userId.toString()), null, null, "$COLUMN_EXP_ID DESC")
        if (cursor.moveToFirst()) {
            do {
                list.add(Expense(
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXP_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXP_DATE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXP_CATEGORY)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXP_AMOUNT))
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    // Budget Operations
    fun addBudget(userId: Int, budget: Budget): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_BUD_USER_ID, userId)
        values.put(COLUMN_BUD_CATEGORY, budget.category)
        values.put(COLUMN_BUD_AMOUNT, budget.amount)
        val success = db.insert(TABLE_BUDGETS, null, values)
        db.close()
        return success != -1L
    }

    fun getBudgets(userId: Int): List<Budget> {
        val list = mutableListOf<Budget>()
        val db = this.readableDatabase
        val cursor = db.query(TABLE_BUDGETS, null, "$COLUMN_BUD_USER_ID=?", arrayOf(userId.toString()), null, null, null)
        if (cursor.moveToFirst()) {
            do {
                list.add(Budget(
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BUD_CATEGORY)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BUD_AMOUNT))
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }
}