package com.fwrdgrp.recipesaving.ui.detailsshoplist

import android.animation.ValueAnimator
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.fwrdgrp.recipesaving.R
import com.fwrdgrp.recipesaving.data.enums.Units
import com.fwrdgrp.recipesaving.data.models.shopping.ShoppingListItem
import com.fwrdgrp.recipesaving.data.models.shopping.Store
import com.fwrdgrp.recipesaving.databinding.FragmentShopListDetailsBinding
import com.fwrdgrp.recipesaving.ui.adapters.ShopListDetailsAdapter
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
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
            ivDelete.setOnClickListener { deleteShopListDialog(args.shopListId).show() }
        }
    }

    fun setupAdapter() {
        storeId?.let { storeId ->
            adapter = ShopListDetailsAdapter(
                emptyList(),
                storeId,
                { item, boolean ->
                    lifecycleScope.launch {
                        viewModel.toggleBought(item.shoppingListItem.copy(bought = boolean))
                        delay(700)
                        viewModel.getShoppingList(args.shopListId)
                    }
                },
                { deleteShopItemDialog(it).show() }
            )
        }
        binding.run {
            binding.rvShopListDetails.adapter = adapter
            binding.rvShopListDetails.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    fun setupSpinner(stores: List<Store>) {
        binding.run {
            val validStores = if (stores.isNotEmpty()) {
                listOf(Store(id = -1, name = "Select a store", location = "")) + stores
            } else {
                spStoreName.isEnabled = false
                listOf(Store(id = -1, name = "Please create a store", location = ""))
            }
            spinnerAdapter = ArrayAdapter(
                requireContext(), android.R.layout.simple_spinner_item,
                validStores
            )
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spStoreName.adapter = spinnerAdapter
            spStoreName.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                var firstSelection = true
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int,
                    id: Long
                ) {
                    val selected = spinnerAdapter.getItem(position) ?: return
                    val selectedStore = validStores[position]
                    if (firstSelection) {
                        firstSelection = false
                        return
                    }
                    if (selectedStore.id == -1) return
                    lifecycleScope.launch {
                        viewModel.changeShoppingListStore(selected)
                        viewModel.getShoppingList(args.shopListId)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }

    fun deleteShopItemDialog(item: ShoppingListItem): Dialog {
        return Dialog(requireContext()).apply {
            setContentView(R.layout.layout_dialog_confirmation)
            window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
            findViewById<TextView>(R.id.tvConfirm).setText(R.string.are_you_sure_delete_shop_item)
            findViewById<MaterialButton>(R.id.mbCancel).setOnClickListener { dismiss() }
            findViewById<MaterialButton>(R.id.mbConfirm).setOnClickListener {
                lifecycleScope.launch {
                    viewModel.deleteShopItem(item)
                    viewModel.getShoppingList(args.shopListId)
                }
                dismiss()
            }
        }
    }

    fun deleteShopListDialog(listId: Int): Dialog {
        return Dialog(requireContext()).apply {
            setContentView(R.layout.layout_dialog_confirmation)
            window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
            findViewById<TextView>(R.id.tvConfirm).setText(R.string.are_you_sure_delete_shop_list)
            findViewById<MaterialButton>(R.id.mbCancel).setOnClickListener { dismiss() }
            findViewById<MaterialButton>(R.id.mbConfirm).setOnClickListener {
                lifecycleScope.launch { viewModel.deleteShopListById(listId) }
                findNavController().popBackStack()
                dismiss()
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
            val currentList = viewModel.shoppingList.value ?: return@launch
            val lowerCaseInput = ingredient.trim().lowercase()
            val duplicateItem = currentList.items.any {
                it.ingredient.name.trim().lowercase() == lowerCaseInput
            }
            if (duplicateItem) {
                showError(ingredient)
                return@launch
            }
            viewModel.addListItem(args.shopListId, ingredient, amount, unit)
            viewModel.getShoppingList(args.shopListId)

        }
    }

    fun showError(name: String) {
        Snackbar.make(binding.root, "\"$name\" already exist.", Snackbar.LENGTH_SHORT)
            .setBackgroundTint(
                ContextCompat.getColor(
                    requireContext(), R.color.color_error
                )
            ).setTextColor(Color.WHITE).show()
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