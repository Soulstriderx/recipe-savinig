package com.fwrdgrp.recipesaving.database.shoppingdao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.fwrdgrp.recipesaving.data.models.shopping.Store
import com.fwrdgrp.recipesaving.data.models.shopping.StoreWithItemsDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface StoreDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStore(store: Store): Long


    @Update
    suspend fun updateStore(store: Store)

    @Query("DELETE FROM Store WHERE id = :id")
    suspend fun deleteStoreById(id: Int)

    @Transaction
    @Query("SELECT * FROM Store WHERE id = :storeId")
    suspend fun getStoreWithItemsDetails(storeId: Int): StoreWithItemsDetails

    @Query("SELECT * FROM Store")
    fun getAllStores(): Flow<List<Store>>
}