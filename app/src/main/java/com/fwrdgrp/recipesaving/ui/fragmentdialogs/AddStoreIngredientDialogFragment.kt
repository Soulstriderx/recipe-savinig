package com.fwrdgrp.recipesaving.ui.fragmentdialogs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.fwrdgrp.recipesaving.data.enums.Units
import com.fwrdgrp.recipesaving.databinding.FragmentAddStoreIngredientDialogBinding
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlin.getValue

class AddStoreIngredientDialogFragment(
    private val itemData: (
        itemName: String, itemIngredient: String, itemPrice: Double,
        itemAmount: Double, itemUnit: String
    ) -> Unit
) : DialogFragment() {
    private val viewModel: AddStoreIngredientDialogViewModel by viewModels {
        AddStoreIngredientDialogViewModel.Factory
    }
    private lateinit var binding: FragmentAddStoreIngredientDialogBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddStoreIngredientDialogBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewModel.ingredients.filterNotNull().collect {
                setupAutofillAdapter(it.map { it.name })
            }
        }
        binding.run {
            setupUnitSpinner(requireContext(), spUnit)
            mbCancel.setOnClickListener { dismiss() }
            mbAdd.setOnClickListener {
                val name = etItemName.text.toString()
                val ingredient = etIngredientName.text.toString()
                val price = etPrice.text.toString().toDouble()
                val amount = etAmount.text.toString().toDouble()
                val unit = spUnit.selectedItem?.toString() ?: ""
                itemData(name, ingredient, price, amount, unit)
                dismiss()
            }
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