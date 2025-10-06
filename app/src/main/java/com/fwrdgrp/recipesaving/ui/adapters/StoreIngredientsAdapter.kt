package com.fwrdgrp.recipesaving.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fwrdgrp.recipesaving.data.models.shopping.StoreItem
import com.fwrdgrp.recipesaving.databinding.LayoutItemIngredientWithPriceBinding

class StoreIngredientsAdapter(
    var ingredients: List<StoreItem>,
    val onEditClick: (Int) -> Unit,
    val onDeleteClick: (Int) -> Unit
): RecyclerView.Adapter<StoreIngredientsAdapter.StoreIngredientsViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StoreIngredientsViewHolder {
        val binding =
            LayoutItemIngredientWithPriceBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        return StoreIngredientsViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: StoreIngredientsViewHolder,
        position: Int
    ) {
        val ingredient = ingredients[position]
        holder.bind(ingredient)
    }

    override fun getItemCount() = ingredients.size

    fun applyIngredients(ingredients: List<StoreItem>) {
        this.ingredients = ingredients.toList()
        notifyDataSetChanged()
    }

    inner class StoreIngredientsViewHolder(
        val binding: LayoutItemIngredientWithPriceBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: StoreItem) {
            binding.run {
                tvIngredient.text = item.name
//                tvAmount.text =
                tvPrice.text = item.price.toString()
            }
        }
    }
}