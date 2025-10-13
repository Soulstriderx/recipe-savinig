package com.fwrdgrp.recipesaving.ui.stores.detailsstore

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.fwrdgrp.recipesaving.R
import com.fwrdgrp.recipesaving.data.models.shopping.StoreWithItemsDetails
import com.fwrdgrp.recipesaving.databinding.FragmentStoreDetailsBinding
import com.fwrdgrp.recipesaving.ui.adapters.StoreIngredientsAdapter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlin.getValue
import com.fwrdgrp.recipesaving.data.models.shopping.StoreItem
import com.fwrdgrp.recipesaving.data.utils.Constant
import com.fwrdgrp.recipesaving.ui.fragmentdialogs.AddStoreDialogFragment
import com.fwrdgrp.recipesaving.ui.fragmentdialogs.AddStoreIngredientDialogFragment
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.Dispatchers

class StoreDetailsFragment : Fragment() {
    private val viewModel: StoreDetailsViewModel by viewModels {
        StoreDetailsViewModel.Factory
    }
    private lateinit var binding: FragmentStoreDetailsBinding
    private lateinit var adapter: StoreIngredientsAdapter
    private val args: StoreDetailsFragmentArgs by navArgs()

    private var storeName: String = ""
    private var storeLocation: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStoreDetailsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewModel.fetchStoreDetails(args.storeId)
            viewModel.storeDetails.filterNotNull().collect {
                storeName = it.store.name
                storeLocation = it.store.location ?: ""
                binding.tvHeaderDetails.text = requireContext().getString(R.string.store_details)
                setData(it)
            }
        }
        setOnClickListeners()
        setupAdapter()

        binding.run {
            ivDelete.setOnClickListener {
                deleteStoreDialogCreation(args.storeId).show()
            }
            ivEdit.setOnClickListener { editStoreDialog() }
        }
    }

    fun setOnClickListeners() {
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.ivAdd.setOnClickListener {
            val dialog = AddStoreIngredientDialogFragment(args.storeId)
            { name, ingredient, price, amount, unit ->
                lifecycleScope.launch {
                    val ingredientId = viewModel.addOneIngredient(ingredient)
                    val storeItem = storeItemBuilder(name, ingredientId, price, amount, unit)
                    viewModel.insertStoreItem(storeItem)
                    viewModel.fetchStoreDetails(args.storeId)
                }
            }
            dialog.show(childFragmentManager, Constant.ADD_STORE_ITEM_DIALOG)
        }
    }

    fun setData(details: StoreWithItemsDetails) {
        binding.run {
            tvName.text = details.store.name
            tvLocation.text =
                details.store.location ?: requireContext().getString(R.string.unknown_location)
            adapter.applyStoreItemWithDetails(details.items)
        }
    }

    fun setupAdapter() {
        binding.run {
            adapter = StoreIngredientsAdapter(
                emptyList(),
                { deleteStoreItemDialog(it).show() }
            )
            rvStoreIngredients.adapter = adapter
            rvStoreIngredients.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    fun storeItemBuilder(
        name: String, ingredientId: Int, price: Double, amount: Double,
        unit: String
    ): StoreItem {
        return StoreItem(
            storeId = args.storeId,
            ingredientId = ingredientId,
            name = name,
            price = price,
            packageAmount = amount,
            packageUnit = unit
        )
    }

    fun editStoreDialog() {
        val dialog = AddStoreDialogFragment(storeName, storeLocation) { storeName, storeLocation ->
            lifecycleScope.launch {
                viewModel.updateStore(storeName, storeLocation)
                viewModel.fetchStoreDetails(args.storeId)
            }
        }
        dialog.show(parentFragmentManager, Constant.ADD_STORE_DIALOG)
    }

    fun deleteStoreItemDialog(id: Int): Dialog {
        return Dialog(requireContext()).apply {
            setContentView(R.layout.layout_dialog_confirmation)
            window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
            findViewById<TextView>(R.id.tvConfirm).text =
                requireContext().getString(R.string.delete_item)
            findViewById<MaterialButton>(R.id.mbCancel).setOnClickListener { dismiss() }
            findViewById<MaterialButton>(R.id.mbConfirm).setOnClickListener {
                lifecycleScope.launch(Dispatchers.IO) {
                    viewModel.deleteStoreItem(id)
                    viewModel.fetchStoreDetails(args.storeId)
                }
                dismiss()
            }
        }
    }

    fun deleteStoreDialogCreation(id: Int): Dialog {
        return Dialog(requireContext()).apply {
            setContentView(R.layout.layout_dialog_confirmation)
            window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
            findViewById<TextView>(R.id.tvConfirm).text =
                requireContext().getString(R.string.delete_store)
            findViewById<MaterialButton>(R.id.mbCancel).setOnClickListener { dismiss() }
            findViewById<MaterialButton>(R.id.mbConfirm).setOnClickListener {
                lifecycleScope.launch(Dispatchers.IO) { viewModel.deleteStore(id) }
                findNavController().popBackStack()
                dismiss()
            }
        }
    }
}