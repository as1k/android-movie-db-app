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
import androidx.lifecycle.ViewModelProvider
import com.example.movie_db.view_model.MoviesVM
import com.example.movie_db.view_model.VMProviderFactory


class FragmentSaved : Fragment() {

    private lateinit var recView: RecyclerView
    private lateinit var adapter: AdapterForMovies
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var movies: List<Movie>
    private lateinit var toolbar: TextView
    private lateinit var moviesVM: MoviesVM

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

        val vmProviderFactory = VMProviderFactory(this.context!!)
        moviesVM = ViewModelProvider(this, vmProviderFactory)
            .get(MoviesVM::class.java)

        swipeRefreshLayout = rootView.findViewById(R.id.main_content)
        swipeRefreshLayout.setOnRefreshListener {
            viewsOnInit()
            moviesVM.getSavedMovies()
        }

        viewsOnInit()
        return rootView
    }

    private fun viewsOnInit() {
        movies = ArrayList()
        adapter = activity?.applicationContext?.let {
            AdapterForMovies(
                it,
                movies
            )
        }!!
        recView.layoutManager = GridLayoutManager(activity, 3)
        recView.itemAnimator = DefaultItemAnimator()
        recView.adapter = adapter
        adapter.notifyDataSetChanged()

        moviesVM.getSavedMovies()
        moviesVM.liveData.observe(this, Observer { result ->
            when (result) {
                is MoviesVM.State.ShowLoading -> {
                    swipeRefreshLayout.isRefreshing = true
                }
                is MoviesVM.State.HideLoading -> {
                    swipeRefreshLayout.isRefreshing = false
                }
                is MoviesVM.State.Result -> {
                    adapter.movies = result.list
                    adapter.notifyDataSetChanged()
                }
            }
        })
    }

}
