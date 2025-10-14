package com.fwrdgrp.recipesaving.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fwrdgrp.recipesaving.data.models.shopping.Store
import com.fwrdgrp.recipesaving.databinding.LayoutItemStoresBinding

class StoresAdapter(
    var stores: List<Store>,
    val onClick: (Int) -> Unit
): RecyclerView.Adapter<StoresAdapter.StoresViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StoresViewHolder {
        val binding =
            LayoutItemStoresBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoresViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: StoresViewHolder,
        position: Int
    ) {
        val store = stores[position]
        holder.bind(store)
    }

    override fun getItemCount() = stores.size

    fun applyStores(stores: List<Store>) {
        this.stores = stores.toList()
        notifyDataSetChanged()
    }

    inner class StoresViewHolder(
        val binding: LayoutItemStoresBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Store) {
            binding.run {
                rlStore.setOnClickListener { onClick(item.id) }
                tvName.text = item.name
                tvLocation.text = item.location
            }
        }
    }
}