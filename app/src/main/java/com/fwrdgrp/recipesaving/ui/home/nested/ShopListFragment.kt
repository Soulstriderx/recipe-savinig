package com.fwrdgrp.recipesaving.ui.home.nested

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.fwrdgrp.recipesaving.databinding.FragmentShopListBinding
import com.fwrdgrp.recipesaving.ui.adapters.ShopListAdapter
import com.fwrdgrp.recipesaving.ui.home.HomeFragmentDirections

class ShopListFragment : Fragment() {
    private lateinit var binding: FragmentShopListBinding
    private lateinit var adapter: ShopListAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentShopListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
        binding.ivStores.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeToStores())
        }
    }

    fun setupAdapter() {
        binding.run {
            adapter = ShopListAdapter(
                emptyList(),
                {} //Details
            )
            rvShopList.adapter = adapter
            rvShopList.layoutManager = LinearLayoutManager(requireContext())
        }
    }
}