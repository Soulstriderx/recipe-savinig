package com.fwrdgrp.recipesaving.ui.detailsshoplist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.fwrdgrp.recipesaving.MyApp
import com.fwrdgrp.recipesaving.data.models.recipe.Ingredient
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
    private val recipeRepo: RecipeRepo
) : ViewModel() {

    private val _stores = MutableStateFlow<List<Store>?>(null)
    val stores: StateFlow<List<Store>?> = _stores

    private val _shoppingList = MutableStateFlow<ShoppingListWithStoreAndItems?>(null)
    val shoppingList: StateFlow<ShoppingListWithStoreAndItems?> = _shoppingList

    private val _ingredients = MutableStateFlow<List<Ingredient>?>(null)
    val ingredients: StateFlow<List<Ingredient>?> = _ingredients

    init {
        getStores()
        getIngredients()
    }

    fun getStores() {
        viewModelScope.launch {
            repo.getStores().collect { stores ->
                _stores.update { stores }
            }
        }
    }

    fun getIngredients() {
        viewModelScope.launch {
            recipeRepo.getAllIngredients().collect { ingredients ->
                _ingredients.update { ingredients }
            }
        }
    }

    suspend fun getShoppingList(id: Int) {
        val shopList = repo.getShoppingListWithPrices(id)
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
                val myRepository = (this[APPLICATION_KEY] as MyApp)
                ShopListDetailsViewModel(
                    repo = myRepository.ShoppingRepository,
                    recipeRepo = myRepository.RecipeRepository
                )
            }
        }
    }
}