package com.fwrdgrp.recipesaving.ui.detailsrecipe

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.fwrdgrp.recipesaving.R
import com.fwrdgrp.recipesaving.data.models.recipe.Recipe
import com.fwrdgrp.recipesaving.databinding.FragmentRecipeDetailsBinding
import com.fwrdgrp.recipesaving.ui.adapters.TabsAdapter
import com.fwrdgrp.recipesaving.ui.detailsrecipe.nested.RecipeInstructionsFragment
import com.fwrdgrp.recipesaving.ui.detailsrecipe.nested.RecipeOverviewFragment
import com.fwrdgrp.recipesaving.ui.home.nested.RecipeFragment
import com.fwrdgrp.recipesaving.ui.home.nested.ShopListFragment
import com.google.android.material.tabs.TabLayoutMediator

class RecipeDetailsFragment : Fragment() {
    private lateinit var binding: FragmentRecipeDetailsBinding
    private val args: RecipeDetailsFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecipeDetailsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //ViewPager Stuff
        val adapter = TabsAdapter(
            fragments = listOf(RecipeOverviewFragment(), RecipeInstructionsFragment()),
            fragment = this
        )
        binding.vpTabs.adapter = adapter
        TabLayoutMediator(binding.tlTabs, binding.vpTabs) { tab, position ->
            when(position) {
                0 -> tab.text = getString(R.string.overview)
                else -> tab.text = getString(R.string.instructions)
            }
        }.attach()
    }

}