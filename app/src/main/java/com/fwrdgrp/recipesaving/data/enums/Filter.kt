package com.fwrdgrp.recipesaving.data.enums

enum class Filter(val label: String) {
    INITIAL("None"),
    ALPHABETICALLY("A-Z"),
    DIFFICULTY("Difficulty"), //How many instructions there are
    INGREDIENTS("Ingredients"), //How many ingredients there are
    TIME("Time to make")
}