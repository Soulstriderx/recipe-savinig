package com.fwrdgrp.recipesaving.data.repo

import com.fwrdgrp.recipesaving.data.models.shopping.ShoppingList
import com.fwrdgrp.recipesaving.data.models.shopping.ShoppingListItem
import com.fwrdgrp.recipesaving.data.models.shopping.Store
import com.fwrdgrp.recipesaving.data.models.shopping.StoreItem
import com.fwrdgrp.recipesaving.database.shoppingdao.ShoppingListDao
import com.fwrdgrp.recipesaving.database.shoppingdao.ShoppingListItemDao
import com.fwrdgrp.recipesaving.database.shoppingdao.StoreDao
import com.fwrdgrp.recipesaving.database.shoppingdao.StoreItemDao

class ShoppingRepo(
    private val storeDao: StoreDao,
    private val storeItemDao: StoreItemDao,
    private val shoppingListDao: ShoppingListDao,
    private val shoppingListItemDao: ShoppingListItemDao
) {

    //Stores
    fun getStores() = storeDao.getAllStores()

    suspend fun updateStore(store: Store) = storeDao.updateStore(store)

    suspend fun insertStore(store: Store) = storeDao.insertStore(store)

    suspend fun getStoreWithItemDetails(storeId: Int) = storeDao.getStoreWithItemsDetails(storeId)

    suspend fun deleteStoreById(id: Int) = storeDao.deleteStoreById(id)

    //Store Items
    suspend fun insertStoreItem(item: StoreItem) = storeItemDao.insertStoreItem(item)

    suspend fun deleteStoreItem(id: Int) = storeItemDao.deleteStoreItemById(id)

    fun getAllStoreItemsWithDetailsByStoreId(storeId: Int) =
        storeItemDao.getStoreItemWithDetailsById(storeId)

    fun getAllStoreItems() = storeItemDao.getAllStoreItems()

    //Shopping List
    suspend fun deleteShoppingListById(listId: Int) = shoppingListDao.deleteShoppingListById(listId)

    suspend fun insertShoppingList(list: ShoppingList) =
        shoppingListDao.insertShoppingList(list).toInt()

    suspend fun updateShoppingList(list: ShoppingList) = shoppingListDao.updateShoppingList(list)

    suspend fun getShoppingListWithPrices(listId: Int) =
        shoppingListDao.getShoppingListWithStoreAndItems(listId)

    fun getAllShoppingListWithStoreAndItems() =
        shoppingListDao.getAllShoppingListsWithStoreAndItems()

    //Shopping List Item
    suspend fun upsertShoppingListItem(shoppingListItem: ShoppingListItem) =
        shoppingListItemDao.upsertShoppingListItem(shoppingListItem)


    suspend fun deleteShoppingListItem(item: ShoppingListItem) =
        shoppingListItemDao.deleteShoppingListItem(item)

    suspend fun toggleBought(item: ShoppingListItem) {
        shoppingListItemDao.updateShoppingListItem(item)
    }


}