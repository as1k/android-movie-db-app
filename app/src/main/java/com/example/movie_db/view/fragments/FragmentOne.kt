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
import com.example.movie_db.AdapterForMovies
import com.example.movie_db.R
import com.example.movie_db.model.data.movie.Movie
import kotlin.collections.ArrayList
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.movie_db.view_model.MoviesVM
import com.example.movie_db.view_model.VMProviderFactory

class FragmentOne : Fragment() {

    private lateinit var adapter: AdapterForMovies
    private lateinit var movies: List<Movie>
    private lateinit var recView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var toolbar: TextView
    private lateinit var moviesVM: MoviesVM

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

        val viewModelProviderFactory = VMProviderFactory(this.context!!)
        moviesVM =
            ViewModelProvider(this, viewModelProviderFactory).get(MoviesVM::class.java)
    }

    private fun setAdapter() {
        recView.layoutManager = LinearLayoutManager(activity)
        swipeRefreshLayout.setOnRefreshListener {
            viewsOnInit()
            moviesVM.getMovies()
        }
        moviesVM.getMovies()

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
        viewsOnInit()
    }

    private fun viewsOnInit() {
        movies = ArrayList()
        this.adapter = activity?.applicationContext?.let {
            AdapterForMovies(
                it,
                movies
            )
        }!!
        recView.layoutManager = GridLayoutManager(activity, 3)
        recView.itemAnimator= DefaultItemAnimator()
        recView.adapter = this.adapter
        this.adapter.notifyDataSetChanged()
    }

}
