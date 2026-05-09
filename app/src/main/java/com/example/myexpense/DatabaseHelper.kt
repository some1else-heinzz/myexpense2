package com.example.myexpense

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "MyExpense.db"
        private const val DATABASE_VERSION = 8

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
        private const val COLUMN_EXP_NOTES = "notes"

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
        private const val COLUMN_CAT_ICON = "icon"
        private const val COLUMN_CAT_COLOR = "color"

        // Notifications table
        private const val TABLE_NOTIFICATIONS = "notifications"
        private const val COLUMN_NOTIF_ID = "id"
        private const val COLUMN_NOTIF_USER_ID = "user_id"
        private const val COLUMN_NOTIF_TITLE = "title"
        private const val COLUMN_NOTIF_MESSAGE = "message"
        private const val COLUMN_NOTIF_DATE = "date"

        // Templates table (Favorites)
        private const val TABLE_TEMPLATES = "templates"
        private const val COLUMN_TEMP_ID = "id"
        private const val COLUMN_TEMP_USER_ID = "user_id"
        private const val COLUMN_TEMP_NAME = "name"
        private const val COLUMN_TEMP_CATEGORY = "category"
        private const val COLUMN_TEMP_AMOUNT = "amount"

        // Recurring table
        private const val TABLE_RECURRING = "recurring"
        private const val COLUMN_REC_ID = "id"
        private const val COLUMN_REC_USER_ID = "user_id"
        private const val COLUMN_REC_NAME = "name"
        private const val COLUMN_REC_CATEGORY = "category"
        private const val COLUMN_REC_AMOUNT = "amount"
        private const val COLUMN_REC_LAST_ADDED_MONTH = "last_added_month" // Format: "MMMM yyyy"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE $TABLE_USERS($COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_USERNAME TEXT UNIQUE, $COLUMN_FULLNAME TEXT, $COLUMN_PASSWORD TEXT)")
        db.execSQL("CREATE TABLE $TABLE_EXPENSES($COLUMN_EXP_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_EXP_USER_ID INTEGER, $COLUMN_EXP_NAME TEXT, $COLUMN_EXP_DATE TEXT, $COLUMN_EXP_CATEGORY TEXT, $COLUMN_EXP_AMOUNT TEXT, $COLUMN_EXP_NOTES TEXT)")
        db.execSQL("CREATE TABLE $TABLE_BUDGETS($COLUMN_BUD_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_BUD_USER_ID INTEGER, $COLUMN_BUD_CATEGORY TEXT, $COLUMN_BUD_AMOUNT TEXT)")
        db.execSQL("CREATE TABLE $TABLE_CATEGORIES($COLUMN_CAT_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_CAT_USER_ID INTEGER, $COLUMN_CAT_NAME TEXT, $COLUMN_CAT_ICON TEXT, $COLUMN_CAT_COLOR TEXT)")
        db.execSQL("CREATE TABLE $TABLE_NOTIFICATIONS($COLUMN_NOTIF_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_NOTIF_USER_ID INTEGER, $COLUMN_NOTIF_TITLE TEXT, $COLUMN_NOTIF_MESSAGE TEXT, $COLUMN_NOTIF_DATE TEXT)")
        db.execSQL("CREATE TABLE $TABLE_TEMPLATES($COLUMN_TEMP_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_TEMP_USER_ID INTEGER, $COLUMN_TEMP_NAME TEXT, $COLUMN_TEMP_CATEGORY TEXT, $COLUMN_TEMP_AMOUNT TEXT)")
        db.execSQL("CREATE TABLE $TABLE_RECURRING($COLUMN_REC_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_REC_USER_ID INTEGER, $COLUMN_REC_NAME TEXT, $COLUMN_REC_CATEGORY TEXT, $COLUMN_REC_AMOUNT TEXT, $COLUMN_REC_LAST_ADDED_MONTH TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("CREATE TABLE IF NOT EXISTS $TABLE_CATEGORIES($COLUMN_CAT_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_CAT_USER_ID INTEGER, $COLUMN_CAT_NAME TEXT, $COLUMN_CAT_ICON TEXT, $COLUMN_CAT_COLOR TEXT)")
        }
        
        addColumnIfNotExists(db, TABLE_EXPENSES, COLUMN_EXP_NOTES, "TEXT DEFAULT ''")
        addColumnIfNotExists(db, TABLE_CATEGORIES, COLUMN_CAT_ICON, "TEXT DEFAULT 'ic_others'")
        addColumnIfNotExists(db, TABLE_CATEGORIES, COLUMN_CAT_COLOR, "TEXT DEFAULT '#757575'")
        
        if (oldVersion < 6) {
            db.execSQL("CREATE TABLE IF NOT EXISTS $TABLE_NOTIFICATIONS($COLUMN_NOTIF_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_NOTIF_USER_ID INTEGER, $COLUMN_NOTIF_TITLE TEXT, $COLUMN_NOTIF_MESSAGE TEXT, $COLUMN_NOTIF_DATE TEXT)")
        }

        if (oldVersion < 7) {
            db.execSQL("CREATE TABLE IF NOT EXISTS $TABLE_TEMPLATES($COLUMN_TEMP_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_TEMP_USER_ID INTEGER, $COLUMN_TEMP_NAME TEXT, $COLUMN_TEMP_CATEGORY TEXT, $COLUMN_TEMP_AMOUNT TEXT)")
        }

        if (oldVersion < 8) {
            db.execSQL("CREATE TABLE IF NOT EXISTS $TABLE_RECURRING($COLUMN_REC_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_REC_USER_ID INTEGER, $COLUMN_REC_NAME TEXT, $COLUMN_REC_CATEGORY TEXT, $COLUMN_REC_AMOUNT TEXT, $COLUMN_REC_LAST_ADDED_MONTH TEXT)")
        }
    }

    private fun addColumnIfNotExists(db: SQLiteDatabase, tableName: String, columnName: String, columnDef: String) {
        val cursor = db.rawQuery("PRAGMA table_info($tableName)", null)
        var exists = false
        while (cursor.moveToNext()) {
            if (cursor.getString(1) == columnName) {
                exists = true
                break
            }
        }
        cursor.close()
        if (!exists) {
            db.execSQL("ALTER TABLE $tableName ADD COLUMN $columnName $columnDef")
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
            addNotification(userId.toInt(), "Welcome!", "Welcome to MyExpense, ${user.fullName}!")
            return true
        }
        return false
    }

    private fun addDefaultCategories(db: SQLiteDatabase, userId: Int) {
        val defaults = listOf(
            Triple("Food", "ic_food", "#4CAF50"),
            Triple("Transportation", "ic_transport", "#FF9800"),
            Triple("Payments", "ic_payment", "#2196F3"),
            Triple("Education", "ic_education", "#3F51B5"),
            Triple("Entertainment", "ic_entertainment", "#E91E63"),
            Triple("Others", "ic_others", "#9E9E9E")
        )
        for (cat in defaults) {
            val values = ContentValues()
            values.put(COLUMN_CAT_USER_ID, userId)
            values.put(COLUMN_CAT_NAME, cat.first)
            values.put(COLUMN_CAT_ICON, cat.second)
            values.put(COLUMN_CAT_COLOR, cat.third)
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
        if (success > 0) {
            addNotification(user.id, "Profile Updated", "Your profile details have been updated.")
        }
        db.close()
        return success > 0
    }

    // Category Operations
    fun addCategory(userId: Int, category: Category): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_CAT_USER_ID, userId)
        values.put(COLUMN_CAT_NAME, category.name)
        values.put(COLUMN_CAT_ICON, category.iconResName)
        values.put(COLUMN_CAT_COLOR, category.colorHex)
        val success = db.insert(TABLE_CATEGORIES, null, values)
        if (success != -1L) {
            addNotification(userId, "New Category", "Category '${category.name}' has been added.")
        }
        db.close()
        return success != -1L
    }

    fun getCategories(userId: Int): List<Category> {
        val list = mutableListOf<Category>()
        val db = this.readableDatabase
        val cursor = db.query(TABLE_CATEGORIES, null, "$COLUMN_CAT_USER_ID=?", arrayOf(userId.toString()), null, null, "$COLUMN_CAT_NAME ASC")
        
        val nameIdx = cursor.getColumnIndex(COLUMN_CAT_NAME)
        val iconIdx = cursor.getColumnIndex(COLUMN_CAT_ICON)
        val colorIdx = cursor.getColumnIndex(COLUMN_CAT_COLOR)

        if (cursor.moveToFirst()) {
            do {
                val name = if (nameIdx != -1) cursor.getString(nameIdx) else "Unknown"
                val icon = if (iconIdx != -1) cursor.getString(iconIdx) else "ic_others"
                val color = if (colorIdx != -1) cursor.getString(colorIdx) else "#757575"
                list.add(Category(name, icon, color))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    fun updateCategory(userId: Int, oldName: String, category: Category): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_CAT_NAME, category.name)
        values.put(COLUMN_CAT_ICON, category.iconResName)
        values.put(COLUMN_CAT_COLOR, category.colorHex)
        val success = db.update(TABLE_CATEGORIES, values, "$COLUMN_CAT_USER_ID=? AND $COLUMN_CAT_NAME=?", arrayOf(userId.toString(), oldName))
        
        if (success > 0) {
            addNotification(userId, "Category Updated", "Category '$oldName' updated to '${category.name}'.")
            if (oldName != category.name) {
                val expValues = ContentValues()
                expValues.put(COLUMN_EXP_CATEGORY, category.name)
                db.update(TABLE_EXPENSES, expValues, "$COLUMN_EXP_USER_ID=? AND $COLUMN_EXP_CATEGORY=?", arrayOf(userId.toString(), oldName))
            }
        }
        
        db.close()
        return success > 0
    }

    fun deleteCategory(userId: Int, name: String): Boolean {
        val db = this.writableDatabase
        val success = db.delete(TABLE_CATEGORIES, "$COLUMN_CAT_USER_ID=? AND $COLUMN_CAT_NAME=?", arrayOf(userId.toString(), name))
        if (success > 0) {
            addNotification(userId, "Category Deleted", "Category '$name' has been deleted.")
        }
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
        values.put(COLUMN_EXP_NOTES, expense.notes)
        val success = db.insert(TABLE_EXPENSES, null, values)
        if (success != -1L) {
            addNotification(userId, "New Expense", "Added '${expense.name}' (${expense.amount}) to ${expense.category}.")
            checkBudgetAlert(userId, expense.category)
        }
        db.close()
        return success != -1L
    }

    private fun checkBudgetAlert(userId: Int, category: String) {
        val budget = getBudgets(userId).find { it.category == category } ?: return
        val budgetAmount = budget.amount.replace("₱", "").replace("P", "").replace(",", "").toDoubleOrNull() ?: 0.0
        if (budgetAmount <= 0) return

        val currentMonth = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Calendar.getInstance().time)
        val allExpenses = getExpenses(userId, category)
        val monthSpent = allExpenses.filter { it.date.contains(currentMonth.split(" ")[0]) && it.date.contains(currentMonth.split(" ")[1]) }
            .sumOf { it.amount.replace("₱", "").replace(",", "").toDoubleOrNull() ?: 0.0 }

        val ratio = monthSpent / budgetAmount
        if (ratio >= 1.0) {
            addNotification(userId, "Budget Exceeded!", "You have reached 100% of your $category budget!")
        } else if (ratio >= 0.8) {
            addNotification(userId, "Budget Warning", "You have reached 80% of your $category budget.")
        }
    }

    fun updateExpense(userId: Int, expense: Expense): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_EXP_NAME, expense.name)
        values.put(COLUMN_EXP_DATE, expense.date)
        values.put(COLUMN_EXP_CATEGORY, expense.category)
        values.put(COLUMN_EXP_AMOUNT, expense.amount)
        values.put(COLUMN_EXP_NOTES, expense.notes)
        val success = db.update(TABLE_EXPENSES, values, "$COLUMN_EXP_ID=? AND $COLUMN_EXP_USER_ID=?", arrayOf(expense.id.toString(), userId.toString()))
        if (success > 0) {
            addNotification(userId, "Expense Updated", "Updated '${expense.name}'.")
            checkBudgetAlert(userId, expense.category)
        }
        db.close()
        return success > 0
    }

    fun deleteExpense(userId: Int, expenseId: Int): Boolean {
        val db = this.writableDatabase
        val success = db.delete(TABLE_EXPENSES, "$COLUMN_EXP_ID=? AND $COLUMN_EXP_USER_ID=?", arrayOf(expenseId.toString(), userId.toString()))
        if (success > 0) {
            addNotification(userId, "Expense Deleted", "An expense has been removed.")
        }
        db.close()
        return success > 0
    }

    fun getExpenses(userId: Int, category: String? = null): List<Expense> {
        val list = mutableListOf<Expense>()
        val db = this.readableDatabase
        val selection = if (category == null || category == "All") "$COLUMN_EXP_USER_ID=?" else "$COLUMN_EXP_USER_ID=? AND $COLUMN_EXP_CATEGORY=?"
        val selectionArgs = if (category == null || category == "All") arrayOf(userId.toString()) else arrayOf(userId.toString(), category)
        
        val cursor = db.query(TABLE_EXPENSES, null, selection, selectionArgs, null, null, "$COLUMN_EXP_ID DESC")
        
        val idIdx = cursor.getColumnIndex(COLUMN_EXP_ID)
        val nameIdx = cursor.getColumnIndex(COLUMN_EXP_NAME)
        val dateIdx = cursor.getColumnIndex(COLUMN_EXP_DATE)
        val catIdx = cursor.getColumnIndex(COLUMN_EXP_CATEGORY)
        val amtIdx = cursor.getColumnIndex(COLUMN_EXP_AMOUNT)
        val notesIdx = cursor.getColumnIndex(COLUMN_EXP_NOTES)

        if (cursor.moveToFirst()) {
            do {
                list.add(Expense(
                    if (idIdx != -1) cursor.getInt(idIdx) else 0,
                    if (nameIdx != -1) cursor.getString(nameIdx) else "",
                    if (dateIdx != -1) cursor.getString(dateIdx) else "",
                    if (catIdx != -1) cursor.getString(catIdx) else "",
                    if (amtIdx != -1) cursor.getString(amtIdx) else "",
                    if (notesIdx != -1) cursor.getString(notesIdx) else ""
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
        if (success != -1L) {
            addNotification(userId, "New Budget", "Set a budget of ${budget.amount} for ${budget.category}.")
        }
        db.close()
        return success != -1L
    }

    fun updateBudget(userId: Int, budget: Budget): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_BUD_CATEGORY, budget.category)
        values.put(COLUMN_BUD_AMOUNT, budget.amount)
        val success = db.update(TABLE_BUDGETS, values, "$COLUMN_BUD_ID=? AND $COLUMN_BUD_USER_ID=?", arrayOf(budget.id.toString(), userId.toString()))
        if (success > 0) {
            addNotification(userId, "Budget Updated", "Updated budget for ${budget.category}.")
        }
        db.close()
        return success > 0
    }

    fun deleteBudget(userId: Int, budgetId: Int): Boolean {
        val db = this.writableDatabase
        val success = db.delete(TABLE_BUDGETS, "$COLUMN_BUD_ID=? AND $COLUMN_BUD_USER_ID=?", arrayOf(budgetId.toString(), userId.toString()))
        if (success > 0) {
            addNotification(userId, "Budget Deleted", "A budget has been removed.")
        }
        db.close()
        return success > 0
    }

    fun getBudgets(userId: Int): List<Budget> {
        val list = mutableListOf<Budget>()
        val db = this.readableDatabase
        val cursor = db.query(TABLE_BUDGETS, null, "$COLUMN_BUD_USER_ID=?", arrayOf(userId.toString()), null, null, null)
        
        val idIdx = cursor.getColumnIndex(COLUMN_BUD_ID)
        val catIdx = cursor.getColumnIndex(COLUMN_BUD_CATEGORY)
        val amtIdx = cursor.getColumnIndex(COLUMN_BUD_AMOUNT)

        if (cursor.moveToFirst()) {
            do {
                list.add(Budget(
                    if (idIdx != -1) cursor.getInt(idIdx) else 0,
                    if (catIdx != -1) cursor.getString(catIdx) else "Others",
                    if (amtIdx != -1) cursor.getString(amtIdx) else "0"
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    // Notification Operations
    fun addNotification(userId: Int, title: String, message: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_NOTIF_USER_ID, userId)
        values.put(COLUMN_NOTIF_TITLE, title)
        values.put(COLUMN_NOTIF_MESSAGE, message)
        
        val now = Calendar.getInstance().time
        val fmt = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
        values.put(COLUMN_NOTIF_DATE, fmt.format(now))
        
        val success = db.insert(TABLE_NOTIFICATIONS, null, values)
        return success != -1L
    }

    fun getNotifications(userId: Int): List<Notification> {
        val list = mutableListOf<Notification>()
        val db = this.readableDatabase
        val cursor = db.query(TABLE_NOTIFICATIONS, null, "$COLUMN_NOTIF_USER_ID=?", arrayOf(userId.toString()), null, null, "$COLUMN_NOTIF_ID DESC")
        
        val titleIdx = cursor.getColumnIndex(COLUMN_NOTIF_TITLE)
        val msgIdx = cursor.getColumnIndex(COLUMN_NOTIF_MESSAGE)
        val dateIdx = cursor.getColumnIndex(COLUMN_NOTIF_DATE)

        if (cursor.moveToFirst()) {
            do {
                list.add(Notification(
                    if (titleIdx != -1) cursor.getString(titleIdx) else "",
                    if (msgIdx != -1) cursor.getString(msgIdx) else "",
                    if (dateIdx != -1) cursor.getString(dateIdx) else ""
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    fun clearNotifications(userId: Int): Boolean {
        val db = this.writableDatabase
        val success = db.delete(TABLE_NOTIFICATIONS, "$COLUMN_NOTIF_USER_ID=?", arrayOf(userId.toString()))
        db.close()
        return success > 0
    }

    // Template (Favorites) Operations
    fun addTemplate(userId: Int, name: String, category: String, amount: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_TEMP_USER_ID, userId)
        values.put(COLUMN_TEMP_NAME, name)
        values.put(COLUMN_TEMP_CATEGORY, category)
        values.put(COLUMN_TEMP_AMOUNT, amount)
        val success = db.insert(TABLE_TEMPLATES, null, values)
        db.close()
        return success != -1L
    }

    fun getTemplates(userId: Int): List<Triple<String, String, String>> {
        val list = mutableListOf<Triple<String, String, String>>()
        val db = this.readableDatabase
        val cursor = db.query(TABLE_TEMPLATES, null, "$COLUMN_TEMP_USER_ID=?", arrayOf(userId.toString()), null, null, null)
        
        val nameIdx = cursor.getColumnIndex(COLUMN_TEMP_NAME)
        val catIdx = cursor.getColumnIndex(COLUMN_TEMP_CATEGORY)
        val amtIdx = cursor.getColumnIndex(COLUMN_TEMP_AMOUNT)

        if (cursor.moveToFirst()) {
            do {
                val name = if (nameIdx != -1) cursor.getString(nameIdx) else ""
                val cat = if (catIdx != -1) cursor.getString(catIdx) else ""
                val amt = if (amtIdx != -1) cursor.getString(amtIdx) else ""
                list.add(Triple(name, cat, amt))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    // Recurring Operations
    fun addRecurring(userId: Int, name: String, category: String, amount: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_REC_USER_ID, userId)
        values.put(COLUMN_REC_NAME, name)
        values.put(COLUMN_REC_CATEGORY, category)
        values.put(COLUMN_REC_AMOUNT, amount)
        values.put(COLUMN_REC_LAST_ADDED_MONTH, "") // Empty initially
        val success = db.insert(TABLE_RECURRING, null, values)
        db.close()
        return success != -1L
    }

    fun processRecurring(userId: Int) {
        val currentMonth = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Calendar.getInstance().time)
        val dbRead = this.readableDatabase
        val cursor = dbRead.query(TABLE_RECURRING, null, "$COLUMN_REC_USER_ID=?", arrayOf(userId.toString()), null, null, null)
        
        val nameIdx = cursor.getColumnIndex(COLUMN_REC_NAME)
        val catIdx = cursor.getColumnIndex(COLUMN_REC_CATEGORY)
        val amtIdx = cursor.getColumnIndex(COLUMN_REC_AMOUNT)
        val lastIdx = cursor.getColumnIndex(COLUMN_REC_LAST_ADDED_MONTH)
        val idIdx = cursor.getColumnIndex(COLUMN_REC_ID)

        if (cursor.moveToFirst()) {
            do {
                val lastAdded = cursor.getString(lastIdx)
                if (lastAdded != currentMonth) {
                    // Add as expense
                    val expense = Expense(
                        name = cursor.getString(nameIdx),
                        date = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(Calendar.getInstance().time),
                        category = cursor.getString(catIdx),
                        amount = cursor.getString(amtIdx),
                        notes = "Auto-added recurring item"
                    )
                    addExpense(userId, expense)

                    // Update last added month
                    val dbWrite = this.writableDatabase
                    val values = ContentValues()
                    values.put(COLUMN_REC_LAST_ADDED_MONTH, currentMonth)
                    dbWrite.update(TABLE_RECURRING, values, "$COLUMN_REC_ID=?", arrayOf(cursor.getString(idIdx)))
                }
            } while (cursor.moveToNext())
        }
        cursor.close()
    }
}