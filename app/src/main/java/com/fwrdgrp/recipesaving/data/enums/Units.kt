package com.fwrdgrp.recipesaving.data.enums

enum class Units(val label: String) {
    PCS("pcs"), G("g"), ML("ml");

    override fun toString(): String = label
}
