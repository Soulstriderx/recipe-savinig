package com.fwrdgrp.recipesaving.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fwrdgrp.recipesaving.data.models.recipe.Recipe
import com.fwrdgrp.recipesaving.databinding.LayoutItemRecipeBinding
import com.fwrdgrp.recipesaving.R

class RecipeAdapter(
    var recipes: List<Recipe>
): RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecipeViewHolder {
        val binding = LayoutItemRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: RecipeViewHolder,
        position: Int
    ) {
        val topic = recipes[position]
        holder.bind(topic)
    }

    override fun getItemCount() = recipes.size

    fun applyRecipes(recipes: List<Recipe>) {
        this.recipes = recipes
        notifyDataSetChanged()
    }

    inner class RecipeViewHolder(
        val binding: LayoutItemRecipeBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Recipe) {
            binding.run {
                tvTitle.text = item.title
                tvTime.text = item.estTime.toString()
                tvDescription.text = item.description
                for(category in item.category.take(3)) {
                    val tvCategory = TextView(root.context).apply {
                        text = category.name
                        setBackgroundResource(R.drawable.box_bg)
                    }
                    llCategory.addView(tvCategory)
                }
            }
        }
    }
}