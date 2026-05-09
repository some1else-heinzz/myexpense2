package com.example.myexpense

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CategoriesActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var session: SessionManager
    private lateinit var rvCategories: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)

        db = DatabaseHelper(this)
        session = SessionManager(this)

        findViewById<ImageView>(R.id.iv_back).setOnClickListener {
            finish()
        }

        rvCategories = findViewById(R.id.rv_categories)
        rvCategories.layoutManager = LinearLayoutManager(this)

        loadCategories()

        findViewById<Button>(R.id.btn_add_category).setOnClickListener {
            startActivity(Intent(this, AddCategoryActivity::class.java))
        }
    }

    private fun loadCategories() {
        val userId = session.getUserId()
        val categories = db.getCategories(userId)

        rvCategories.adapter = CategoryAdapter(
            categories,
            onEditClick = { category ->
                val intent = Intent(this, EditCategoryActivity::class.java)
                intent.putExtra("CATEGORY_NAME", category.name)
                startActivity(intent)
            },
            onDeleteClick = { category ->
                showDeleteConfirmationDialog(category)
            }
        )
    }

    private fun showDeleteConfirmationDialog(category: Category) {
        AlertDialog.Builder(this)
            .setTitle("Delete Category")
            .setMessage("Are you sure you want to delete this category?")
            .setPositiveButton("Confirm") { _, _ ->
                val userId = session.getUserId()
                if (db.deleteCategory(userId, category.name)) {
                    loadCategories()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        loadCategories()
    }
}