package com.fwrdgrp.recipesaving

import android.app.Application
import androidx.room.Room
import com.fwrdgrp.recipesaving.data.repo.RecipeRepo
import com.fwrdgrp.recipesaving.data.repo.ShoppingRepo
import com.fwrdgrp.recipesaving.database.MyDatabase

class MyApp : Application() {
    lateinit var RecipeRepository: RecipeRepo
    lateinit var ShoppingRepository: ShoppingRepo
    override fun onCreate() {
        super.onCreate()

        val db = Room.databaseBuilder(
            this,
            MyDatabase::class.java,
            MyDatabase.NAME
        ).build()
        RecipeRepository = RecipeRepo(
            db.getRecipeDao(), db.getIngredientDao(), db.getInstructionDao()
        )
        ShoppingRepository =
            ShoppingRepo(
                db.getIngredientDao(), db.getStoreDao(), db.getStoreItemDao(),
                db.getShoppingListDao(), db.getShoppingListItemDao()
            )
    }
}