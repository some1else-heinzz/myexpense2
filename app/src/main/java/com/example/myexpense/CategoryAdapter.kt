package com.example.myexpense

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class CategoryAdapter(
    private val categories: List<Category>,
    private val onEditClick: (Category) -> Unit,
    private val onDeleteClick: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tv_category_name)
        val ivIcon: ImageView = view.findViewById(R.id.iv_category_icon)
        val flIconBg: FrameLayout = view.findViewById(R.id.fl_category_icon_bg)
        val ivEdit: ImageView = view.findViewById(R.id.iv_edit_category)
        val ivDelete: ImageView = view.findViewById(R.id.iv_delete_category)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.tvName.text = category.name
        
        val context = holder.itemView.context
        
        // Set Icon
        val resId = context.resources.getIdentifier(category.iconResName, "drawable", context.packageName)
        if (resId != 0) {
            holder.ivIcon.setImageResource(resId)
        } else {
            holder.ivIcon.setImageResource(R.drawable.ic_others)
        }

        // Set Color
        try {
            val color = Color.parseColor(category.colorHex)
            holder.ivIcon.setColorFilter(color)
            
            // Create a semi-transparent version of the color for background
            val alphaColor = Color.argb(30, Color.red(color), Color.green(color), Color.blue(color))
            val shape = GradientDrawable()
            shape.shape = GradientDrawable.OVAL
            shape.setColor(alphaColor)
            holder.flIconBg.background = shape
        } catch (e: Exception) {
            holder.ivIcon.setColorFilter(ContextCompat.getColor(context, R.color.green_primary))
        }

        holder.ivEdit.setOnClickListener { onEditClick(category) }
        holder.ivDelete.setOnClickListener { onDeleteClick(category) }
    }

    override fun getItemCount() = categories.size
}