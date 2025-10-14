package com.fwrdgrp.recipesaving.ui.stores

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.fwrdgrp.recipesaving.data.utils.Constant
import com.fwrdgrp.recipesaving.databinding.FragmentStoresBinding
import com.fwrdgrp.recipesaving.ui.adapters.StoresAdapter
import com.fwrdgrp.recipesaving.ui.fragmentdialogs.AddStoreDialogFragment
import kotlinx.coroutines.launch
import kotlin.getValue

class StoresFragment : Fragment() {
    private val viewModel: StoresFragmentViewModel by viewModels {
        StoresFragmentViewModel.Factory
    }
    private lateinit var binding: FragmentStoresBinding
    private lateinit var adapter: StoresAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStoresBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
        lifecycleScope.launch {
            viewModel.stores.collect {
                adapter.applyStores(it)
            }
        }
        binding.run {
            etSearch.doOnTextChanged { text, _, _, _ ->
                viewModel.setSearch(text.toString())
            }
            ivBack.setOnClickListener { findNavController().popBackStack() }
            ivAdd.setOnClickListener {
                val dialog = AddStoreDialogFragment { storeName, storeLocation ->
                    lifecycleScope.launch {
                        viewModel.addStore(storeName, storeLocation)
                    }
                }
                dialog.show(parentFragmentManager, Constant.ADD_STORE_DIALOG)
            }
        }
    }

    fun setupAdapter() {
        binding.run {
            adapter = StoresAdapter(
                emptyList(),
                {
                    findNavController().navigate(
                        StoresFragmentDirections.actionStoresToStoreDetails(it)
                    )
                }
            )
            rvStores.adapter = adapter
            rvStores.layoutManager = LinearLayoutManager(requireContext())
        }
    }


}