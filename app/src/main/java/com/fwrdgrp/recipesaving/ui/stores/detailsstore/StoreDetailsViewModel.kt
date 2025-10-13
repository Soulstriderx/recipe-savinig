package com.fwrdgrp.recipesaving.ui.stores.detailsstore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.fwrdgrp.recipesaving.MyApp
import com.fwrdgrp.recipesaving.data.models.recipe.Ingredient
import com.fwrdgrp.recipesaving.data.models.shopping.StoreItem
import com.fwrdgrp.recipesaving.data.models.shopping.StoreWithItemsDetails
import com.fwrdgrp.recipesaving.data.repo.RecipeRepo
import com.fwrdgrp.recipesaving.data.repo.ShoppingRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class StoreDetailsViewModel(
    private val shoppingRepo: ShoppingRepo,
    private val recipeRepo: RecipeRepo
) : ViewModel() {

    private val _storeDetails = MutableStateFlow<StoreWithItemsDetails?>(null)
    val storeDetails: StateFlow<StoreWithItemsDetails?> = _storeDetails

    suspend fun fetchStoreDetails(id: Int): StoreWithItemsDetails {
        val details = shoppingRepo.getStoreWithItemDetails(id)
        _storeDetails.value = details
        return details
    }

    suspend fun deleteStore(id: Int) {
        shoppingRepo.deleteStoreById(id)
    }

    suspend fun addOneIngredient(name: String): Int {
        val exist = recipeRepo.getIngredientByName(name)
        return exist?.id ?: recipeRepo.upsertSingleIngredient(Ingredient(name = name)).toInt()
    }

    suspend fun insertStoreItem(storeItem: StoreItem) {
        shoppingRepo.insertStoreItem(storeItem)
    }

    suspend fun deleteStoreItem(id: Int) {
        shoppingRepo.deleteStoreItem(id)
    }

    suspend fun updateStore(storeName: String, storeLocation: String?) {
        _storeDetails.value?.store?.let {
            shoppingRepo.updateStore(it.copy(name = storeName, location = storeLocation))
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val myRepository = (this[APPLICATION_KEY] as MyApp)
                StoreDetailsViewModel(
                    shoppingRepo = myRepository.ShoppingRepository,
                    recipeRepo = myRepository.RecipeRepository
                )
            }
        }
    }
}