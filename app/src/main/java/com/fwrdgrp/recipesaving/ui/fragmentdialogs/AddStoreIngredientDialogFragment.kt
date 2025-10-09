package com.fwrdgrp.recipesaving.ui.fragmentdialogs

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.fwrdgrp.recipesaving.data.enums.Units
import com.fwrdgrp.recipesaving.data.models.shopping.StoreItemWithDetails
import com.fwrdgrp.recipesaving.databinding.FragmentAddStoreIngredientDialogBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlin.getValue

class AddStoreIngredientDialogFragment(
    private val storeId: Int,
    private val itemData: (
        itemName: String, itemIngredient: String, itemPrice: Double,
        itemAmount: Double, itemUnit: String
    ) -> Unit
) : DialogFragment() {
    private val viewModel: AddStoreIngredientDialogViewModel by viewModels {
        AddStoreIngredientDialogViewModel.Factory
    }
    private lateinit var binding: FragmentAddStoreIngredientDialogBinding

    private var existingStoreItems: List<StoreItemWithDetails> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddStoreIngredientDialogBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getStoreItems(storeId)
        lifecycleScope.launch {
            viewModel.ingredients.filterNotNull().collect {
                setupAutofillAdapter(it.map { it.name })
            } }
        lifecycleScope.launch {
            viewModel.storeItems.filterNotNull().collect {
                existingStoreItems = it
            } }
        setupListeners()
    }

    fun showError() {
        Snackbar.make(binding.root, "This ingredient already exist.", Snackbar.LENGTH_SHORT)
            .setBackgroundTint(
                ContextCompat.getColor(
                    requireContext(), android.R.color.holo_red_light
                )
            ).setTextColor(Color.WHITE).show()
    }

    private fun setupListeners() {
        binding.run {
            setupUnitSpinner(requireContext(), spUnit)
            mbCancel.setOnClickListener { dismiss() }
            mbAdd.setOnClickListener { handleAdd() }
        }
    }

    fun handleAdd() {
        binding.run {
            val name = etItemName.text.toString()
            val ingredient = etIngredientName.text.toString()
            val price = etPrice.text.toString().toDouble()
            val amount = etAmount.text.toString().toDouble()
            val unit = spUnit.selectedItem?.toString() ?: ""

            if (isDuplicate(ingredient)) {
                showError()
                return
            }

            itemData(name, ingredient, price, amount, unit)
            dismiss()
        }
    }

    private fun isDuplicate(ingredientName: String): Boolean {
        return existingStoreItems.any {
            it.ingredient.name.equals(ingredientName, ignoreCase = true)
        }
    }

    fun setupAutofillAdapter(ingredients: List<String>) {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            ingredients
        )
        binding.etIngredientName.setAdapter(adapter)
    }

    fun setupUnitSpinner(context: Context, spinner: Spinner) {
        val unitValues = getUnitDisplayNames()
        val adapter = createUnitSpinnerAdapter(context, unitValues)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
            }

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