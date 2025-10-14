package com.fwrdgrp.recipesaving.data.repo

import androidx.room.Transaction
import com.fwrdgrp.recipesaving.data.models.recipe.Ingredient
import com.fwrdgrp.recipesaving.data.models.recipe.Instruction
import com.fwrdgrp.recipesaving.data.models.recipe.Recipe
import com.fwrdgrp.recipesaving.data.models.recipe.RecipeIngredient
import com.fwrdgrp.recipesaving.data.models.recipe.RecipeWithDetails
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
    //Recipe
    suspend fun addRecipeWithDetails(recipe: Recipe): Int = recipeDao.insertRecipe(recipe).toInt()

    @Transaction
    suspend fun upsertRecipeWithDetails(recipe: Recipe): Int {
        return if (recipe.id == 0) {
            recipeDao.insertRecipe(recipe).toInt()
        } else {
            recipeDao.updateRecipe(recipe)
            recipe.id
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

    //Ingredients
    @Transaction
    fun getAllIngredients(): Flow<List<Ingredient>> = ingredientDao.getAllIngredients()

    suspend fun getIngredientByName(name: String) = ingredientDao.getIngredientByName(name)

    suspend fun insertRecipeIngredient(recipeIngredient: RecipeIngredient) =
        ingredientDao.insertRecipeIngredient(recipeIngredient)

    suspend fun upsertSingleIngredient(ingredient: Ingredient) =
        ingredientDao.upsertIngredient(ingredient)

    suspend fun deleteRecipeIngredientsByRecipeId(recipeId: Int) =
        ingredientDao.deleteRecipeIngredientsByRecipeId(recipeId)

    //Instructions
    suspend fun insertInstruction(instruction: Instruction) =
        instructionDao.insertInstruction(instruction)

    suspend fun deleteInstructionsByRecipeId(recipeId: Int) =
        instructionDao.deleteInstructionsByRecipeId(recipeId)

    //Combined Queries
    @Transaction
    suspend fun deleteRecipeWithDetails(recipe: Recipe) {
        instructionDao.deleteInstructionsByRecipeId(recipe.id)
        ingredientDao.deleteRecipeIngredientsByRecipeId(recipe.id)
        recipeDao.deleteRecipe(recipe)
    }

    @Transaction
    fun getRecipeWithDetails(id: Int): Flow<RecipeWithDetails> {
        val recipeFlow = recipeDao.getRecipeWithDetails(id)
        val ingredientsFlow = ingredientDao.getIngredientsForRecipe(id)

        return combine(recipeFlow, ingredientsFlow) { recipeDetails, ingredients ->
            recipeDetails.fillIngredients(ingredients)
        }
    }


}