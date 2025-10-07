package com.fwrdgrp.recipesaving.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.fwrdgrp.recipesaving.data.models.shopping.ShoppingListItemWithIngredient
import com.fwrdgrp.recipesaving.databinding.LayoutItemShopListDetailsBinding

class ShopListDetailsAdapter(
    var shopListItems: List<ShoppingListItemWithIngredient>,
    val storeId: Int
) : RecyclerView.Adapter<ShopListDetailsAdapter.ShopListDetailsViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ShopListDetailsViewHolder {
        val binding =
            LayoutItemShopListDetailsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ShopListDetailsViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ShopListDetailsViewHolder,
        position: Int
    ) {
        val shopListItem = shopListItems[position]
        holder.bind(shopListItem)
    }

    override fun getItemCount() = shopListItems.size

    fun applyShopListItem(shopListItem: List<ShoppingListItemWithIngredient>) {
        this.shopListItems = shopListItem.toList()
        notifyDataSetChanged()
    }

    inner class ShopListDetailsViewHolder(
        val binding: LayoutItemShopListDetailsBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ShoppingListItemWithIngredient) {
            val price =
                item.storeItems.firstOrNull {it.storeId == storeId}?.price ?: 0.0
            binding.run {
                tvName.text = item.ingredient.name
                tvAmount.text = item.shoppingListItem.amountNeeded.toString()
                tvPrice.text = if (price == 0.0) { "Unavailable at this location." } else price.toString()
                cbDone.isChecked = item.shoppingListItem.bought
                    cbDone.setOnCheckedChangeListener { _, isChecked ->
                        TransitionManager.beginDelayedTransition(
                            root as ViewGroup,
                            AutoTransition()
                        )
                        vChecked.visibility = if (isChecked) View.VISIBLE else View.GONE
                    }
            }
        }
    }
}