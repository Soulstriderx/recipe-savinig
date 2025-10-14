package com.fwrdgrp.recipesaving.database.shoppingdao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Update
import androidx.room.Upsert
import com.fwrdgrp.recipesaving.data.models.shopping.ShoppingListItem

@Dao
interface ShoppingListItemDao {
    @Upsert
    suspend fun upsertShoppingListItem(item: ShoppingListItem)

    @Update
    suspend fun updateShoppingListItem(item: ShoppingListItem)

    @Delete
    suspend fun deleteShoppingListItem(item: ShoppingListItem)
}