package com.fwrdgrp.recipesaving

import android.app.Application
import androidx.room.Room
import com.fwrdgrp.recipesaving.data.repo.RecipeRepo
import com.fwrdgrp.recipesaving.database.MyDatabase

class MyApp : Application() {
    lateinit var repo: RecipeRepo
    override fun onCreate() {
        super.onCreate()

        val db = Room.databaseBuilder(
            this,
            MyDatabase::class.java,
            MyDatabase.NAME
        ).build()
        repo = RecipeRepo(db.getRecipeDao())
    }
}