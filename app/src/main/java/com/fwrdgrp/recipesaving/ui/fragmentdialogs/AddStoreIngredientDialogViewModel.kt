package com.fwrdgrp.recipesaving.ui.fragmentdialogs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.fwrdgrp.recipesaving.MyApp
import com.fwrdgrp.recipesaving.data.models.recipe.Ingredient
import com.fwrdgrp.recipesaving.data.repo.RecipeRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddStoreIngredientDialogViewModel(
    private val repo: RecipeRepo
) : ViewModel() {
    private val _ingredients = MutableStateFlow<List<Ingredient>?>(null)
    val ingredients: StateFlow<List<Ingredient>?> = _ingredients

    init {
        getIngredients()
    }

    fun getIngredients() {
        viewModelScope.launch {
            repo.getAllIngredients().collect { ingredients ->
                _ingredients.update { ingredients }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val myRepository = (this[APPLICATION_KEY] as MyApp).RecipeRepository
                AddStoreIngredientDialogViewModel(repo = myRepository)
            }
        }
    }
}