package com.fwrdgrp.recipesaving.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.fwrdgrp.recipesaving.MyApp
import com.fwrdgrp.recipesaving.data.models.shopping.ShoppingList
import com.fwrdgrp.recipesaving.data.models.shopping.ShoppingListWithStoreAndItems
import com.fwrdgrp.recipesaving.data.repo.ShoppingRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repo: ShoppingRepo
) : ViewModel() {

    private val _shoppingList = MutableStateFlow<List<ShoppingListWithStoreAndItems>?>(null)
    val shoppingList: StateFlow<List<ShoppingListWithStoreAndItems>?> = _shoppingList

    fun getShoppingLists() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getAllShoppingListWithStoreAndItems().collect { shoppingLists ->
                _shoppingList.update { shoppingLists }
            }
        }
    }

    suspend fun addShopList(name: String, storeId: Int) {
        repo.upsertShoppingList(
            ShoppingList(
                name = name,
                dateCreated = System.currentTimeMillis(),
                storeId = storeId
            )
        )
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val myRepository = (this[APPLICATION_KEY] as MyApp).ShoppingRepository
                HomeViewModel(repo = myRepository)
            }
        }
    }
}