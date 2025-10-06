package com.fwrdgrp.recipesaving.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fwrdgrp.recipesaving.data.models.recipe.Ingredient
import com.fwrdgrp.recipesaving.data.models.recipe.Instruction
import com.fwrdgrp.recipesaving.data.models.recipe.Recipe
import com.fwrdgrp.recipesaving.data.models.recipe.RecipeIngredient
import com.fwrdgrp.recipesaving.data.models.shopping.ShoppingList
import com.fwrdgrp.recipesaving.data.models.shopping.ShoppingListItem
import com.fwrdgrp.recipesaving.data.models.shopping.Store
import com.fwrdgrp.recipesaving.data.models.shopping.StoreItem
import com.fwrdgrp.recipesaving.data.utils.Converters


@Database(entities = [Ingredient::class, Recipe::class, RecipeIngredient::class, Instruction::class, ShoppingList::class, ShoppingListItem::class, Store::class, StoreItem:: class], version = 1)
@TypeConverters(Converters::class)
abstract class MyDatabase: RoomDatabase() {
    abstract fun getRecipeDao(): RecipeDao
    abstract fun getShoppingDao(): ShoppingDao

    companion object {
        const val NAME = "my_database"
    }
}