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
import com.fwrdgrp.recipesaving.database.recipedao.IngredientDao
import com.fwrdgrp.recipesaving.database.recipedao.InstructionDao
import com.fwrdgrp.recipesaving.database.recipedao.RecipeDao
import com.fwrdgrp.recipesaving.database.shoppingdao.ShoppingListDao
import com.fwrdgrp.recipesaving.database.shoppingdao.ShoppingListItemDao
import com.fwrdgrp.recipesaving.database.shoppingdao.StoreDao
import com.fwrdgrp.recipesaving.database.shoppingdao.StoreItemDao


@Database(entities = [Ingredient::class, Recipe::class, RecipeIngredient::class, Instruction::class, ShoppingList::class, ShoppingListItem::class, Store::class, StoreItem:: class], version = 1)
@TypeConverters(Converters::class)
abstract class MyDatabase: RoomDatabase() {
    abstract fun getRecipeDao(): RecipeDao
    abstract fun getIngredientDao(): IngredientDao
    abstract fun getInstructionDao(): InstructionDao
    abstract fun getShoppingListDao(): ShoppingListDao
    abstract fun getShoppingListItemDao(): ShoppingListItemDao
    abstract fun getStoreDao(): StoreDao
    abstract fun getStoreItemDao(): StoreItemDao


    companion object {
        const val NAME = "my_database"
    }
}