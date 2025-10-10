package com.fwrdgrp.recipesaving.data.repo

import com.fwrdgrp.recipesaving.data.models.shopping.ShoppingList
import com.fwrdgrp.recipesaving.data.models.shopping.ShoppingListItem
import com.fwrdgrp.recipesaving.data.models.shopping.Store
import com.fwrdgrp.recipesaving.data.models.shopping.StoreItem
import com.fwrdgrp.recipesaving.data.utils.ShoppingRepoUtils
import com.fwrdgrp.recipesaving.database.ShoppingDao

class ShoppingRepo(
    private val dao: ShoppingDao,
) {
    private val utils = ShoppingRepoUtils(dao)

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
    fun getAllStoreItemsWithDetailsByStoreId(storeId: Int) = dao.getStoreItemWithDetailsById(storeId)

    // ---- Shopping Lists ----
    fun getShoppingLists() = dao.getAllShoppingLists()
    suspend fun deleteShoppingListById(listId: Int) = dao.deleteShoppingListById(listId)

    //    fun getShoppingListById(id: Int) = dao.getShoppingListById(id)
    suspend fun upsertShoppingList(list: ShoppingList) = dao.upsertShoppingList(list).toInt()
    suspend fun updateShoppingList(list: ShoppingList) = dao.updateShoppingList(list)
//    suspend fun deleteShoppingList(list: ShoppingList) = dao.deleteShoppingList(list)

    // ---- Shopping List Items ----
//    fun getShoppingListItems(listId: Int) = dao.getShoppingListItems(listId)
    suspend fun upsertShoppingListItem(
        listId: Int,
        ingredient: String,
        price: Double,
        unit: String
    ) {
        val ingredientId = utils.addSingleIngredients(ingredient)
        val shoppingListItem = utils.buildShoppingListItem(listId, ingredientId, price, unit)
        dao.upsertShoppingListItem(shoppingListItem)
    }

    suspend fun insertGeneratedShopListItem(
        listId: Int,
        ingredientId: Int,
        amount: Double,
        unit: String
    ) {
        val shoppingListItem = utils.buildShoppingListItem(listId, ingredientId, amount, unit)
        dao.upsertShoppingListItem(shoppingListItem)
    }

    suspend fun deleteShoppingListItem(item: ShoppingListItem) = dao.deleteShoppingListItem(item)

    suspend fun toggleBought(item: ShoppingListItem) {
        dao.updateShoppingListItem(item)
    }

    //    suspend fun deleteShoppingListItem(item: ShoppingListItem) = dao.deleteShoppingListItem(item)
    suspend fun toggleBought(listId: Int, ingredientId: Int, bought: Boolean) =
        dao.updateBoughtStatus(listId, ingredientId, bought)

    // ---- Joins / UI Helpers ----
    fun getShoppingListWithItems(listId: Int) = dao.getShoppingListWithItems(listId)
//    fun getShoppingListWithStoreItems(listId: Int, storeId: Int) =
//        dao.getShoppingListWithStoreItems(listId, storeId)

    suspend fun getShoppingListWithPrices(listId: Int) =
        dao.getShoppingListWithStoreAndItems(listId)

    fun getAllShoppingListWithStoreAndItems() = dao.getAllShoppingListsWithStoreAndItems()

    fun getAllShoppingList() = dao.getShoppingListItem()

    fun getAllStoreItems() = dao.getAllStoreItems()
}