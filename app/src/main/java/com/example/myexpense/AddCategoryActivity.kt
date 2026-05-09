package com.example.myexpense

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class AddCategoryActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var session: SessionManager
    
    private var selectedIcon: String = "ic_others"
    private var selectedColor: String = "#757575"
    
    private lateinit var ivPreview: ImageView
    private lateinit var flPreviewBg: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_category)

        db = DatabaseHelper(this)
        session = SessionManager(this)

        ivPreview = findViewById(R.id.iv_preview_icon)
        flPreviewBg = findViewById(R.id.fl_preview_bg)
        val etName = findViewById<EditText>(R.id.et_category_name)

        findViewById<ImageView>(R.id.iv_back).setOnClickListener {
            showDiscardDialog()
        }

        setupIconGrid()
        setupColorList()

        findViewById<Button>(R.id.btn_save_category).setOnClickListener {
            val name = etName.text.toString().trim()
            if (name.isNotEmpty()) {
                val userId = session.getUserId()
                val newCategory = Category(name, selectedIcon, selectedColor)
                if (db.addCategory(userId, newCategory)) {
                    Toast.makeText(this, "Category saved", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Failed to save category", Toast.LENGTH_SHORT).show()
                }
            } else {
                etName.error = "Name cannot be empty"
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showDiscardDialog()
            }
        })
        
        updatePreview()
    }

    private fun setupIconGrid() {
        val glIcons = findViewById<GridLayout>(R.id.gl_icons)
        val icons = listOf(
            "ic_food", "ic_transport", "ic_education", "ic_entertainment",
            "ic_home", "ic_clothing", "ic_heart", "ic_others"
        )

        for (iconName in icons) {
            val view = layoutInflater.inflate(R.layout.item_icon_choice, glIcons, false)
            val ivIcon = view.findViewById<ImageView>(R.id.iv_choice_icon)
            val indicator = view.findViewById<View>(R.id.v_selection_indicator)
            
            val resId = resources.getIdentifier(iconName, "drawable", packageName)
            ivIcon.setImageResource(resId)
            
            if (selectedIcon == iconName) indicator.visibility = View.VISIBLE

            view.setOnClickListener {
                selectedIcon = iconName
                // Refresh grid selection
                for (i in 0 until glIcons.childCount) {
                    glIcons.getChildAt(i).findViewById<View>(R.id.v_selection_indicator).visibility = View.GONE
                }
                indicator.visibility = View.VISIBLE
                updatePreview()
            }
            glIcons.addView(view)
        }
    }

    private fun setupColorList() {
        val llColors = findViewById<LinearLayout>(R.id.ll_colors)
        val colors = listOf(
            "#4CAF50", "#FFC107", "#2196F3", "#3F51B5", "#E91E63", "#9C27B0", "#795548", "#757575"
        )

        for (colorHex in colors) {
            val view = layoutInflater.inflate(R.layout.item_color_choice, llColors, false)
            val vBg = view.findViewById<View>(R.id.v_color_bg)
            val ivCheck = view.findViewById<ImageView>(R.id.iv_check)
            
            val color = Color.parseColor(colorHex)
            val shape = GradientDrawable()
            shape.shape = GradientDrawable.OVAL
            shape.setColor(color)
            vBg.background = shape

            if (selectedColor == colorHex) ivCheck.visibility = View.VISIBLE

            view.setOnClickListener {
                selectedColor = colorHex
                // Refresh list selection
                for (i in 0 until llColors.childCount) {
                    llColors.getChildAt(i).findViewById<View>(R.id.iv_check).visibility = View.GONE
                }
                ivCheck.visibility = View.VISIBLE
                updatePreview()
            }
            llColors.addView(view)
        }
    }

    private fun updatePreview() {
        val resId = resources.getIdentifier(selectedIcon, "drawable", packageName)
        ivPreview.setImageResource(resId)
        
        try {
            val color = Color.parseColor(selectedColor)
            ivPreview.setColorFilter(color)
            
            val alphaColor = Color.argb(30, Color.red(color), Color.green(color), Color.blue(color))
            val shape = GradientDrawable()
            shape.shape = GradientDrawable.OVAL
            shape.setColor(alphaColor)
            flPreviewBg.background = shape
        } catch (e: Exception) {}
    }

    private fun showDiscardDialog() {
        if (findViewById<EditText>(R.id.et_category_name).text.isNotEmpty()) {
            AlertDialog.Builder(this)
                .setTitle("Discard Changes?")
                .setMessage("Are you sure you want to discard your changes?")
                .setPositiveButton("Discard") { _, _ ->
                    finish()
                }
                .setNegativeButton("Keep Editing", null)
                .show()
        } else {
            finish()
        }
    }
}