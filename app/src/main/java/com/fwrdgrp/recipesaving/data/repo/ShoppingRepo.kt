package com.fwrdgrp.recipesaving.data.repo

import com.fwrdgrp.recipesaving.data.models.shopping.ShoppingList
import com.fwrdgrp.recipesaving.data.models.shopping.ShoppingListItem
import com.fwrdgrp.recipesaving.data.models.shopping.Store
import com.fwrdgrp.recipesaving.data.models.shopping.StoreItem
import com.fwrdgrp.recipesaving.database.ShoppingDao

class ShoppingRepo(
    private val dao: ShoppingDao,
) {
    // ---- Stores ----
    fun getStores() = dao.getAllStores()
//    fun getStoreById(id: Int) = dao.getStoreById(id)
    suspend fun upsertStore(store: Store) = dao.upsertStore(store)
    suspend fun getStoreWithItemDetails(storeId: Int) = dao.getStoreWithItemsDetails(storeId)
    suspend fun deleteStoreById(id: Int) = dao.deleteStoreById(id)

    // ---- Store Items ----
//    fun getStoreItems(storeId: Int) = dao.getStoreItems(storeId)
    suspend fun upsertStoreItem(item: StoreItem) = dao.upsertStoreItem(item)
    suspend fun deleteStoreItem(id: Int) = dao.deleteStoreItemById(id)

    // ---- Shopping Lists ----
    fun getShoppingLists() = dao.getAllShoppingLists()
//    fun getShoppingListById(id: Int) = dao.getShoppingListById(id)
    suspend fun upsertShoppingList(list: ShoppingList) = dao.upsertShoppingList(list)
    suspend fun deleteShoppingList(list: ShoppingList) = dao.deleteShoppingList(list)

    // ---- Shopping List Items ----
//    fun getShoppingListItems(listId: Int) = dao.getShoppingListItems(listId)
    suspend fun upsertShoppingListItem(item: ShoppingListItem) = dao.upsertShoppingListItem(item)
//    suspend fun deleteShoppingListItem(item: ShoppingListItem) = dao.deleteShoppingListItem(item)
    suspend fun toggleBought(listId: Int, ingredientId: Int, bought: Boolean) =
        dao.updateBoughtStatus(listId, ingredientId, bought)

    // ---- Joins / UI Helpers ----
    fun getShoppingListWithItems(listId: Int) = dao.getShoppingListWithItems(listId)
//    fun getShoppingListWithStoreItems(listId: Int, storeId: Int) =
//        dao.getShoppingListWithStoreItems(listId, storeId)
}