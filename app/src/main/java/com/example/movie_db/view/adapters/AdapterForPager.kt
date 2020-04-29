package com.example.movie_db.view.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

@Suppress("DEPRECATION")
class AdapterForPager(fragmentManager: FragmentManager?, private val fragments: List<Fragment>) :
    FragmentPagerAdapter(fragmentManager!!) {

    override fun getItem(position: Int): Fragment {
    return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }
}
