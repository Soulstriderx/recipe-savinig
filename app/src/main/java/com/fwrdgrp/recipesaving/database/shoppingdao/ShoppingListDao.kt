package com.fwrdgrp.recipesaving.database.shoppingdao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.fwrdgrp.recipesaving.data.models.shopping.ShoppingList
import com.fwrdgrp.recipesaving.data.models.shopping.ShoppingListWithItems
import com.fwrdgrp.recipesaving.data.models.shopping.ShoppingListWithStoreAndItems
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingListDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertShoppingList(list: ShoppingList): Long

    @Update
    suspend fun updateShoppingList(list: ShoppingList)

    @Query("DELETE FROM ShoppingList WHERE id = :listId")
    suspend fun deleteShoppingListById(listId: Int)

    @Transaction
    @Query("SELECT * FROM ShoppingList WHERE id = :listId")
    fun getShoppingListWithItems(listId: Int): Flow<ShoppingListWithItems>

    @Query("SELECT * FROM ShoppingList WHERE id = :listId")
    suspend fun getShoppingListWithStoreAndItems(listId: Int): ShoppingListWithStoreAndItems

    @Transaction
    @Query("SELECT * FROM ShoppingList")
    fun getAllShoppingListsWithStoreAndItems(): Flow<List<ShoppingListWithStoreAndItems>>
}