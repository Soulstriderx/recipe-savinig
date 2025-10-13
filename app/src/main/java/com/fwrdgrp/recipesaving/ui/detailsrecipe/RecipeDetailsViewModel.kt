package com.fwrdgrp.recipesaving.ui.detailsrecipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.fwrdgrp.recipesaving.MyApp
import com.fwrdgrp.recipesaving.data.models.recipe.RecipeWithDetails
import com.fwrdgrp.recipesaving.data.models.shopping.ShoppingList
import com.fwrdgrp.recipesaving.data.repo.RecipeRepo
import com.fwrdgrp.recipesaving.data.repo.ShoppingRepo
import com.fwrdgrp.recipesaving.data.utils.Constant
import com.fwrdgrp.recipesaving.data.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class RecipeDetailsViewModel(
    private val recipeRepo: RecipeRepo,
    private val shoppingRepo: ShoppingRepo,
    private val utils: Utils = Utils()
) : ViewModel() {

    private val _recipeDetails = MutableStateFlow<RecipeWithDetails?>(null)
    val recipeDetails: StateFlow<RecipeWithDetails?> = _recipeDetails

    private val _error = MutableSharedFlow<String>()
    val error: SharedFlow<String> = _error

    private val _shoppingListId = MutableSharedFlow<Int>()
    val shoppingListId: MutableSharedFlow<Int> = _shoppingListId

    suspend fun fetchRecipe(id: Int): RecipeWithDetails {
        val details = recipeRepo.getRecipeWithDetails(id).first()
        _recipeDetails.value = details
        return details
    }

    fun deleteRecipe() {
        viewModelScope.launch(Dispatchers.IO) {
            _recipeDetails.value?.let { details ->
                recipeRepo.deleteRecipeWithDetails(details.recipe)
            }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch(Dispatchers.IO) {
            _recipeDetails.value?.let { details ->
                val updatedRecipe = details.recipe.copy(favorite = !details.recipe.favorite)
                _recipeDetails.value = details.copy(recipe = updatedRecipe)
                recipeRepo.toggleFavorite(updatedRecipe)
            }
        }
    }

    //Below this is the Shopping List Generator
    fun addShopList() {
        val item = _recipeDetails.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val storeItems = shoppingRepo.getAllStoreItems().first()
            val storeId: Int = utils.generateStoreId(item, storeItems)
            if (storeId == -1) {
                viewModelScope.launch { _error.emit(Constant.DONT_HAVE_ITEM) }
                return@launch
            }
            val shopList: ShoppingList = buildShopList(item.recipe.title, storeId)
            val shopListId = shoppingRepo.upsertShoppingList(shopList)
            item.ingredients.forEach {
                addShopListItemsToShopList(
                    shopListId, it.ingredient.id, it.amount ?: 0.0, it.unit ?: "pcs"
                )
            }
            _shoppingListId.emit(shopListId)
        }
    }

    suspend fun addShopListItemsToShopList(
        listId: Int,
        ingredientId: Int,
        amount: Double,
        unit: String
    ) {
        shoppingRepo.insertGeneratedShopListItem(
            listId, ingredientId, amount, unit
        )
    }

    fun buildShopList(name: String, storeId: Int): ShoppingList {
        return ShoppingList(
            name = name, dateCreated = System.currentTimeMillis(), storeId = storeId
        )
    }


    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val myRepository = (this[APPLICATION_KEY] as MyApp)
                RecipeDetailsViewModel(
                    recipeRepo = myRepository.RecipeRepository,
                    shoppingRepo = myRepository.ShoppingRepository
                )
            }
        }
    }

}