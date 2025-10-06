package com.fwrdgrp.recipesaving.database

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.fwrdgrp.recipesaving.data.models.recipe.Ingredient
import com.fwrdgrp.recipesaving.data.models.shopping.ShoppingList
import com.fwrdgrp.recipesaving.data.models.shopping.ShoppingListItem
import com.fwrdgrp.recipesaving.data.models.shopping.ShoppingListWithItems
import com.fwrdgrp.recipesaving.data.models.shopping.Store
import com.fwrdgrp.recipesaving.data.models.shopping.StoreItem
import kotlinx.coroutines.flow.Flow

interface ShoppingDao {
    // ---- INGREDIENTS ----
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertIngredient(ingredient: Ingredient): Long

    @Query("SELECT * FROM Ingredient")
    fun getAllIngredients(): Flow<List<Ingredient>>

    // ---- STORES ----
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertStore(store: Store): Long

    @Query("SELECT * FROM Store")
    fun getAllStores(): Flow<List<Store>>

    // ---- STORE ITEMS ----
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertStoreItem(storeItem: StoreItem): Long

    @Query("SELECT * FROM StoreItem WHERE ingredientId = :ingredientId")
    fun getStoreItemsForIngredient(ingredientId: Int): Flow<List<StoreItem>>

    @Query("SELECT * FROM StoreItem WHERE ingredientId = :ingredientId ORDER BY price ASC LIMIT 1")
    suspend fun getCheapestStoreItem(ingredientId: Int): StoreItem?

    // ---- SHOPPING LISTS ----
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertShoppingList(list: ShoppingList): Long

    @Query("SELECT * FROM ShoppingList")
    fun getAllShoppingLists(): Flow<List<ShoppingList>>

    @Transaction
    @Query("SELECT * FROM ShoppingList WHERE id = :listId")
    fun getShoppingListWithItems(listId: Int): Flow<ShoppingListWithItems>

    @Delete
    suspend fun deleteShoppingList(list: ShoppingList)

    // ---- SHOPPING LIST ITEMS ----
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertShoppingListItem(item: ShoppingListItem)

    @Query("SELECT * FROM ShoppingListItem WHERE listId = :listId")
    fun getItemsForList(listId: Int): Flow<List<ShoppingListItem>>

    @Query("UPDATE ShoppingListItem SET bought = :bought WHERE listId = :listId AND ingredientId = :ingredientId")
    suspend fun updateBoughtStatus(listId: Int, ingredientId: Int, bought: Boolean)

}