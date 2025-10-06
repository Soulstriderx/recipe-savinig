package com.fwrdgrp.recipesaving.ui.managerecipe

import android.net.Uri
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class EditRecipeViewModel(
    repo: RecipeRepo
) : BaseManageRecipeViewModel(repo) {

    private val _recipeDetails = MutableStateFlow<RecipeWithDetails?>(null)

    suspend fun fetchRecipe(id: Int): RecipeWithDetails {
        val details = repo.getRecipeWithDetails(id).first()
        _recipeDetails.value = details
        return details
    }

    override suspend fun submitRecipe(
        recipe: Recipe,
        instruction: List<Instruction>,
        ingredients: List<Pair<Ingredient, Pair<Double, String>>>,
        image: Uri?
    ) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                repo.upsertRecipeWithDetails(
                    recipe.copy(imageUri = image.toString()),
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
                val myRepository = (this[APPLICATION_KEY] as MyApp).RecipeRepository
                EditRecipeViewModel(repo = myRepository)
            }
        }
    }
}