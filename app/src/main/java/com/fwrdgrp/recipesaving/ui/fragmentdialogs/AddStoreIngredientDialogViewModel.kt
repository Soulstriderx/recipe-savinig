package com.fwrdgrp.recipesaving.ui.fragmentdialogs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.fwrdgrp.recipesaving.MyApp
import com.fwrdgrp.recipesaving.data.models.recipe.Ingredient
import com.fwrdgrp.recipesaving.data.models.shopping.StoreItemWithDetails
import com.fwrdgrp.recipesaving.data.repo.RecipeRepo
import com.fwrdgrp.recipesaving.data.repo.ShoppingRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddStoreIngredientDialogViewModel(
    private val recipeRepo: RecipeRepo,
    private val shoppingRepo: ShoppingRepo
) : ViewModel() {
    private val _ingredients = MutableStateFlow<List<Ingredient>?>(null)
    val ingredients: StateFlow<List<Ingredient>?> = _ingredients

    private val _storeItems = MutableStateFlow<List<StoreItemWithDetails>?>(null)
    val storeItems: StateFlow<List<StoreItemWithDetails>?> = _storeItems

    init {
        getIngredients()
    }

    fun getIngredients() {
        viewModelScope.launch {
            recipeRepo.getAllIngredients().collect { ingredients ->
                _ingredients.update { ingredients }
            }
        }
    }

    fun getStoreItems(id: Int) {
        viewModelScope.launch {
            shoppingRepo.getAllStoreItemsWithDetailsByStoreId(id).collect { items ->
                _storeItems.update { items }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val myRepository = (this[APPLICATION_KEY] as MyApp)
                AddStoreIngredientDialogViewModel(
                    recipeRepo = myRepository.RecipeRepository,
                    shoppingRepo = myRepository.ShoppingRepository
                )
            }
        }
    }
}