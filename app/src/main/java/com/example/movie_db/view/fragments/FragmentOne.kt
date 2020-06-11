package com.example.movie_db.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.movie_db.view.adapters.AdapterForMovies
import com.example.movie_db.R
import com.example.movie_db.model.data.movie.Movie
import kotlin.collections.ArrayList
import androidx.lifecycle.Observer
import com.example.movie_db.utils.PaginationListener
import com.example.movie_db.model.database.MovieDao
import com.example.movie_db.model.database.MovieDatabase
import com.example.movie_db.model.network.Retrofit
import com.example.movie_db.model.repository.MovieRepositoryImpl
import com.example.movie_db.view_model.MoviesViewModel

class FragmentOne : Fragment() {

    private lateinit var adapter: AdapterForMovies
    private lateinit var movies: List<Movie>
    private lateinit var recView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var toolbar: TextView
    private lateinit var moviesViewModel: MoviesViewModel
    private lateinit var layoutManager: GridLayoutManager
    private var currentPage = PaginationListener.PAGE_START
    private var isLastPage = false
    private var isLoading = false
    private var itemCount = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater
            .inflate(
                R.layout.fragments_activity,
                container, false) as ViewGroup
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState == null) {
            bindView(view)
            setAdapter()
        }
    }

    private fun bindView(view: View) {
        toolbar = view.findViewById(R.id.toolbar)
        toolbar.text = "Movies"
        recView = view.findViewById(R.id.recycler_view)
        swipeRefreshLayout = view.findViewById(R.id.main_content)

        val movieDao: MovieDao = MovieDatabase.getDatabase(requireContext()).movieDao()
        val movieRepository = MovieRepositoryImpl(Retrofit, movieDao)
        moviesViewModel = MoviesViewModel(requireContext(), movieRepository)
    }

    private fun setAdapter() {
        recView.layoutManager = LinearLayoutManager(activity)
        swipeRefreshLayout.setOnRefreshListener {
//            viewsOnInit()
//            moviesViewModel.getMovies(page)
            adapter?.clear()
            itemCount = 0
            currentPage = PaginationListener.PAGE_START
            isLastPage = false
            moviesViewModel.getMovies(currentPage)
        }
        moviesViewModel.getMovies(currentPage)

        moviesViewModel.liveData.observe(this, Observer { result ->
            when (result) {
                is MoviesViewModel.State.ShowLoading -> {
                    swipeRefreshLayout.isRefreshing = currentPage == 1
                }
                is MoviesViewModel.State.HideLoading -> {
                    swipeRefreshLayout.isRefreshing = false
                }
                is MoviesViewModel.State.Result -> {
                    itemCount = result.list?.size ?: 0
                    if (currentPage != PaginationListener.PAGE_START) {
                        adapter?.removeLoading()
                    }
//                    adapter.movies = result.list
                    adapter?.replaceItems(result.list)
                    adapter?.addLoading()
                    isLoading = false
                    adapter.notifyDataSetChanged()
                }
            }
        })
        viewsOnInit()
    }

    private fun viewsOnInit() {
        movies = ArrayList()
        this.adapter = activity?.applicationContext?.let {
            AdapterForMovies(
                it
            )
        }!!
        layoutManager = GridLayoutManager(activity, 4)
        recView.layoutManager = layoutManager
        recView.itemAnimator= DefaultItemAnimator()
        recView.adapter = this.adapter

        recView.addOnScrollListener(object : PaginationListener(layoutManager) {
            override fun loadMoreItems() {
                isLoading = true
                currentPage++
                moviesViewModel.getMovies(currentPage)
            }

            override fun isLastPage(): Boolean = isLastPage
            override fun isLoading(): Boolean = isLoading
        })

//        recView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//                if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN)) {
//                    page++
//                    moviesViewModel.getMovies(page)
//                }
//            }
//        })
//        this.adapter.notifyDataSetChanged()
    }
}
