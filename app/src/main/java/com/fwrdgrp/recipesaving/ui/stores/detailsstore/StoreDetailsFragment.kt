package com.fwrdgrp.recipesaving.ui.stores.detailsstore

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.fwrdgrp.recipesaving.data.models.shopping.StoreWithItemsDetails
import com.fwrdgrp.recipesaving.databinding.FragmentStoreDetailsBinding
import com.fwrdgrp.recipesaving.ui.adapters.StoreIngredientsAdapter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlin.getValue
import androidx.core.view.isGone

class StoreDetailsFragment : Fragment() {
    private val viewModel: StoreDetailsViewModel by viewModels {
        StoreDetailsViewModel.Factory
    }
    private lateinit var binding: FragmentStoreDetailsBinding
    private lateinit var adapter: StoreIngredientsAdapter
    private val args: StoreDetailsFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStoreDetailsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.ivAdd.setOnClickListener {
            var visibilityChanger = if (binding.llAddIngredient.isGone) View.VISIBLE
            else View.GONE
            binding.llAddIngredient.visibility = visibilityChanger
        }
        lifecycleScope.launch {
            viewModel.fetchStoreDetails(args.storeId)
            viewModel.storeDetails.filterNotNull().collect {
                binding.tvHeaderDetails.text = "Store Details"
                setData(it)
            }
        }
        setupAdapter()
    }

    fun setData(details: StoreWithItemsDetails) {
        binding.run {
            tvName.text = details.store.name
            tvLocation.text = details.store.location ?: "Unknown location"
            adapter.applyStoreItemWithDetails(details.items)
        }
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