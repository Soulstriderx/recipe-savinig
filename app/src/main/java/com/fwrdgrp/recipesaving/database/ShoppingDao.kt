package com.fwrdgrp.recipesaving.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.fwrdgrp.recipesaving.data.models.recipe.Ingredient
import com.fwrdgrp.recipesaving.data.models.shopping.ShoppingList
import com.fwrdgrp.recipesaving.data.models.shopping.ShoppingListItem
import com.fwrdgrp.recipesaving.data.models.shopping.ShoppingListWithItems
import com.fwrdgrp.recipesaving.data.models.shopping.ShoppingListWithStoreAndItems
import com.fwrdgrp.recipesaving.data.models.shopping.Store
import com.fwrdgrp.recipesaving.data.models.shopping.StoreItem
import com.fwrdgrp.recipesaving.data.models.shopping.StoreItemWithDetails
import com.fwrdgrp.recipesaving.data.models.shopping.StoreWithItemsDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingDao {
    // ---- INGREDIENTS ----
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertIngredient(ingredient: Ingredient): Long

    @Query("SELECT * FROM Ingredient WHERE LOWER(name) = LOWER(:name) LIMIT 1")
    suspend fun getIngredientByName(name: String): Ingredient?

    @Query("SELECT * FROM Ingredient")
    fun getAllIngredients(): Flow<List<Ingredient>>

    // ---- STORES ----
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertStore(store: Store): Long

    @Query("DELETE FROM Store WHERE id = :id")
    suspend fun deleteStoreById(id: Int)

    @Transaction
    @Query("SELECT * FROM Store WHERE id = :storeId")
    suspend fun getStoreWithItemsDetails(storeId: Int): StoreWithItemsDetails

    @Query("SELECT * FROM Store")
    fun getAllStores(): Flow<List<Store>>

    // ---- STORE ITEMS ----
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertStoreItem(storeItem: StoreItem): Long

    @Query("SELECT * FROM StoreItem WHERE storeId = :storeId")
    fun getAllStoreItemsByStoreId(storeId: Int): Flow<List<StoreItem>>

    @Transaction
    @Query("SELECT * FROM StoreItem WHERE id = :storeItemId")
    fun getStoreItemWithDetailsById(storeItemId: Int): Flow<List<StoreItemWithDetails>>

    @Query("DELETE FROM StoreItem WHERE id = :id")
    suspend fun deleteStoreItemById(id: Int)

    @Query("SELECT * FROM StoreItem WHERE ingredientId = :ingredientId")
    fun getStoreItemsForIngredient(ingredientId: Int): Flow<List<StoreItem>>

    @Query("SELECT * FROM StoreItem WHERE ingredientId = :ingredientId ORDER BY price ASC LIMIT 1")
    suspend fun getCheapestStoreItem(ingredientId: Int): StoreItem?

    // ---- SHOPPING LISTS ----
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun upsertShoppingList(list: ShoppingList): Long

    @Update
    suspend fun updateShoppingList(list: ShoppingList)

    @Query("DELETE FROM ShoppingList WHERE id = :listId")
    suspend fun deleteShoppingListById(listId: Int)

    @Query("SELECT * FROM ShoppingList")
    fun getAllShoppingLists(): Flow<List<ShoppingList>>

    @Transaction
    @Query("SELECT * FROM ShoppingList WHERE id = :listId")
    fun getShoppingListWithItems(listId: Int): Flow<ShoppingListWithItems>

//    @Query("SELECT * FROM ShoppingList")
//    suspend fun deleteShoppingList(list: ShoppingList): Flow<ShoppingList>

    @Query("SELECT * FROM ShoppingList WHERE id = :listId")
    suspend fun getShoppingListWithStoreAndItems(listId: Int): ShoppingListWithStoreAndItems

    @Transaction
    @Query("SELECT * FROM ShoppingList ORDER BY dateCreated DESC")
    fun getAllShoppingListsWithStoreAndItems(): Flow<List<ShoppingListWithStoreAndItems>>

    // ---- SHOPPING LIST ITEMS ----
    @Upsert
    suspend fun upsertShoppingListItem(item: ShoppingListItem)

    @Update
    suspend fun updateShoppingListItem(item: ShoppingListItem)

    @Delete
    suspend fun deleteShoppingListItem(item: ShoppingListItem)

    @Query("SELECT * FROM ShoppingListItem")
    fun getShoppingListItem(): Flow<List<ShoppingListItem>>

    @Query("SELECT * FROM ShoppingListItem WHERE listId = :listId")
    fun getItemsForList(listId: Int): Flow<List<ShoppingListItem>>

    @Query("UPDATE ShoppingListItem SET bought = :bought WHERE listId = :listId AND ingredientId = :ingredientId")
    suspend fun updateBoughtStatus(listId: Int, ingredientId: Int, bought: Boolean)



}