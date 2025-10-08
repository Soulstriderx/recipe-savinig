package com.fwrdgrp.recipesaving.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.fwrdgrp.recipesaving.R
import com.fwrdgrp.recipesaving.data.models.shopping.ShoppingListItemWithIngredient
import com.fwrdgrp.recipesaving.databinding.LayoutItemShopListDetailsBinding
import kotlin.math.ceil

class ShopListDetailsAdapter(
    var shopListItems: List<ShoppingListItemWithIngredient>,
    val storeId: Int,
    private val onCheck: (ShoppingListItemWithIngredient, Boolean) -> Unit
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
            val storeItem = item.storeItems.firstOrNull { it.storeId == storeId }
            var price = 0.0;
            var truePrice = 0.0;
            var packageNeeded = 0
            storeItem?.let {
                price = storeItem.price
                packageNeeded =
                    ceil(item.shoppingListItem.amountNeeded / storeItem.packageAmount).toInt()
                truePrice = packageNeeded * price
            }

            binding.run {
                tvName.text = item.ingredient.name
                tvAmount.text = root.context.getString(
                    R.string.shopping_list_amount_unit,
                    item.shoppingListItem.amountNeeded, item.shoppingListItem.neededUnit
                )
                tvPrice.text = if (truePrice == 0.0) {
                    "Unavailable at this location."
                } else root.context.getString(
                    R.string.shopping_list_price, packageNeeded, price, truePrice
                )

                cbDone.isChecked = item.shoppingListItem.bought
                cbDone.setOnCheckedChangeListener { _, isChecked ->
                    TransitionManager.beginDelayedTransition(root as ViewGroup, AutoTransition())
                    vChecked.visibility = if (isChecked) View.VISIBLE else View.GONE
                    onCheck(item, isChecked)
                }
                ivDelete.setOnClickListener { }
            }
        }
    }
}