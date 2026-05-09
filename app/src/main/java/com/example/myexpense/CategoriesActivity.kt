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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)

        findViewById<ImageView>(R.id.iv_back).setOnClickListener {
            finish()
        }

        val rvCategories = findViewById<RecyclerView>(R.id.rv_categories)
        rvCategories.layoutManager = LinearLayoutManager(this)

        val categories = listOf(
            Category("Food"),
            Category("Transport"),
            Category("Education"),
            Category("Entertainment"),
            Category("Others")
        )

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

        findViewById<Button>(R.id.btn_add_category).setOnClickListener {
            startActivity(Intent(this, AddCategoryActivity::class.java))
        }
    }

    private fun showDeleteConfirmationDialog(category: Category) {
        AlertDialog.Builder(this)
            .setTitle("Delete Category")
            .setMessage("Are you sure you want to delete this category?")
            .setPositiveButton("Confirm") { _, _ ->
                // Actual delete logic would go here
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}