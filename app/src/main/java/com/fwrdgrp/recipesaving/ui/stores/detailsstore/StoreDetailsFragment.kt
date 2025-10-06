package com.fwrdgrp.recipesaving.ui.stores.detailsstore

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.fwrdgrp.recipesaving.databinding.FragmentStoreDetailsBinding
import com.fwrdgrp.recipesaving.ui.adapters.StoreIngredientsAdapter

class StoreDetailsFragment : Fragment() {
    private lateinit var binding: FragmentStoreDetailsBinding
    private lateinit var adapter: StoreIngredientsAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStoreDetailsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
    }

    fun setupAdapter() {
        binding.run {
            adapter = StoreIngredientsAdapter(
                emptyList(),
                {},
                {}
            )
            rvStoreIngredients.adapter = adapter
            rvStoreIngredients.layoutManager = LinearLayoutManager(requireContext())
        }
    }
}