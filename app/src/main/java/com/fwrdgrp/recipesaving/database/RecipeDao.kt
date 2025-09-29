package com.fwrdgrp.recipesaving.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.fwrdgrp.recipesaving.data.models.recipe.Ingredient
import com.fwrdgrp.recipesaving.data.models.recipe.IngredientWithAmount
import com.fwrdgrp.recipesaving.data.models.recipe.Instruction
import com.fwrdgrp.recipesaving.data.models.recipe.Recipe
import com.fwrdgrp.recipesaving.data.models.recipe.RecipeIngredient
import com.fwrdgrp.recipesaving.data.models.recipe.RecipeWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    //Recipe
    @Insert
    suspend fun insertRecipe(recipe: Recipe): Long

    @Update
    suspend fun updateRecipe(recipe: Recipe)

    @Delete
    suspend fun deleteRecipe(recipe: Recipe)

    @Query("SELECT * FROM Recipe WHERE id = :id")
    fun getRecipeById(id: Int): Flow<Recipe?>

    @Query("SELECT * FROM Recipe")
    fun getAllRecipes(): Flow<List<Recipe>>

    //Ingredient
    @Insert
    suspend fun insertIngredient(ingredient: Ingredient): Long

    @Update
    suspend fun updateIngredient(ingredient: Ingredient)

    @Delete
    suspend fun deleteIngredient(ingredient: Ingredient)

    @Query("SELECT * FROM Ingredient WHERE id = :id")
    fun getIngredientById(id: Int): Flow<Ingredient?>

    @Query("SELECT * FROM Ingredient")
    fun getAllIngredients(): Flow<List<Ingredient>>

    //RecipeIngredient
    @Insert
    suspend fun insertRecipeIngredient(recipeIngredient: RecipeIngredient)

    @Delete
    suspend fun deleteRecipeIngredient(recipeIngredient: RecipeIngredient)

    @Query("SELECT * FROM RecipeIngredient WHERE recipeId = :recipeId")
    fun getRecipeIngredients(recipeId: Int): Flow<List<RecipeIngredient>>

    //Instructions
    @Insert
    suspend fun insertInstruction(instruction: Instruction): Long

    @Update
    suspend fun updateInstruction(instruction: Instruction)

    @Delete
    suspend fun deleteInstruction(instruction: Instruction)

    @Query("SELECT * FROM Instruction WHERE recipeId = :recipeId ORDER BY stepNumber ASC")
    fun getInstructionsForRecipe(recipeId: Int): Flow<List<Instruction>>

    //RecipeWithDetails
    @Transaction
    @Query("SELECT * FROM Recipe WHERE id = :id")
    fun getRecipeWithDetails(id: Int): Flow<RecipeWithDetails>

    //IngredientWithAmount
    @Query("""
    SELECT Ingredient.*, RecipeIngredient.amount, RecipeIngredient.unit
    FROM Ingredient
    INNER JOIN RecipeIngredient 
        ON Ingredient.id = RecipeIngredient.ingredientId
    WHERE RecipeIngredient.recipeId = :recipeId
    """)
    fun getIngredientsForRecipe(recipeId: Int): Flow<List<IngredientWithAmount>>
}