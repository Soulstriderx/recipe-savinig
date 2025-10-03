package com.fwrdgrp.recipesaving.ui.detailsrecipe.nested

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.fwrdgrp.recipesaving.data.models.recipe.Instruction
import com.fwrdgrp.recipesaving.databinding.FragmentRecipeInstructionsBinding
import com.fwrdgrp.recipesaving.ui.adapters.DisplayInstructionAdapter
import com.fwrdgrp.recipesaving.ui.detailsrecipe.RecipeDetailsViewModel
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlin.getValue

class RecipeInstructionsFragment : Fragment() {
    private val viewModel: RecipeDetailsViewModel by viewModels(
        ownerProducer = { requireParentFragment() },
        factoryProducer = { RecipeDetailsViewModel.Factory }
    )

    private lateinit var binding: FragmentRecipeInstructionsBinding
    private lateinit var adapter: DisplayInstructionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecipeInstructionsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewModel.recipeDetails.filterNotNull().collect {
                setupAdapter(it.instructions)
            }
        }
    }

    fun setupAdapter(instructions: List<Instruction>) {
        adapter = DisplayInstructionAdapter(instructions)
        binding.run {
            rvIngredient.adapter = adapter
            rvIngredient.layoutManager = LinearLayoutManager(requireContext())
        }
    }

}