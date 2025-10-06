package com.fwrdgrp.recipesaving.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fwrdgrp.recipesaving.databinding.LayoutItemShopListBinding

class ShopListAdapter(
    var shopList: List<String>,
    val onClick: (Int) -> Unit
): RecyclerView.Adapter<ShopListAdapter.ShopListViewHolder>() {
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
        val shopList = shopList[position]
//        holder.bind(shopList)
    }

    override fun getItemCount() = shopList.size

    fun applyShopList(shopList: List<String>) {
        //I don't know why, but it needs to turn this list into another list?
        this.shopList = shopList.toList()
        notifyDataSetChanged()
    }

    inner class ShopListViewHolder(
        val binding: LayoutItemShopListBinding
    ): RecyclerView.ViewHolder(binding.root) {
//        fun bind(item: ShopList) {
//
//        }
    }
}