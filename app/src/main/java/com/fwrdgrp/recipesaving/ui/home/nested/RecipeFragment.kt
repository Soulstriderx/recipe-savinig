package com.fwrdgrp.recipesaving.ui.home.nested

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fwrdgrp.recipesaving.databinding.FragmentRecipeBinding
import com.fwrdgrp.recipesaving.ui.adapters.RecipeAdapter

class RecipeFragment : Fragment() {
    private lateinit var binding: FragmentRecipeBinding
    private lateinit var adapter: RecipeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecipeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()

    }

    fun setupAdapter() {
        adapter = RecipeAdapter(
            emptyList()
            //Nav to Edit
        )
    }
}