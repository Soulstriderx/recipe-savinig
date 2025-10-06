package com.fwrdgrp.recipesaving.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fwrdgrp.recipesaving.data.models.shopping.StoreItemWithDetails
import com.fwrdgrp.recipesaving.databinding.LayoutItemIngredientWithPriceBinding

class StoreIngredientsAdapter(
    var storeItem: List<StoreItemWithDetails>,
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
        val storeItem = storeItem[position]
        holder.bind(storeItem)
    }

    override fun getItemCount() = storeItem.size

    fun applyStoreItemWithDetails(storeItem: List<StoreItemWithDetails>) {
        this.storeItem = storeItem.toList()
        notifyDataSetChanged()
    }

    inner class StoreIngredientsViewHolder(
        val binding: LayoutItemIngredientWithPriceBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: StoreItemWithDetails) {
            binding.run {
                tvIngredient.text = item.storeItem.name
//                tvAmount.text =
                tvPrice.text = item.storeItem.price.toString()
            }
        }
    }
}