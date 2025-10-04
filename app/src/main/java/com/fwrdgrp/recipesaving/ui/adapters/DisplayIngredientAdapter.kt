package com.fwrdgrp.recipesaving.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fwrdgrp.recipesaving.data.models.recipe.Ingredient
import com.fwrdgrp.recipesaving.databinding.LayoutItemDisplayIngredientBinding

class DisplayIngredientAdapter(
    var ingredients: List<Pair<Ingredient, Pair<Double, String>>>
) : RecyclerView.Adapter<DisplayIngredientAdapter.IngredientViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): IngredientViewHolder {
        val binding = LayoutItemDisplayIngredientBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return IngredientViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: IngredientViewHolder,
        position: Int
    ) {
        val ingredient = ingredients[position]
        holder.binding.run {
            tvIngredient.text = ingredient.first.name
            tvAmount.text = ingredient.second.first.toString()
            tvUnit.text = ingredient.second.second
        }
    }

    override fun getItemCount() = ingredients.size

    fun applyIngredient(ingredients: List<Pair<Ingredient, Pair<Double, String>>>) {
        this.ingredients = ingredients
        notifyDataSetChanged()
    }

    inner class IngredientViewHolder(
        val binding: LayoutItemDisplayIngredientBinding
    ) : RecyclerView.ViewHolder(binding.root)
}