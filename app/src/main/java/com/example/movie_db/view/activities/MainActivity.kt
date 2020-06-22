package com.example.movie_db.view.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.movie_db.model.utils.MoviesViewPager
import com.example.movie_db.R
import com.example.movie_db.view.adapters.MoviesPagerAdapter
import com.example.movie_db.view.fragments.PopularMoviesFragment
import com.example.movie_db.view.fragments.ProfileFragment
import com.example.movie_db.view.fragments.SavedMoviesFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var pager: MoviesViewPager
    private lateinit var pagerAdapter: MoviesPagerAdapter
    private var movieListFragment: Fragment = PopularMoviesFragment()
    private var likedMovieListFragment: Fragment = SavedMoviesFragment()
    private var profileFragment: Fragment = ProfileFragment()
    private var fragmentList: MutableList<Fragment> = ArrayList()
    private lateinit var bottomNavView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_menu_layout)
        bindViews()
        setFragment()
    }

    private fun bindViews() {
        bottomNavView = findViewById(R.id.bottom_navigation)
        fragmentList.add(movieListFragment)
        fragmentList.add(likedMovieListFragment)
        fragmentList.add(profileFragment)
        pager = findViewById(R.id.pager)

        pager.offscreenPageLimit = 3
        pager.setSwiping(false)
        pagerAdapter = MoviesPagerAdapter(supportFragmentManager, fragmentList)
        pager.adapter = pagerAdapter
    }

    private fun setFragment() {
        bottomNavView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    pager.setCurrentItem(0, false)
                    bottomNavView.menu.findItem(R.id.save)
                        .setIcon(R.drawable.ic_save)
                    bottomNavView.menu.findItem(R.id.profile)
                        .setIcon(R.drawable.ic_profile_not_clicked)
                    item.setIcon(R.drawable.ic_home_clicked)
                }
                R.id.save -> {
                    pager.setCurrentItem(1, false)
                    item.setIcon(R.drawable.ic_favorite)
                    bottomNavView.menu.findItem(R.id.profile)
                        .setIcon(R.drawable.ic_profile_not_clicked)
                    bottomNavView.menu.findItem(R.id.home)
                        .setIcon(R.drawable.ic_home_not_clicked)
                }
                R.id.profile -> {
                    pager.setCurrentItem(2, false)
                    bottomNavView.menu.findItem(R.id.save)
                        .setIcon(R.drawable.ic_save)
                    bottomNavView.menu.findItem(R.id.home)
                        .setIcon(R.drawable.ic_home_not_clicked)
                    item.setIcon(R.drawable.ic_profile_clicked)
                }
            }
            false
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        bottomNavView.visibility = View.VISIBLE
    }
}
