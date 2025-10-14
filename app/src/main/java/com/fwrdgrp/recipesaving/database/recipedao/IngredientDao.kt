package com.fwrdgrp.recipesaving.database.recipedao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.fwrdgrp.recipesaving.data.models.recipe.Ingredient
import com.fwrdgrp.recipesaving.data.models.recipe.IngredientWithAmount
import com.fwrdgrp.recipesaving.data.models.recipe.RecipeIngredient
import kotlinx.coroutines.flow.Flow

@Dao
interface IngredientDao {
    //Ingredient
    @Upsert
    suspend fun upsertIngredient(ingredient: Ingredient): Long

    @Query("SELECT * FROM Ingredient WHERE LOWER(name) = LOWER(:name) LIMIT 1")
    suspend fun getIngredientByName(name: String): Ingredient?

    @Query("SELECT * FROM Ingredient")
    fun getAllIngredients(): Flow<List<Ingredient>>

    //RecipeIngredient
    @Insert
    suspend fun insertRecipeIngredient(recipeIngredient: RecipeIngredient)

    @Query("DELETE FROM RecipeIngredient WHERE recipeId = :recipeId")
    suspend fun deleteRecipeIngredientsByRecipeId(recipeId: Int)

    //IngredientsForRecipe
    @Query("""
    SELECT Ingredient.*, RecipeIngredient.amount, RecipeIngredient.unit
    FROM Ingredient
    INNER JOIN RecipeIngredient 
        ON Ingredient.id = RecipeIngredient.ingredientId
    WHERE RecipeIngredient.recipeId = :recipeId
    """)
    fun getIngredientsForRecipe(recipeId: Int): Flow<List<IngredientWithAmount>>
}