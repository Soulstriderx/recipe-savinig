package com.fwrdgrp.recipesaving.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.fwrdgrp.recipesaving.data.models.shopping.ShoppingListItem
import com.fwrdgrp.recipesaving.databinding.LayoutItemShopListDetailsBinding

class ShopListDetailsAdapter(
    var shopListItems: List<ShoppingListItem>
): RecyclerView.Adapter<ShopListDetailsAdapter.ShopListDetailsViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ShopListDetailsViewHolder {
        val binding =
            LayoutItemShopListDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShopListDetailsViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ShopListDetailsViewHolder,
        position: Int
    ) {
        val shopListItem = shopListItems[position]

    }

    override fun getItemCount() = shopListItems.size

    fun applyShopListItem(shopListItem: List<ShoppingListItem>) {
        this.shopListItems = shopListItem.toList()
        notifyDataSetChanged()
    }

    inner class ShopListDetailsViewHolder(
        val binding: LayoutItemShopListDetailsBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ShoppingListItem) {
            binding.run {
//                tvName.text =
//                tvAmount.text =
//                tvPrice.text =
                cbDone.setOnCheckedChangeListener { _, isChecked ->
                    TransitionManager.beginDelayedTransition(root as ViewGroup,AutoTransition())
                    vChecked.visibility = if (isChecked) View.VISIBLE else View.GONE
                }
            }
        }
    }
}