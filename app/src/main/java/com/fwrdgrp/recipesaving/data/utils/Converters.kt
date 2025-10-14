package com.fwrdgrp.recipesaving.data.utils

import androidx.room.TypeConverter
import com.fwrdgrp.recipesaving.data.enums.Category

class Converters {
    @TypeConverter
    fun fromListToString(categories: List<Category>): String {
        return categories.joinToString(",") { it.name }
    }

    @TypeConverter
    fun fromStringToList(categories: String): List<Category> {
        return if (categories.isEmpty()) emptyList()
        else categories.split(",").map { Category.valueOf(it) }
    }
}