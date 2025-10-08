package com.fwrdgrp.recipesaving.ui.detailsshoplist

import android.animation.ValueAnimator
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.fwrdgrp.recipesaving.data.enums.Units
import com.fwrdgrp.recipesaving.data.models.shopping.Store
import com.fwrdgrp.recipesaving.databinding.FragmentShopListDetailsBinding
import com.fwrdgrp.recipesaving.ui.adapters.ShopListDetailsAdapter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlin.getValue

class ShopListDetailsFragment : Fragment() {
    private val viewModel: ShopListDetailsViewModel by viewModels {
        ShopListDetailsViewModel.Factory
    }

    private lateinit var binding: FragmentShopListDetailsBinding
    private lateinit var adapter: ShopListDetailsAdapter
    private lateinit var spinnerAdapter: ArrayAdapter<Store>

    private var selectedStore: Store? = null
    private var storeId: Int? = null

    private val args: ShopListDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentShopListDetailsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchShoppingList()
        lifecycleScope.launch {
            viewModel.shoppingList.filterNotNull().collect {
                selectedStore = it.store
                binding.run {
                    tvShopListName.text = it.shoppingList.name
                    tvStoreName.text = it.store.name
                    storeId = it.store.id
                    setupAdapter()
                    adapter.applyShopListItem(it.items)
                }
            }
        }
        lifecycleScope.launch {
            viewModel.ingredients.filterNotNull().collect {
                setupAutofillAdapter(it.map { it.name })
            }
        }
        lifecycleScope.launch {
            viewModel.stores.filterNotNull().collect {
                setupSpinner(it)
            }
        }
        setupAll()
    }

    fun setupAutofillAdapter(ingredients: List<String>) {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            ingredients
        )
        binding.etIngredient.setAdapter(adapter)
    }

    fun fetchShoppingList() {
        lifecycleScope.launch {
            viewModel.getShoppingList(args.shopListId)
        }
    }

    fun setupAll() {
        binding.run {
            setupUnitSpinner(requireContext(), spUnit)
            ivBack.setOnClickListener { findNavController().popBackStack() }
            ivAdd.setOnClickListener { toggleFilter(llAddIngredient) }
            mbAdd.setOnClickListener {
                val ingredient = etIngredient.text.toString()
                val amount = etAmount.text.toString().toDoubleOrNull() ?: 0.0
                val unit = spUnit.selectedItem?.toString() ?: ""
                addListItem(ingredient, amount, unit)
            }
        }
    }

    fun setupAdapter() {
        storeId?.let { storeId ->
            adapter = ShopListDetailsAdapter(
                emptyList(),
                storeId
            )
        }
        binding.run {
            binding.rvShopListDetails.adapter = adapter
            binding.rvShopListDetails.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    fun setupSpinner(stores: List<Store>) {
        binding.run {
            val validStores = if (stores.size > 0) stores else {
                spStoreName.isEnabled = false
                listOf(Store(name = "Please create a store", location = ""))
            }
            spinnerAdapter = ArrayAdapter(
                requireContext(), android.R.layout.simple_spinner_item,
                validStores
            )
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spStoreName.adapter = spinnerAdapter
            selectedStore?.let { store ->
                val position = stores.indexOfFirst { it.id == store.id }
                if (position >= 0) spStoreName.setSelection(position)
            }
            spStoreName.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int,
                    id: Long
                ) {
                    val selected = spinnerAdapter.getItem(position) ?: return
                    lifecycleScope.launch {
                        viewModel.changeShoppingListStore(selected)
                        viewModel.getShoppingList(args.shopListId)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }

    fun toggleFilter(view: View) {
        val isToggled = view.height == 0
        val viewHeight = if (isToggled) {
            view.measure(
                View.MeasureSpec.makeMeasureSpec(
                    (view.parent as View).width,
                    View.MeasureSpec.EXACTLY
                ),
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

    fun addListItem(ingredient: String, amount: Double, unit: String) {
        lifecycleScope.launch {
            viewModel.addListItem(args.shopListId, ingredient, amount, unit)
            viewModel.getShoppingList(args.shopListId)

        }
    }

    fun setupUnitSpinner(context: Context, spinner: Spinner, ) {
        val unitValues = getUnitDisplayNames()
        val adapter = createUnitSpinnerAdapter(context, unitValues)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {}
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
    }

    fun getUnitDisplayNames(): List<String> = Units.entries.map { it.label }

    fun createUnitSpinnerAdapter(context: Context, units: List<String>): ArrayAdapter<String> {
        return ArrayAdapter(context, android.R.layout.simple_spinner_item, units).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
    }

}