package com.fwrdgrp.recipesaving.ui.detailsshoplist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.fwrdgrp.recipesaving.MyApp
import com.fwrdgrp.recipesaving.data.models.recipe.Ingredient
import com.fwrdgrp.recipesaving.data.models.shopping.ShoppingListItem
import com.fwrdgrp.recipesaving.data.models.shopping.ShoppingListWithStoreAndItems
import com.fwrdgrp.recipesaving.data.models.shopping.Store
import com.fwrdgrp.recipesaving.data.repo.RecipeRepo
import com.fwrdgrp.recipesaving.data.repo.ShoppingRepo
import com.fwrdgrp.recipesaving.data.utils.Constant
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
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

    private val _error = MutableSharedFlow<String>()
    val error: SharedFlow<String> = _error

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
        val sorted = shopList.items.sortedByDescending { it.storeItems.size }
            .sortedBy { it.shoppingListItem.bought }
        _shoppingList.value = shopList.copy(items = sorted)
    }

    //Update store
    suspend fun changeShoppingListStore(newStore: Store) {
        if (newStore.id < 1) return
        shoppingList.value?.let {
            repo.updateShoppingList(it.shoppingList.copy(storeId = newStore.id))
        }
    }

    suspend fun updateShoppingListName(name: String, newStoreId: Int) {
        shoppingList.value?.let {
            repo.updateShoppingList(it.shoppingList.copy(name = name, storeId = newStoreId))
        }
    }

    suspend fun addListItem(
        listId: Int, name: String, amount: Double, unit: String,
        currentList: ShoppingListWithStoreAndItems
    ) {
        try {
            require(name.isNotBlank()) { Constant.NO_ING }
            require(amount > 0) { Constant.NO_AMOUNT }
            checkDupe(name, currentList)

            val ingredientId = addOneIngredient(name)
            val shoppingListItem = buildShoppingListItem(listId, ingredientId, amount, unit)

            repo.upsertShoppingListItem(shoppingListItem)
        } catch (e: Exception) {
            _error.emit(e.message ?: Constant.UNKNOWN)
        }
    }

    suspend fun addOneIngredient(name: String): Int {
        val exist = recipeRepo.getIngredientByName(name)
        return exist?.id ?: recipeRepo.upsertSingleIngredient(Ingredient(name = name)).toInt()
    }

    fun buildShoppingListItem(
        listId: Int, ingredientId: Int, amount: Double, unit: String
    ): ShoppingListItem {
        return ShoppingListItem(
            listId = listId,
            ingredientId = ingredientId,
            amountNeeded = amount,
            neededUnit = unit
        )
    }

    fun checkDupe(ingredient: String, currentList: ShoppingListWithStoreAndItems) {
        val lowerCaseInput = ingredient.trim().lowercase()
        val duplicateItem = currentList.items.any {
            it.ingredient.name.trim().lowercase() == lowerCaseInput
        }
        require(duplicateItem == false) { Constant.INGREDIENT_EXIST }
    }

    suspend fun toggleBought(item: ShoppingListItem) {
        repo.toggleBought(item)
    }

    suspend fun deleteShopItem(item: ShoppingListItem) {
        repo.deleteShoppingListItem(item)
    }

    suspend fun deleteShopListById(listId: Int) {
        repo.deleteShoppingListById(listId)
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