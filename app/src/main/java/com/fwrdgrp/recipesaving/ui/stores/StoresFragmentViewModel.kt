package com.fwrdgrp.recipesaving.ui.stores

import android.util.Log
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

class StoresFragmentViewModel(
    private val repo: ShoppingRepo
): ViewModel() {
    private val _stores = MutableStateFlow<List<Store>>(emptyList())
    val stores: StateFlow<List<Store>> = _stores

    init {
        getStores()
    }

    fun getStores() {
        viewModelScope.launch {
            repo.getStores().collect { storeList ->
                _stores.update { storeList }
            }
        }
    }

    suspend fun deleteStore(id: Int) {
        repo.deleteStoreById(id)
        getStores()
    }

    suspend fun addStore(storeName: String, storeLocation: String?) {
        repo.upsertStore(Store(name = storeName,location = storeLocation))
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