package com.fwrdgrp.recipesaving.data.repo

import androidx.room.Transaction
import com.fwrdgrp.recipesaving.data.models.recipe.Ingredient
import com.fwrdgrp.recipesaving.data.models.recipe.Instruction
import com.fwrdgrp.recipesaving.data.models.recipe.Recipe
import com.fwrdgrp.recipesaving.data.models.recipe.RecipeWithDetails
import com.fwrdgrp.recipesaving.data.utils.RecipeRepoUtils
import com.fwrdgrp.recipesaving.database.RecipeDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class RecipeRepo(
    private val dao: RecipeDao,
) {
    private val utils = RecipeRepoUtils(dao)

    @Transaction
    suspend fun addRecipeWithDetails(
        recipe: Recipe,
        instruction: List<Instruction>,
        ingredients: List<Pair<Ingredient , Pair<Double, String>>>
    ) {
        val recipeId = dao.insertRecipe(recipe).toInt()
        utils.addInstructions(instruction, recipeId)
        utils.addIngredients(ingredients, recipeId)
    }

    @Transaction
    suspend fun deleteRecipeWithDetails(recipe: Recipe) {
        dao.deleteInstructionsByRecipeId(recipe.id)
        dao.deleteRecipeIngredientsByRecipeId(recipe.id)
        dao.deleteRecipe(recipe)
    }

    @Transaction
    suspend fun upsertRecipeWithDetails(
        recipe: Recipe,
        instruction: List<Instruction>,
        ingredients: List<Pair<Ingredient , Pair<Double, String>>>
    ) {
        val recipeId = if (recipe.id == 0) { dao.insertRecipe(recipe).toInt() } else {
            dao.updateRecipe(recipe)
            recipe.id
        }

        dao.deleteRecipeIngredientsByRecipeId(recipeId)
        dao.deleteInstructionsByRecipeId(recipeId)

        utils.addInstructions(instruction, recipeId)
        utils.addIngredients(ingredients, recipeId)
    }

    //details page
    @Transaction
    fun getRecipeWithDetails(id: Int): Flow<RecipeWithDetails> {
        val recipeFlow = dao.getRecipeWithDetails(id)
        val ingredientsFlow = dao.getIngredientsForRecipe(id)

        return combine(recipeFlow, ingredientsFlow) { recipeDetails, ingredients ->
            recipeDetails.copy(ingredients = ingredients)
        }
    }

    @Transaction
    fun getAllRecipes(): Flow<List<Recipe>> {
        return dao.getAllRecipes()
    }
}