package com.example.movie_db.fragments

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
import com.example.movie_db.BuildConfig
import com.example.movie_db.R
import com.example.movie_db.Retrofit
import com.example.movie_db.classes.User
import com.example.movie_db.classes.Movie
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import android.content.Context
import com.example.movie_db.classes.*
import kotlinx.coroutines.*
import java.lang.Exception
import com.example.movie_db.activities.MovieInfoActivity
import com.google.gson.JsonObject

class FragmentSaved : Fragment(), CoroutineScope {

    private lateinit var recView: RecyclerView
    private lateinit var adapter: AdapterForMovies
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var movies: List<Movie>
    private lateinit var toolbar: TextView
    private val job = Job()

    private var movieDao: MovieDao? = null

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

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
        toolbar = rootView.findViewById(R.id.toolbar)
        toolbar.text = "Favorites"

        movieDao = MovieDatabase.getDatabase(activity as Context).movieDao()

        recView.layoutManager = LinearLayoutManager(activity)
        swipeRefreshLayout = rootView.findViewById(R.id.main_content)
        swipeRefreshLayout.setOnRefreshListener {
            viewsOnInit()
        }
        viewsOnInit()
        return rootView
    }

    private fun viewsOnInit() {
        movies = ArrayList()
        adapter = activity?.applicationContext?.let { AdapterForMovies(it, movies) }!!
        recView.layoutManager = GridLayoutManager(activity, 3)
        recView.itemAnimator = DefaultItemAnimator()
        recView.adapter = adapter
        adapter.notifyDataSetChanged()

        jsonOnLoadCoroutine()
    }

    private fun jsonOnLoadCoroutine() {
        launch {
            swipeRefreshLayout.isRefreshing = true
            val list = withContext(Dispatchers.IO) {
                try {
                    if (MovieInfoActivity.notSynced) {
                        val savedMovieList = movieDao?.getAll()
                        if (savedMovieList != null)
                            for (movie in savedMovieList) {
                                val body = JsonObject().apply {
                                    addProperty("media_type", "movie")
                                    addProperty("media_id", movie.id)
                                    addProperty("favorite", movie.isSaved)
                                }
                                Retrofit.getPostApi().addRemoveSavedCoroutine(
                                    User.user?.userId,
                                    BuildConfig.MOVIE_DB_API_KEY,
                                    User.user?.sessionId,
                                    body
                                )
                            }
                        MovieInfoActivity.notSynced = false
                    }
                    val response = Retrofit.getPostApi()
                        .getSavedMoviesCoroutine(
                            User.user?.userId!!,
                            BuildConfig.MOVIE_DB_API_KEY,
                            User.user?.sessionId.toString()
                        )
                    if (response.isSuccessful) {
                        val result = response.body()?.getResults()
                        if (!result.isNullOrEmpty()) {
                            for (movie in result)
                                movie?.isSaved = true
                            movieDao?.insertAll(result as List<Movie>)
                        }
                        result
                    } else {
                        movieDao?.getFavorite() ?: emptyList()
                    }
                } catch (e: Exception) {
                    movieDao?.getFavorite() ?: emptyList()
                }
            }
            adapter.movies = list as List<Movie>
            adapter.notifyDataSetChanged()
            swipeRefreshLayout.isRefreshing = false
        }
    }
}