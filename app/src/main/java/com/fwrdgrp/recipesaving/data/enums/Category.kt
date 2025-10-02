package com.fwrdgrp.recipesaving.data.enums

enum class Category(val label: String) {
    INITIAL("Select a Category..."),
    BREAKFAST("Breakfast"),
    LUNCH("Lunch"),
    DINNER("Dinner"),
    DESSERT("Dessert"),
    VEGAN("Vegan"),
    PESCATARIAN("Pescatarian"),
    VEGETARIAN("Vegetarian"),
    SNACK("Snack"),
    DRINK("Drink");

    override fun toString(): String = label

}