package com.example.movie_db

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.PagerAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView


class FragmentsActivity : AppCompatActivity() {
    private lateinit var pager: MyViewPager
    private lateinit var pagerAdapter: PagerAdapter
    private var fragment1: Fragment = FragmentOne()
    private var fragment2: Fragment = FragmentTwo()
    private var fragment3: Fragment = FragmentThree()
    private var fragmentList: MutableList<Fragment> = ArrayList()
    private lateinit var bottomNavView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)

        bottomNavView = findViewById(R.id.bottom_navigation)
        fragmentList.add(fragment1)
        fragmentList.add(fragment2)
        fragmentList.add(fragment3)
        pager = findViewById(R.id.pager)
        pager.setSwipe(false)
        pagerAdapter = AdapterForPager(supportFragmentManager, fragmentList)
        pager.adapter = pagerAdapter

        bottomNavView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    pager.setCurrentItem(0, false)
                    bottomNavView.menu.findItem(R.id.save)
                        .setIcon(R.drawable.ic_save)
                    bottomNavView.menu.findItem(R.id.profile)
                        .setIcon(R.drawable.ic_profile)
                    item.setIcon(R.drawable.ic_home)
                }
                R.id.save -> {
                    pager.setCurrentItem(1, false)
                    item.setIcon(R.drawable.ic_favorite)
                    bottomNavView.menu.findItem(R.id.profile)
                        .setIcon(R.drawable.ic_profile)
                    bottomNavView.menu.findItem(R.id.home)
                        .setIcon(R.drawable.ic_home_new)
                }
                R.id.profile -> {
                    pager.setCurrentItem(2, false)
                    bottomNavView.menu.findItem(R.id.save)
                        .setIcon(R.drawable.ic_save)
                    bottomNavView.menu.findItem(R.id.home)
                        .setIcon(R.drawable.ic_home_new)
                    item.setIcon(R.drawable.ic_person)
                }
            }
            false
        }
    }
}
