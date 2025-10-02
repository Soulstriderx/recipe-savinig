package com.fwrdgrp.recipesaving.ui.detailsrecipe.nested

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.fwrdgrp.recipesaving.R
import com.fwrdgrp.recipesaving.data.models.recipe.Ingredient
import com.fwrdgrp.recipesaving.data.models.recipe.Instruction
import com.fwrdgrp.recipesaving.databinding.FragmentRecipeOverviewBinding
import com.fwrdgrp.recipesaving.ui.adapters.AddInstructionAdapter
import com.fwrdgrp.recipesaving.ui.adapters.DisplayIngredientAdapter
import com.fwrdgrp.recipesaving.ui.detailsrecipe.RecipeDetailsViewModel

class RecipeOverviewFragment : Fragment() {
    private val viewModel: RecipeDetailsViewModel by viewModels(
        ownerProducer = { requireParentFragment() }
    )
    private lateinit var binding: FragmentRecipeOverviewBinding
    private lateinit var adapter: DisplayIngredientAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecipeOverviewBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()

        binding.run {
            viewModel.run {
//                ivImage
                tvTitle.text =  recipe.title
                for(category in recipe.category) {
                    val tvCategory = TextView(requireContext()).apply {
                        text = category.name
                        setBackgroundResource(R.drawable.box_bg)
                    }
//                    tvCategory.setOnClickListener { (category) } navigate to filter thing
                    llCategory.addView(tvCategory)
                }
                tvDesc.text =  recipe.description
                tvTime.text = recipe.estTime.toString()
                tvServing.text =  recipe.totalServing.toString()

                rvIngredient.adapter
            }
        }
    }
    fun setupAdapter() {
        adapter = DisplayIngredientAdapter(mutableListOf())
        binding.run {
            rvIngredient.adapter = adapter
            rvIngredient.layoutManager = LinearLayoutManager(requireContext())
        }
    }
}