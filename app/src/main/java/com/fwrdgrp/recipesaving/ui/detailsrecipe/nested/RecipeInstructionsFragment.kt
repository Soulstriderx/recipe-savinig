package com.fwrdgrp.recipesaving.ui.detailsrecipe.nested

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.fwrdgrp.recipesaving.R
import com.fwrdgrp.recipesaving.databinding.FragmentRecipeInstructionsBinding
import com.fwrdgrp.recipesaving.ui.adapters.DisplayInstructionAdapter

class RecipeInstructionsFragment : Fragment() {

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
        setupAdapter()
    }

    fun setupAdapter() {
        adapter = DisplayInstructionAdapter(emptyList())
        binding.run {
            rvIngredient.adapter = adapter
            rvIngredient.layoutManager = LinearLayoutManager(requireContext())
        }
    }

}