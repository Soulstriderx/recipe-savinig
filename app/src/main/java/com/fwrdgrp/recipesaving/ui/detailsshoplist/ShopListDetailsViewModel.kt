package com.fwrdgrp.recipesaving.ui.detailsshoplist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.fwrdgrp.recipesaving.MyApp
import com.fwrdgrp.recipesaving.data.models.shopping.ShoppingListItem
import com.fwrdgrp.recipesaving.data.models.shopping.ShoppingListWithStoreAndItems
import com.fwrdgrp.recipesaving.data.models.shopping.Store
import com.fwrdgrp.recipesaving.data.repo.RecipeRepo
import com.fwrdgrp.recipesaving.data.repo.ShoppingRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ShopListDetailsViewModel(
    private val repo: ShoppingRepo,
) : ViewModel() {

    private val _stores = MutableStateFlow<List<Store>?>(null)
    val stores: StateFlow<List<Store>?> = _stores

    private val _shoppingList = MutableStateFlow<ShoppingListWithStoreAndItems?>(null)
    val shoppingList: StateFlow<ShoppingListWithStoreAndItems?> = _shoppingList

    private val _shoppingListItem = MutableStateFlow<List<ShoppingListItem>?>(null)
    val shoppingListItem: StateFlow<List<ShoppingListItem>?> = _shoppingListItem

    init {
        getShoppingItems()
        getStores()
    }

    fun getStores() {
        viewModelScope.launch {
            repo.getStores().collect { stores ->
                _stores.update { stores }
            }
        }
    }

    fun getShoppingItems() {
        viewModelScope.launch {
            repo.getAllShoppingList().collect { items ->
                Log.d("debug", "c$items")
            }
        }
    }

    suspend fun getShoppingList(id: Int) {
        val shopList = repo.getShoppingListWithPrices(id)
        Log.d("debug", "b{$shopList}")
        _shoppingList.value = shopList
    }

    //Update store
    suspend fun changeShoppingListStore(newStore: Store) {
        shoppingList.value?.let {
            repo.updateShoppingList(it.shoppingList.copy(storeId = newStore.id))
        }
    }

    suspend fun addListItem(listId: Int, name: String, amount: Double, unit: String) {
        repo.upsertShoppingListItem(listId, name, amount, unit)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val myRepository = (this[APPLICATION_KEY] as MyApp).ShoppingRepository
                ShopListDetailsViewModel(
                    repo = myRepository,
                )
            }
        }
    }
}