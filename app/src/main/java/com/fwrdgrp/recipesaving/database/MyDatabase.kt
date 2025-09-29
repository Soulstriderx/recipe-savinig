package com.fwrdgrp.recipesaving.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fwrdgrp.recipesaving.data.models.recipe.Ingredient
import com.fwrdgrp.recipesaving.data.models.recipe.Instruction
import com.fwrdgrp.recipesaving.data.models.recipe.Recipe
import com.fwrdgrp.recipesaving.data.models.recipe.RecipeIngredient


@Database(entities = [Ingredient::class, Recipe::class, RecipeIngredient::class, Instruction::class], version = 1)
abstract class MyDatabase: RoomDatabase() {
    abstract fun getRecipeDao(): RecipeDao

    companion object {
        const val NAME = "my_database"
    }
}