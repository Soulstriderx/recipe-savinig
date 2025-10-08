package com.fwrdgrp.recipesaving.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fwrdgrp.recipesaving.R
import com.fwrdgrp.recipesaving.data.models.shopping.ShoppingListItemWithIngredient
import com.fwrdgrp.recipesaving.data.models.shopping.ShoppingListWithStoreAndItems
import com.fwrdgrp.recipesaving.databinding.LayoutItemShopListBinding
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.ceil

class ShopListAdapter(
    var shoppingList: List<ShoppingListWithStoreAndItems>,
    val onClick: (Int) -> Unit
) : RecyclerView.Adapter<ShopListAdapter.ShopListViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ShopListViewHolder {
        val binding =
            LayoutItemShopListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShopListViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ShopListViewHolder,
        position: Int
    ) {
        val shoppingList = shoppingList[position]
        holder.bind(shoppingList)
    }

    override fun getItemCount() = shoppingList.size

    fun applyShopList(shopList: List<ShoppingListWithStoreAndItems>) {
        this.shoppingList = shopList.toList()
        notifyDataSetChanged()
    }

    inner class ShopListViewHolder(
        val binding: LayoutItemShopListBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ShoppingListWithStoreAndItems) {
            val formatter = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
            val totalPrice = calculateTotalPrice(item.items, item.store.id)
            binding.run {
                llShopList.setOnClickListener { onClick(item.shoppingList.id) }
                tvName.text = item.shoppingList.name
                tvStore.text = root.context.getString(R.string.shoplist_store_display, item.store.name)
                tvTotalItems.text = item.items.size.toString()
                tvDate.text = formatter.format(item.shoppingList.dateCreated)
                tvTotalPrice.text = root.context.getString(R.string.store_detail_price, totalPrice)
            }
        }
        private fun calculateTotalPrice(
            items: List<ShoppingListItemWithIngredient>,
            storeId: Int
        ): Double {
            return items.filter { !it.shoppingListItem.bought }.sumOf { shopListItems ->
                val storeItem = shopListItems.storeItems.firstOrNull { it.storeId == storeId }

                if (storeItem == null) {
                    0.0
                } else {
                    val price = storeItem.price
                    val packageAmount = storeItem.packageAmount.takeIf { it > 0.0 } ?: 1.0
                    val packagesNeeded = ceil(shopListItems.shoppingListItem.amountNeeded /
                            packageAmount).toInt()
                    packagesNeeded * price
                }
            }
        }
    }
}