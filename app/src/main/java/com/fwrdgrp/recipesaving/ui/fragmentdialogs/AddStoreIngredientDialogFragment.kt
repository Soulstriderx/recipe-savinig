package com.fwrdgrp.recipesaving.ui.fragmentdialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
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
            mbCancel.setOnClickListener { dismiss() }
            mbAdd.setOnClickListener {
                val name = etItemName.text.toString()
                val ingredient = etIngredientName.text.toString()
                val price = etPrice.text.toString().toDouble()
                val amount = etAmount.text.toString().toDouble()
                val unit = etUnit.text.toString()
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
}