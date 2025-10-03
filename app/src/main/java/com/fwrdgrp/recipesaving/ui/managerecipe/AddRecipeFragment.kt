package com.fwrdgrp.recipesaving.ui.managerecipe

import androidx.fragment.app.viewModels
import kotlin.getValue

class AddRecipeFragment : BaseManageRecipeFragment() {
    override val viewModel: AddRecipeViewModel by viewModels {
        AddRecipeViewModel.Factory
    }

}