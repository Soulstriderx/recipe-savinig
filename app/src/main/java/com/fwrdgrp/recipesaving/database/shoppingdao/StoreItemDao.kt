package com.fwrdgrp.recipesaving.database.shoppingdao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.fwrdgrp.recipesaving.data.models.shopping.StoreItem
import com.fwrdgrp.recipesaving.data.models.shopping.StoreItemWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface StoreItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStoreItem(storeItem: StoreItem): Long

    @Query("SELECT * FROM StoreItem")
    fun getAllStoreItems(): Flow<List<StoreItem>>

    @Transaction
    @Query("SELECT * FROM StoreItem WHERE storeId = :storeItemId")
    fun getStoreItemWithDetailsById(storeItemId: Int): Flow<List<StoreItemWithDetails>>

    @Query("DELETE FROM StoreItem WHERE id = :id")
    suspend fun deleteStoreItemById(id: Int)
}