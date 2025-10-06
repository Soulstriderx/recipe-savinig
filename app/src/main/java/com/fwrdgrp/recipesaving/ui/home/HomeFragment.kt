package com.fwrdgrp.recipesaving.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.fwrdgrp.recipesaving.R
import com.fwrdgrp.recipesaving.databinding.FragmentHomeBinding
import com.fwrdgrp.recipesaving.ui.adapters.TabsAdapter
import com.fwrdgrp.recipesaving.ui.home.nested.RecipeFragment
import com.fwrdgrp.recipesaving.ui.home.nested.ShopListFragment
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mbAdd.setOnClickListener {
            val action = if (binding.vpTabs.currentItem == 0) HomeFragmentDirections.actionHomeToAddRecipe()
            else HomeFragmentDirections.actionHomeToAddShopList()
            findNavController().navigate(action)
        }
        val adapter = TabsAdapter(fragments = listOf(RecipeFragment(), ShopListFragment()), fragment = this)
        binding.vpTabs.adapter = adapter
        TabLayoutMediator(binding.tlTabs, binding.vpTabs) { tab, position ->
            when(position) {
                0 -> tab.text = getString(R.string.recipes)
                else -> tab.text = getString(R.string.shop_list)
            }
        }.attach()
        binding.vpTabs.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) { addButtonText(position) }
        })
    }

    fun addButtonText(pos: Int) {
        binding.mbAdd.text = if(pos == 0) getString(R.string.add_recipes)
        else getString(R.string.add_shop_list)
    }
}