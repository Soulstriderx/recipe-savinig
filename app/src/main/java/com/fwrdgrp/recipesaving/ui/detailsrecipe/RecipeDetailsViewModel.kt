package com.fwrdgrp.recipesaving.ui.detailsrecipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.fwrdgrp.recipesaving.MyApp
import com.fwrdgrp.recipesaving.data.models.recipe.RecipeWithDetails
import com.fwrdgrp.recipesaving.data.repo.RecipeRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class RecipeDetailsViewModel(
    private val repo: RecipeRepo
) : ViewModel() {

    private val _recipeDetails = MutableStateFlow<RecipeWithDetails?>(null)
    val recipeDetails: StateFlow<RecipeWithDetails?> = _recipeDetails

    suspend fun fetchRecipe(id: Int): RecipeWithDetails {
        val details = repo.getRecipeWithDetails(id).first()
        _recipeDetails.value = details
        return details
    }

    fun deleteRecipe() {
        viewModelScope.launch(Dispatchers.IO) {
            _recipeDetails.value?.let { details ->
                repo.deleteRecipeWithDetails(details.recipe)
            }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch(Dispatchers.IO) {
            _recipeDetails.value?.let { details ->
                val updatedRecipe = details.recipe.copy(favorite = !details.recipe.favorite)
                _recipeDetails.value = details.copy(recipe = updatedRecipe)
                repo.toggleFavorite(updatedRecipe)
            }
        }
    }


    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val myRepository = (this[APPLICATION_KEY] as MyApp).repo
                RecipeDetailsViewModel(repo = myRepository)
            }
        }
    }

}