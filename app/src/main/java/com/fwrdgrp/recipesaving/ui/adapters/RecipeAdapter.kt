package com.fwrdgrp.recipesaving.ui.adapters

import android.content.Context
import android.graphics.ImageDecoder
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fwrdgrp.recipesaving.data.models.recipe.Recipe
import com.fwrdgrp.recipesaving.databinding.LayoutItemRecipeBinding
import com.fwrdgrp.recipesaving.R
import com.fwrdgrp.recipesaving.data.enums.Category
import androidx.core.net.toUri

class RecipeAdapter(
    var recipes: List<Recipe>,
    val onRecipeClick: (Int) -> Unit,
    val onCategoryClick: (Category) -> Unit
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecipeViewHolder {
        val binding =
            LayoutItemRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: RecipeViewHolder,
        position: Int
    ) {
        val recipe = recipes[position]
        holder.bind(recipe)
    }

    override fun getItemCount() = recipes.size

    fun applyRecipes(recipes: List<Recipe>) {
        //I don't know why, but it needs to turn this list into another list?
        this.recipes = recipes.toList()
        notifyDataSetChanged()
    }

    fun ImageView.loadPersistedUri(context: Context, uri: Uri) {
        try {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            val drawable = ImageDecoder.decodeDrawable(source)
            this.setImageDrawable(drawable)
        } catch (e: Exception) {
            e.printStackTrace()
            this.setImageResource(R.drawable.image_save_bg) // fallback
        }
    }

    inner class RecipeViewHolder(
        val binding: LayoutItemRecipeBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Recipe) {
            binding.run {
                tvTitle.text = item.title
                tvTime.text = item.estTime.toString()
                tvDescription.text = item.description
                llRecipe.setOnClickListener { onRecipeClick(item.id) }
                item.imageUri?.let { uriString ->
                    val uri = uriString.toUri()
                    ivImage.loadPersistedUri(itemView.context, uri)
                }
                glCategory.removeAllViews()
                for (category in item.category.take(3)) {
                    val tvCategory = TextView(root.context).apply {
                        text = category.name
                        setBackgroundResource(R.drawable.box_bg)
                    }
                    tvCategory.setOnClickListener { onCategoryClick(category) }
                    glCategory.addView(tvCategory)
                }
                ivStar.visibility = if(item.favorite) View.VISIBLE
                else View.GONE
            }
        }
    }
}