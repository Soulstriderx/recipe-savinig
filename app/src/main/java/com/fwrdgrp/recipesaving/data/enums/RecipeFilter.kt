package com.fwrdgrp.recipesaving.data.enums

enum class RecipeFilter(val label: String) {
    DATE("Date"), //In the order it was made
    ALPHABETICALLY("A-Z"),
    TIME("Time taken");

    override fun toString(): String = label
}