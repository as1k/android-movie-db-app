package com.example.movie_db

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

@Suppress("DEPRECATION")
class AdapterForPager(fm: FragmentManager?, private val fragmentList: List<Fragment>) :
    FragmentPagerAdapter(fm!!) {
    override fun getItem(position: Int): Fragment {
    return fragmentList[position]
    }
    override fun getCount(): Int {
        return fragmentList.size
    }
}