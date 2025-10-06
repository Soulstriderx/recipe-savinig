package com.fwrdgrp.recipesaving.ui.stores.detailsstore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.fwrdgrp.recipesaving.MyApp
import com.fwrdgrp.recipesaving.data.models.shopping.StoreWithItemsDetails
import com.fwrdgrp.recipesaving.data.repo.ShoppingRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class StoreDetailsViewModel(
    private val repo: ShoppingRepo
): ViewModel() {

    private val _storeDetails = MutableStateFlow<StoreWithItemsDetails?>(null)
    val storeDetails: StateFlow<StoreWithItemsDetails?> = _storeDetails

    suspend fun fetchStoreDetails(id: Int): StoreWithItemsDetails {
        val details = repo.getStoreWithItemDetails(id)
        _storeDetails.value = details
        return details
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val myRepository = (this[APPLICATION_KEY] as MyApp).ShoppingRepository
                StoreDetailsViewModel(repo = myRepository)
            }
        }
    }
}