package com.example.movie_db.view.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.movie_db.view.adapters.MovieListAdapter
import com.example.movie_db.R
import com.example.movie_db.model.data.movie.Movie
import androidx.lifecycle.Observer
import com.example.movie_db.model.utils.PaginationListener
import com.example.movie_db.view_model.MovieListViewModel
import androidx.fragment.app.activityViewModels
import com.example.movie_db.view_model.SharedViewModel
import kotlinx.android.synthetic.main.main_menu_layout.*
import org.koin.android.ext.android.inject

class PopularMoviesFragment : Fragment(), MovieListAdapter.RecyclerViewItemClick {

    private lateinit var adapter: MovieListAdapter
    private lateinit var recView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var toolbar: TextView
    private val movieListViewModel: MovieListViewModel by inject()
    private lateinit var layoutManager: GridLayoutManager

    private var currentPage = PaginationListener.PAGE_START
    private var isLastPage = false
    private var isLoading = false
    private var itemCount = 0
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d("my_debug", "popular movies fragment onActivityCreated")

        sharedViewModel.savedMovies.observe(requireActivity(), Observer { item ->
            adapter.updateItem(item)
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("my_debug", "popular movies fragment onCreateView")

        return inflater.inflate(R.layout.main_menu_decor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("my_debug", "popular movies fragment onViewCreated")

        bindViews(view)
        refresh()
        setAdapter()
        getMovies(currentPage)
    }

    private fun bindViews(view: View) {
        Log.d("my_debug", "popular movies fragment bindViews")

        toolbar = view.findViewById(R.id.toolbar)
        toolbar.text = getString(R.string.movies)
        recView = view.findViewById(R.id.recycler_view)
        swipeRefreshLayout = view.findViewById(R.id.swipeInFragments)
    }

    private fun setAdapter() {
        Log.d("my_debug", "popular movies fragment setAdapter")
        layoutManager = GridLayoutManager(requireActivity(), 3)
        recView.layoutManager = layoutManager
        adapter = MovieListAdapter(this, requireActivity())
        recView.adapter = adapter

        recView.addOnScrollListener(object : PaginationListener(layoutManager) {
            override fun loadMoreItems() {
                isLoading = true
                currentPage++
                getMovies(currentPage)
            }

            override fun isLastPage(): Boolean = isLastPage
            override fun isLoading(): Boolean = isLoading
        })
    }

    private fun refresh() {
        Log.d("my_debug", "popular movies fragment refresh")
        swipeRefreshLayout.setOnRefreshListener {
            adapter.clearAll()
            itemCount = 0
            currentPage= PaginationListener.PAGE_START
            isLastPage = false
            movieListViewModel.getPopularMovieList(currentPage)
        }
    }

    private fun getMovies(page: Int) {
        Log.d("my_debug", "popular movies fragment getMovies:")

        movieListViewModel.getPopularMovieList(page)
        movieListViewModel.liveData.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is MovieListViewModel.State.ShowLoading -> {
                    swipeRefreshLayout.isRefreshing = true
                }
                is MovieListViewModel.State.HideLoading -> {
                    swipeRefreshLayout.isRefreshing = false
                }
                is MovieListViewModel.State.Result -> {
                    adapter?.removeFooterLoading()
                    adapter?.addItems(result.list!!)
                    adapter?.addFooterLoading()
                    isLoading = false
                }
            }
        })
    }

    override fun itemClick(position: Int, item: Movie) {
        val bundle = Bundle()
        bundle.putInt("id", item.id)
        val movieInfoFragment = MovieInfoFragment()
        movieInfoFragment.arguments = bundle
        parentFragmentManager.beginTransaction().add(R.id.frameLayout, movieInfoFragment)
            .addToBackStack(null).commit()
        requireActivity().bottom_navigation.visibility = View.GONE
    }

    override fun addToFavourites(position: Int, item: Movie) {
        movieListViewModel.addToFavourites(item)
        sharedViewModel.setMovie(item)
    }
}
