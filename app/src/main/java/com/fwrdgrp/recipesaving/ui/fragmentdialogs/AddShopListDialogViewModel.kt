package com.fwrdgrp.recipesaving.ui.fragmentdialogs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.fwrdgrp.recipesaving.MyApp
import com.fwrdgrp.recipesaving.data.models.shopping.Store
import com.fwrdgrp.recipesaving.data.repo.ShoppingRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddShopListDialogViewModel(
    private val repo: ShoppingRepo
) : ViewModel() {
    private val _stores = MutableStateFlow<List<Store>?>(null)
    val stores: StateFlow<List<Store>?> = _stores

    init {
        getStores()
    }

    fun getStores() {
        viewModelScope.launch {
            repo.getStores().collect { store ->
                _stores.update { store }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val myRepository = (this[APPLICATION_KEY] as MyApp).ShoppingRepository
                AddShopListDialogViewModel(repo = myRepository)
            }
        }
    }
}