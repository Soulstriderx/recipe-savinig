package com.fwrdgrp.recipesaving.ui.fragmentdialogs

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.fwrdgrp.recipesaving.R
import com.fwrdgrp.recipesaving.data.enums.Units
import com.fwrdgrp.recipesaving.data.models.shopping.StoreItemWithDetails
import com.fwrdgrp.recipesaving.data.utils.Constant
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
            }
        }
        lifecycleScope.launch {
            viewModel.storeItems.filterNotNull().collect {
                existingStoreItems = it
            }
        }
        lifecycleScope.launch {
            viewModel.error.collect {
                showError(it)
            }
        }
        setupListeners()
    }

    fun showError(msg: String) {
        Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT)
            .setBackgroundTint(
                ContextCompat.getColor(
                    requireContext(), R.color.color_error
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
            val price = etPrice.text.toString().toDoubleOrNull() ?: 0
            val amount = etAmount.text.toString().toDoubleOrNull() ?: 0
            val unit = spUnit.selectedItem?.toString() ?: ""
            lifecycleScope.launch {
                val isValid =
                    viewModel.validateFields(name, ingredient, price.toDouble(), amount.toDouble())
                if (!isValid) return@launch
                if (isDuplicate(ingredient)) {
                    showError(Constant.INGREDIENT_EXIST)
                    return@launch
                }

                itemData(name, ingredient, price.toDouble(), amount.toDouble(), unit)
                dismiss()
            }
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

    }

    fun getUnitDisplayNames(): List<String> = Units.entries.map { it.label }

    fun createUnitSpinnerAdapter(context: Context, units: List<String>): ArrayAdapter<String> {
        return ArrayAdapter(context, android.R.layout.simple_spinner_item, units).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
    }
}