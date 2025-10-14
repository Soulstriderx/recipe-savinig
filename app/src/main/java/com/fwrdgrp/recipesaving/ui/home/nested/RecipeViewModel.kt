package com.fwrdgrp.recipesaving.ui.home.nested

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.fwrdgrp.recipesaving.MyApp
import com.fwrdgrp.recipesaving.data.enums.RecipeFilter
import com.fwrdgrp.recipesaving.data.enums.SortOrder
import com.fwrdgrp.recipesaving.data.models.recipe.Recipe
import com.fwrdgrp.recipesaving.data.repo.RecipeRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RecipeViewModel(
    private val repo: RecipeRepo
) : ViewModel() {
    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes: StateFlow<List<Recipe>> = _recipes

    var currentSearch = ""
    var currentFilter = RecipeFilter.DATE
    var currentOrder = SortOrder.ASCENDING

    init {
        getRecipe()
    }

    fun getRecipe() {
        viewModelScope.launch {
            repo.getAllRecipes().map {
                it.filter {
                    currentSearch.isBlank() || it.title.contains(currentSearch, ignoreCase = true)
                }.applySort(currentOrder, currentFilter)
            }.collect { recipesList ->
                _recipes.update { recipesList }
            }
        }
    }

    fun randomRecipe(): Int {
        val randomRecipe = _recipes.value.random()
        return randomRecipe.id
    }

    fun setSearch(str: String) {
        currentSearch = str
        getRecipe()
    }

    fun setFilter(filter: RecipeFilter, sortOrder: SortOrder) {
        currentFilter = filter
        currentOrder = sortOrder
        getRecipe()
    }

    fun List<Recipe>.applySort(sortOrder: SortOrder, filter: RecipeFilter): List<Recipe> {
        val sort = when (filter) {
            RecipeFilter.DATE -> sortByDate(sortOrder)
            RecipeFilter.ALPHABETICALLY -> sortByName(sortOrder)
            RecipeFilter.TIME -> sortByTime(sortOrder)
        }
        return sort.sortedByDescending { it.favorite }
    }

    private fun List<Recipe>.sortByName(order: SortOrder): List<Recipe> = when (order) {
        SortOrder.ASCENDING -> sortedBy { it.title.lowercase() }
        SortOrder.DESCENDING -> sortedByDescending { it.title.lowercase() }
    }

    private fun List<Recipe>.sortByDate(order: SortOrder): List<Recipe> = when (order) {
        SortOrder.ASCENDING -> sortedBy { it.id }
        SortOrder.DESCENDING -> sortedByDescending { it.id }
    }

    private fun List<Recipe>.sortByTime(order: SortOrder): List<Recipe> = when (order) {
        SortOrder.ASCENDING -> sortedBy { it.estTime }
        SortOrder.DESCENDING -> sortedByDescending { it.estTime }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val myRepository = (this[APPLICATION_KEY] as MyApp).RecipeRepository
                RecipeViewModel(repo = myRepository)
            }
        }
    }
}