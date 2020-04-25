package com.example.movie_db.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
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
import com.example.movie_db.classes.MovieResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class FragmentSaved : Fragment() {

    private lateinit var recView: RecyclerView
    private lateinit var adapter: AdapterForMovies
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var movies: List<Movie>
    private lateinit var toolbar: TextView

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

        jsonOnLoad()
    }

    private fun jsonOnLoad() {
        try {
            if (BuildConfig.MOVIE_DB_API_KEY.isEmpty()) {
                return
            }
            Retrofit.getPostApi().getSavedMovies(
                User.user?.userId!!,
                BuildConfig.MOVIE_DB_API_KEY,
                User.user?.sessionId.toString()
            ).enqueue(object : Callback<MovieResponse> {
                override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                    swipeRefreshLayout.isRefreshing = false
                }

                override fun onResponse(
                    call: Call<MovieResponse>,
                    response: Response<MovieResponse>
                ) {
                    if (response.isSuccessful) {
                        val list = response.body()?.getResults()
                        adapter.movies = list as List<Movie>
                        adapter.notifyDataSetChanged()
                    }
                    swipeRefreshLayout.isRefreshing = false

                }
            })
        } catch (e: Exception) {
            Toast.makeText(activity, e.toString(), Toast.LENGTH_SHORT).show()
        }
    }
}