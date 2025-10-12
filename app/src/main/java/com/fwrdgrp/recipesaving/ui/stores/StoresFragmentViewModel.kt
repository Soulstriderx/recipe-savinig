package com.fwrdgrp.recipesaving.ui.stores

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
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.collections.filter

class StoresFragmentViewModel(
    private val repo: ShoppingRepo
) : ViewModel() {
    private val _stores = MutableStateFlow<List<Store>>(emptyList())
    val stores: StateFlow<List<Store>> = _stores

    var currentSearch = ""

    init {
        getStores()
    }

    fun getStores() {
        viewModelScope.launch {
            repo.getStores().map {
                it.filter {
                    currentSearch.isBlank() || it.name.contains(
                        currentSearch,
                        ignoreCase = true
                    )
                }
            }.collect { storeList ->
                _stores.update { storeList }
            }
        }
    }

    suspend fun deleteStore(id: Int) {
        repo.deleteStoreById(id)
        getStores()
    }

    suspend fun addStore(storeName: String, storeLocation: String?) {
        repo.upsertStore(Store(name = storeName, location = storeLocation))
    }

    fun setSearch(str: String) {
        currentSearch = str
        getStores()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val myRepository = (this[APPLICATION_KEY] as MyApp).ShoppingRepository
                StoresFragmentViewModel(repo = myRepository)
            }
        }
    }
}