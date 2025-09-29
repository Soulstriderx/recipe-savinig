package com.fwrdgrp.recipesaving.ui.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class TabsAdapter(
    private val fragments: List<Fragment>,
    fragment: Fragment
): FragmentStateAdapter(fragment) {
    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
    override fun getItemCount() = fragments.size
}