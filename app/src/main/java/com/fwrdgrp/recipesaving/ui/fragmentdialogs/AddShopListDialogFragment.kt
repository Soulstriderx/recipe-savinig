package com.fwrdgrp.recipesaving.ui.fragmentdialogs

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.fwrdgrp.recipesaving.R
import com.fwrdgrp.recipesaving.data.models.shopping.Store
import com.fwrdgrp.recipesaving.databinding.FragmentAddShopListDialogBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlin.getValue

class AddShopListDialogFragment(
    private val shopListName: String = "",
    private val storeId: Int = 0,
    private val shopListData: (listName: String, store: Store) -> Unit
) : DialogFragment() {
    private val viewModel: AddShopListDialogViewModel by viewModels {
        AddShopListDialogViewModel.Factory
    }
    private lateinit var binding: FragmentAddShopListDialogBinding
    private lateinit var spinnerAdapter: ArrayAdapter<Store>

    private var selectedStore: Store? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddShopListDialogBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.etListName.setText(shopListName)
        lifecycleScope.launch {
            viewModel.stores.filterNotNull().collect {
                setupSpinner(it)
            }
        }
        setupListeners()
        lifecycleScope.launch {
            viewModel.error.collect {
                showError(it)
            }
        }
    }

    fun setupListeners() {
        binding.run {
            mbCancel.setOnClickListener { dismiss() }
            mbAdd.setOnClickListener {
                val name = etListName.text.toString()
                val store = selectedStore

                lifecycleScope.launch {
                    val isValid = viewModel.validateField(name)
                    if (!isValid || store == null) return@launch

                    shopListData(name, store)
                    dismiss()
                }
            }
        }
    }

    fun showError(msg: String) {
        Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).setBackgroundTint(
                ContextCompat.getColor(requireContext(), R.color.color_error)
        ).setTextColor(Color.WHITE).show()
    }

    fun setupSpinner(stores: List<Store>) {
        binding.run {
            val validStores = if (stores.isNotEmpty()) stores else {
                spStoreName.isEnabled = false
                mbAdd.isEnabled = false
                listOf(Store(name = "Please create a store", location = ""))
            }
            spinnerAdapter = ArrayAdapter(
                requireContext(), android.R.layout.simple_spinner_item,
                validStores
            )
            val selectedIndex = validStores.indexOfFirst { it.id == storeId }.takeIf { it >= 0 } ?: 0
            spStoreName.post {
                spStoreName.setSelection(selectedIndex, false)
                selectedStore = validStores[selectedIndex]
            }
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spStoreName.adapter = spinnerAdapter
            spStoreName.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    selectedStore = spinnerAdapter.getItem(position)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }
}