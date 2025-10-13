package com.fwrdgrp.recipesaving.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.fwrdgrp.recipesaving.MyApp
import com.fwrdgrp.recipesaving.data.enums.ShopListFilter
import com.fwrdgrp.recipesaving.data.enums.SortOrder
import com.fwrdgrp.recipesaving.data.models.shopping.ShoppingList
import com.fwrdgrp.recipesaving.data.models.shopping.ShoppingListWithStoreAndItems
import com.fwrdgrp.recipesaving.data.repo.ShoppingRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repo: ShoppingRepo
) : ViewModel() {

    var currentSearch = ""
    var currentFilter = ShopListFilter.DATE
    var currentOrder = SortOrder.ASCENDING

    private val _shoppingList = MutableStateFlow<List<ShoppingListWithStoreAndItems>?>(null)
    val shoppingList: StateFlow<List<ShoppingListWithStoreAndItems>?> = _shoppingList

    fun getShoppingLists() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getAllShoppingListWithStoreAndItems().map {
                it.filter {
                    currentSearch.isBlank() || it.shoppingList.name.contains(currentSearch, ignoreCase = true)
                }.applySort(currentOrder, currentFilter)
            }.collect { shoppingLists ->
                _shoppingList.update { shoppingLists }
            }
        }
    }

    suspend fun addShopList(name: String, storeId: Int) {
        repo.insertShoppingList(
            ShoppingList(
                name = name,
                dateCreated = System.currentTimeMillis(),
                storeId = storeId
            )
        )
    }

    fun setSearch(str: String) {
        currentSearch = str
        getShoppingLists()
    }

    fun setFilter(filter: ShopListFilter, sortOrder: SortOrder) {
        currentFilter = filter
        currentOrder = sortOrder
        getShoppingLists()
    }

    fun List<ShoppingListWithStoreAndItems>.applySort(
        sortOrder: SortOrder, filter: ShopListFilter
    ): List<ShoppingListWithStoreAndItems> {
        val sort = when (filter) {
            ShopListFilter.DATE -> sortByDate(sortOrder)
            ShopListFilter.ALPHABETICALLY -> sortByName(sortOrder)
            ShopListFilter.SIZE -> sortBySize(sortOrder)
        }
        return sort
    }

    private fun List<ShoppingListWithStoreAndItems>.sortByName(order: SortOrder): List<ShoppingListWithStoreAndItems> = when (order) {
        SortOrder.ASCENDING -> sortedBy { it.shoppingList.name }
        SortOrder.DESCENDING -> sortedByDescending { it.shoppingList.name }
    }

    private fun List<ShoppingListWithStoreAndItems>.sortByDate(order: SortOrder): List<ShoppingListWithStoreAndItems> = when (order) {
        SortOrder.ASCENDING -> sortedBy { it.shoppingList.dateCreated }
        SortOrder.DESCENDING -> sortedByDescending { it.shoppingList.dateCreated }
    }

    private fun List<ShoppingListWithStoreAndItems>.sortBySize(order: SortOrder): List<ShoppingListWithStoreAndItems> = when (order) {
        SortOrder.ASCENDING -> sortedBy { it.items.size }
        SortOrder.DESCENDING -> sortedByDescending { it.items.size }
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