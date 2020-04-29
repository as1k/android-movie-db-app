package com.example.movie_db.fragments

import android.os.Bundle
import android.util.Log
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
import com.example.movie_db.classes.Movie
import com.example.movie_db.classes.MovieResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList

class FragmentOne : Fragment() {

    private lateinit var adapter: AdapterForMovies
    private lateinit var movies: List<Movie>
    private lateinit var recView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var toolbar: TextView

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
        recView = view.findViewById(R.id.recycler_view)
        swipeRefreshLayout = view.findViewById(R.id.main_content)
        toolbar.text = "Movies"
    }
    
    private fun setAdapter(){
        recView.layoutManager = LinearLayoutManager(activity)
        swipeRefreshLayout.setOnRefreshListener {
            viewsOnInit()
        }
        viewsOnInit()
    }

    private fun jsonOnLoad() {
        try {
            if (BuildConfig.MOVIE_DB_API_KEY.isEmpty()) {
                return
            }
            Retrofit.getPostApi()
                .getMovies(BuildConfig.MOVIE_DB_API_KEY)
                .enqueue(object : Callback<MovieResponse> {
                    override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                        swipeRefreshLayout.isRefreshing = false
                    }

                    override fun onResponse(
                        call: Call<MovieResponse>,
                        response: Response<MovieResponse>
                    ) {
                        Log.d("postList", response.body().toString())
                        if (response.isSuccessful) {
                            val list = response.body()?.getResults()
                            adapter.movies = list as List<Movie>
                            adapter.notifyDataSetChanged()
                        }
                        swipeRefreshLayout.isRefreshing = false
                    }
                })
        } catch (e: Exception){
            Toast.makeText(activity, e.toString(), Toast.LENGTH_SHORT)
        }
    }

    private fun viewsOnInit(){
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

        jsonOnLoad()
    }
}
