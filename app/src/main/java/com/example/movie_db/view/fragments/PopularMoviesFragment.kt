package com.example.movie_db.view.fragments

import android.os.Bundle
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
import org.koin.androidx.viewmodel.ext.android.viewModel

class PopularMoviesFragment : Fragment(), MovieListAdapter.RecyclerViewItemClick {

    private lateinit var adapter: MovieListAdapter
    private lateinit var recView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var toolbar: TextView
    private val movieListViewModel by viewModel<MovieListViewModel>()
    private lateinit var layoutManager: GridLayoutManager

    private var currentPage = PaginationListener.PAGE_START
    private var isLastPage = false
    private var isLoading = false
    private var itemCount = 0
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sharedViewModel.savedMovies.observe(requireActivity(), Observer { item ->
            adapter.updateItem(item)
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.main_menu_decor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews(view)
        refresh()
        setAdapter()
        getMovies(currentPage)
    }

    override fun onResume() {
        super.onResume()
        swipeRefreshLayout.isRefreshing = true
        requireActivity().bottom_navigation.visibility = View.VISIBLE
        swipeRefreshLayout.isRefreshing = false
    }

    private fun bindViews(view: View) {
        toolbar = view.findViewById(R.id.toolbar)
        toolbar.text = getString(R.string.movies)
        recView = view.findViewById(R.id.recycler_view)
        swipeRefreshLayout = view.findViewById(R.id.swipeInFragments)
    }

    private fun setAdapter() {
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
        recView.layoutManager = LinearLayoutManager(requireActivity())
        swipeRefreshLayout.setOnRefreshListener {
            adapter.clearAll()
            itemCount = 0
            currentPage= PaginationListener.PAGE_START
            isLastPage = false
            movieListViewModel.getPopularMovieList(currentPage)
        }
    }

    private fun getMovies(page: Int) {
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
                    adapter.removeLoadingProgressBar()
                    adapter.addItems(result.list!!)
                    adapter.addLoadingProgressBar()
                    isLoading = false
                }
            }
        })
    }

    override fun itemClick(position: Int, item: Movie) {
        val bundle = Bundle()
        bundle.putInt("id", item.id)
        val movieInfoFragment =
            MovieInfoFragment()
        movieInfoFragment.arguments = bundle
        parentFragmentManager.beginTransaction().add(R.id.frameLayout, movieInfoFragment).addToBackStack(null).commit()
        requireActivity().bottom_navigation.visibility = View.GONE
    }

    override fun like(position: Int, item: Movie) {
        movieListViewModel.addToFavourites(item)
        sharedViewModel.setMovie(item)
    }
}
