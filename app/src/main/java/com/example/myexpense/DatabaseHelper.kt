package com.example.myexpense

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "MyExpense.db"
        private const val DATABASE_VERSION = 2

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

        // Categories table
        private const val TABLE_CATEGORIES = "categories"
        private const val COLUMN_CAT_ID = "id"
        private const val COLUMN_CAT_USER_ID = "user_id"
        private const val COLUMN_CAT_NAME = "name"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE $TABLE_USERS($COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_USERNAME TEXT UNIQUE, $COLUMN_FULLNAME TEXT, $COLUMN_PASSWORD TEXT)")
        db.execSQL("CREATE TABLE $TABLE_EXPENSES($COLUMN_EXP_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_EXP_USER_ID INTEGER, $COLUMN_EXP_NAME TEXT, $COLUMN_EXP_DATE TEXT, $COLUMN_EXP_CATEGORY TEXT, $COLUMN_EXP_AMOUNT TEXT)")
        db.execSQL("CREATE TABLE $TABLE_BUDGETS($COLUMN_BUD_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_BUD_USER_ID INTEGER, $COLUMN_BUD_CATEGORY TEXT, $COLUMN_BUD_AMOUNT TEXT)")
        db.execSQL("CREATE TABLE $TABLE_CATEGORIES($COLUMN_CAT_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_CAT_USER_ID INTEGER, $COLUMN_CAT_NAME TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("CREATE TABLE $TABLE_CATEGORIES($COLUMN_CAT_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_CAT_USER_ID INTEGER, $COLUMN_CAT_NAME TEXT)")
        }
    }

    // User Operations
    fun addUser(user: User): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_USERNAME, user.username)
        values.put(COLUMN_FULLNAME, user.fullName)
        values.put(COLUMN_PASSWORD, user.password)
        val userId = db.insert(TABLE_USERS, null, values)
        if (userId != -1L) {
            addDefaultCategories(db, userId.toInt())
            return true
        }
        return false
    }

    private fun addDefaultCategories(db: SQLiteDatabase, userId: Int) {
        val defaults = listOf("Food", "Transportation", "Payments", "Education", "Entertainment", "Others")
        for (cat in defaults) {
            val values = ContentValues()
            values.put(COLUMN_CAT_USER_ID, userId)
            values.put(COLUMN_CAT_NAME, cat)
            db.insert(TABLE_CATEGORIES, null, values)
        }
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

    fun updateUser(user: User): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_FULLNAME, user.fullName)
        values.put(COLUMN_PASSWORD, user.password)
        val success = db.update(TABLE_USERS, values, "$COLUMN_USER_ID=?", arrayOf(user.id.toString()))
        db.close()
        return success > 0
    }

    // Category Operations
    fun addCategory(userId: Int, name: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_CAT_USER_ID, userId)
        values.put(COLUMN_CAT_NAME, name)
        val success = db.insert(TABLE_CATEGORIES, null, values)
        db.close()
        return success != -1L
    }

    fun getCategories(userId: Int): List<Category> {
        val list = mutableListOf<Category>()
        val db = this.readableDatabase
        val cursor = db.query(TABLE_CATEGORIES, null, "$COLUMN_CAT_USER_ID=?", arrayOf(userId.toString()), null, null, "$COLUMN_CAT_NAME ASC")
        if (cursor.moveToFirst()) {
            do {
                list.add(Category(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CAT_NAME))))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    fun updateCategory(userId: Int, oldName: String, newName: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_CAT_NAME, newName)
        val success = db.update(TABLE_CATEGORIES, values, "$COLUMN_CAT_USER_ID=? AND $COLUMN_CAT_NAME=?", arrayOf(userId.toString(), oldName))
        
        // Also update expenses with this category
        val expValues = ContentValues()
        expValues.put(COLUMN_EXP_CATEGORY, newName)
        db.update(TABLE_EXPENSES, expValues, "$COLUMN_EXP_USER_ID=? AND $COLUMN_EXP_CATEGORY=?", arrayOf(userId.toString(), oldName))
        
        db.close()
        return success > 0
    }

    fun deleteCategory(userId: Int, name: String): Boolean {
        val db = this.writableDatabase
        val success = db.delete(TABLE_CATEGORIES, "$COLUMN_CAT_USER_ID=? AND $COLUMN_CAT_NAME=?", arrayOf(userId.toString(), name))
        db.close()
        return success > 0
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

    fun updateExpense(userId: Int, expense: Expense): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_EXP_NAME, expense.name)
        values.put(COLUMN_EXP_CATEGORY, expense.category)
        values.put(COLUMN_EXP_AMOUNT, expense.amount)
        val success = db.update(TABLE_EXPENSES, values, "$COLUMN_EXP_ID=? AND $COLUMN_EXP_USER_ID=?", arrayOf(expense.id.toString(), userId.toString()))
        db.close()
        return success > 0
    }

    fun deleteExpense(userId: Int, expenseId: Int): Boolean {
        val db = this.writableDatabase
        val success = db.delete(TABLE_EXPENSES, "$COLUMN_EXP_ID=? AND $COLUMN_EXP_USER_ID=?", arrayOf(expenseId.toString(), userId.toString()))
        db.close()
        return success > 0
    }

    fun getExpenses(userId: Int, category: String? = null): List<Expense> {
        val list = mutableListOf<Expense>()
        val db = this.readableDatabase
        val selection = if (category == null || category == "All") "$COLUMN_EXP_USER_ID=?" else "$COLUMN_EXP_USER_ID=? AND $COLUMN_EXP_CATEGORY=?"
        val selectionArgs = if (category == null || category == "All") arrayOf(userId.toString()) else arrayOf(userId.toString(), category)
        
        val cursor = db.query(TABLE_EXPENSES, null, selection, selectionArgs, null, null, "$COLUMN_EXP_ID DESC")
        if (cursor.moveToFirst()) {
            do {
                list.add(Expense(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_EXP_ID)),
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