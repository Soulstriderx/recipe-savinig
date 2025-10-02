package com.fwrdgrp.recipesaving.ui.managerecipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.fwrdgrp.recipesaving.MyApp
import com.fwrdgrp.recipesaving.data.models.recipe.Ingredient
import com.fwrdgrp.recipesaving.data.models.recipe.Instruction
import com.fwrdgrp.recipesaving.data.models.recipe.Recipe
import com.fwrdgrp.recipesaving.data.models.recipe.RecipeWithDetails
import com.fwrdgrp.recipesaving.data.repo.RecipeRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class EditRecipeViewModel(
    private val repo: RecipeRepo
) : ViewModel() {
    private val _finish = MutableSharedFlow<Unit>()
    val finish: SharedFlow<Unit> = _finish
    private val _error = MutableSharedFlow<String>()
    val error: SharedFlow<String> = _error

    private val _recipeDetails = MutableStateFlow<RecipeWithDetails?>(null)
    val recipeDetails: StateFlow<RecipeWithDetails?> = _recipeDetails

    suspend fun fetchRecipe(id: Int): RecipeWithDetails {
        val details = repo.getRecipeWithDetails(id).first()
        _recipeDetails.value = details
        return details
    }

    suspend fun submitRecipe(
        recipe: Recipe,
        instruction: List<Instruction>,
        ingredients: List<Pair<Ingredient, Pair<Double, String>>>
    ) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                repo.upsertRecipeWithDetails(
                    recipe,
                    instruction,
                    ingredients,
                )
                _finish.emit(Unit)
            }
        } catch (e: Exception) {
            _error.emit(e.message ?: "Unknown error")
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val myRepository = (this[APPLICATION_KEY] as MyApp).repo
                EditRecipeViewModel(repo = myRepository)
            }
        }
    }
}