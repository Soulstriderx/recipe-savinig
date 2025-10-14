package com.fwrdgrp.recipesaving.ui.home.nested

import android.animation.ValueAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.fwrdgrp.recipesaving.data.enums.ShopListFilter
import com.fwrdgrp.recipesaving.data.enums.SortOrder
import com.fwrdgrp.recipesaving.databinding.FragmentShopListBinding
import com.fwrdgrp.recipesaving.ui.adapters.ShopListAdapter
import com.fwrdgrp.recipesaving.ui.home.HomeFragmentDirections
import com.fwrdgrp.recipesaving.ui.home.HomeViewModel
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlin.getValue

class ShopListFragment : Fragment() {
    private val viewModel: HomeViewModel by viewModels(
        ownerProducer = { requireParentFragment() },
        factoryProducer = { HomeViewModel.Factory }
    )
    private lateinit var binding: FragmentShopListBinding
    private lateinit var adapter: ShopListAdapter
    private lateinit var filterAdapter: ArrayAdapter<ShopListFilter>
    private lateinit var ascDescAdapter: ArrayAdapter<SortOrder>

    private var currentFilter = ShopListFilter.DATE
    private var currentSort = SortOrder.ASCENDING

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentShopListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getShoppingLists()
        setupAdapter()
        setupFilter()
        setupFilterAscDesc()
        lifecycleScope.launch {
            viewModel.shoppingList.filterNotNull().collect {
                adapter.applyShopList(it)
            }
        }
        binding.run {
            ivStores.setOnClickListener {
                findNavController().navigate(HomeFragmentDirections.actionHomeToStores())
            }
            tvFilter.setOnClickListener { toggleFilter(llFilter) }
            etSearch.doOnTextChanged { text, _, _, _ ->
                viewModel.setSearch(text.toString())
            }
        }
    }

    fun setupFilter() {
        binding.run {
            filterAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                ShopListFilter.entries.toList()
            )
            filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spFilter.adapter = filterAdapter
            spFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val selected = filterAdapter.getItem(position) ?: return
                    currentFilter = selected
                    viewModel.setFilter(currentFilter, currentSort)
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }

    fun setupFilterAscDesc() {
        binding.run {
            ascDescAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                SortOrder.entries.toList()
            )
            ascDescAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spAscDesc.adapter = ascDescAdapter
            spAscDesc.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val selected = ascDescAdapter.getItem(position) ?: return
                    currentSort = selected
                    viewModel.setFilter(currentFilter, currentSort)
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }

    fun toggleFilter(view: View) {
        val isToggled = view.height == 0
        val viewHeight = if(isToggled) {
            view.measure(
                View.MeasureSpec.makeMeasureSpec((view.parent as View).width,
                    View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            view.measuredHeight
        } else 0

        val animation = ValueAnimator.ofInt(view.height, viewHeight)
        animation.addUpdateListener {
            val value = it.animatedValue as Int
            view.layoutParams.height = value
            view.requestLayout()
        }
        animation.duration = 250
        animation.start()
    }

    fun setupAdapter() {
        binding.run {
            adapter = ShopListAdapter(
                emptyList(),
                { findNavController().navigate(
                    HomeFragmentDirections.actionHomeToShopListDetails(it)) }
            )
            rvShopList.adapter = adapter
            rvShopList.layoutManager = LinearLayoutManager(requireContext())
        }
    }
}