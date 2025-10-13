package com.fwrdgrp.recipesaving.data.repo

import androidx.room.Transaction
import com.fwrdgrp.recipesaving.data.models.recipe.Ingredient
import com.fwrdgrp.recipesaving.data.models.recipe.Instruction
import com.fwrdgrp.recipesaving.data.models.recipe.Recipe
import com.fwrdgrp.recipesaving.data.models.recipe.RecipeWithDetails
import com.fwrdgrp.recipesaving.data.utils.RecipeRepoUtils
import com.fwrdgrp.recipesaving.database.recipedao.IngredientDao
import com.fwrdgrp.recipesaving.database.recipedao.InstructionDao
import com.fwrdgrp.recipesaving.database.recipedao.RecipeDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class RecipeRepo(
    private val recipeDao: RecipeDao,
    private val ingredientDao: IngredientDao,
    private val instructionDao: InstructionDao
) {
    private val utils = RecipeRepoUtils(recipeDao, ingredientDao, instructionDao)

    @Transaction
    fun getAllIngredients(): Flow<List<Ingredient>> {
        return ingredientDao.getAllIngredients()
    }

    //add
    @Transaction
    suspend fun addRecipeWithDetails(recipe: Recipe, instruction: List<Instruction>,
        ingredients: List<Pair<Ingredient , Pair<Double, String>>>
    ) {
        val recipeId = recipeDao.insertRecipe(recipe).toInt()
        utils.addInstructions(instruction, recipeId)
        utils.addIngredients(ingredients, recipeId)
    }

    @Transaction
    suspend fun upsertIngredient(ingredientName: String): Int {
        return utils.addSingleIngredients(ingredientName)
    }

    //delete
    @Transaction
    suspend fun deleteRecipeWithDetails(recipe: Recipe) {
        instructionDao.deleteInstructionsByRecipeId(recipe.id)
        ingredientDao.deleteRecipeIngredientsByRecipeId(recipe.id)
        recipeDao.deleteRecipe(recipe)
    }

    //edit
    @Transaction
    suspend fun upsertRecipeWithDetails(
        recipe: Recipe,
        instruction: List<Instruction>,
        ingredients: List<Pair<Ingredient , Pair<Double, String>>>
    ) {
        val recipeId = if (recipe.id == 0) { recipeDao.insertRecipe(recipe).toInt() } else {
            recipeDao.updateRecipe(recipe)
            recipe.id
        }

        ingredientDao.deleteRecipeIngredientsByRecipeId(recipeId)
        instructionDao.deleteInstructionsByRecipeId(recipeId)

        utils.addInstructions(instruction, recipeId)
        utils.addIngredients(ingredients, recipeId)
    }

    //details page
    @Transaction
    fun getRecipeWithDetails(id: Int): Flow<RecipeWithDetails> {
        val recipeFlow = recipeDao.getRecipeWithDetails(id)
        val ingredientsFlow = ingredientDao.getIngredientsForRecipe(id)

        return combine(recipeFlow, ingredientsFlow) { recipeDetails, ingredients ->
            recipeDetails.fillIngredients(ingredients)
        }
    }

    @Transaction
    fun getAllRecipes(): Flow<List<Recipe>> {
        return recipeDao.getAllRecipes()
    }

    @Transaction
    suspend fun toggleFavorite(recipe: Recipe) {
        recipeDao.updateRecipe(recipe)
    }
}