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
import androidx.lifecycle.Observer
import com.example.movie_db.model.database.MovieDao
import com.example.movie_db.model.database.MovieDatabase
import com.example.movie_db.model.network.Retrofit
import com.example.movie_db.model.repository.MovieRepositoryImpl
import com.example.movie_db.view_model.MoviesViewModel


class FragmentSaved : Fragment() {

    private lateinit var recView: RecyclerView
    private lateinit var adapter: AdapterForMovies
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var movies: List<Movie>
    private lateinit var toolbar: TextView
    private lateinit var moviesViewModel: MoviesViewModel

    override fun onResume() {
        super.onResume()
        swipeRefreshLayout.isRefreshing = true
        viewsOnInit()
        swipeRefreshLayout.isRefreshing = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: ViewGroup = inflater
            .inflate(
                R.layout.fragments_activity,
                container, false
            ) as ViewGroup

        recView = rootView.findViewById(R.id.recycler_view)
        recView.layoutManager = LinearLayoutManager(activity)
        toolbar = rootView.findViewById(R.id.toolbar)
        toolbar.text = "Favorites"

        val movieDao: MovieDao = MovieDatabase.getDatabase(requireContext()).movieDao()
        val movieRepository = MovieRepositoryImpl(Retrofit, movieDao)
        moviesViewModel = MoviesViewModel(requireContext(), movieRepository)

        swipeRefreshLayout = rootView.findViewById(R.id.main_content)
        swipeRefreshLayout.setOnRefreshListener {
            viewsOnInit()
            moviesViewModel.getSavedMovies()
        }

        viewsOnInit()
        return rootView
    }

    private fun viewsOnInit() {
        movies = ArrayList()
        adapter = activity?.applicationContext?.let {
            AdapterForMovies(
                it
            )
        }!!
        recView.layoutManager = GridLayoutManager(activity, 4)
        recView.itemAnimator = DefaultItemAnimator()
        recView.adapter = adapter
        adapter.notifyDataSetChanged()

        moviesViewModel.getSavedMovies()
        moviesViewModel.liveData.observe(this, Observer { result ->
            when (result) {
                is MoviesViewModel.State.ShowLoading -> {
                    swipeRefreshLayout.isRefreshing = true
                }
                is MoviesViewModel.State.HideLoading -> {
                    swipeRefreshLayout.isRefreshing = false
                }
                is MoviesViewModel.State.Result -> {
                    adapter.movies = result.list as MutableList<Movie>
                    adapter.notifyDataSetChanged()
                }
            }
        })
    }

}
