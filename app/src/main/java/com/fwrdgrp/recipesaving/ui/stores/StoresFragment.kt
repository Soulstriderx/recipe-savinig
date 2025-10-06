package com.fwrdgrp.recipesaving.ui.stores

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.fwrdgrp.recipesaving.databinding.FragmentStoresBinding
import com.fwrdgrp.recipesaving.ui.adapters.StoresAdapter

class StoresFragment : Fragment() {
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
        binding.run {
            ivBack.setOnClickListener { findNavController().popBackStack() }
            ivAdd.setOnClickListener { findNavController().navigate(
                StoresFragmentDirections.actionStoresToAddStoreDialog()) }
        }
    }

    fun setupAdapter() {
        binding.run {
            adapter = StoresAdapter(
                emptyList(),
                { findNavController().navigate(
                    StoresFragmentDirections.actionStoresToStoreDetails(it))
                }
            )
            rvStores.adapter = adapter
            rvStores.layoutManager = LinearLayoutManager(requireContext())
        }
    }
}