package com.example.movie_db

import android.annotation.SuppressLint
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class FragmentTwo: Fragment() {

    private lateinit var recView: RecyclerView
    private lateinit var adapter: AdapterForMovies
    private lateinit var swipeCase: SwipeRefreshLayout
    private lateinit var movies: List<Movie>
    private lateinit var toolbar: TextView

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater
            .inflate(
                R.layout.fragments_activity,
                container, false) as ViewGroup

        return rootView
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
        swipeCase = view.findViewById(R.id.main_content)
        toolbar.text = "Saved"
    }

    private fun setAdapter(){
        recView.layoutManager = LinearLayoutManager(activity)
        swipeCase.setOnRefreshListener {
            viewsOnInit()
        }
        viewsOnInit()
    }

    @SuppressLint("ShowToast")
    private fun jsonOnLoad() {
        try {
            if (BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()) {
                return
            }
            RetrofitService.getPostApi().getTrendingMovieList(BuildConfig.THE_MOVIE_DB_API_TOKEN)
                .enqueue(object : Callback<MovieResponse> {
                    override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                        swipeCase.isRefreshing = false
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
                        swipeCase.isRefreshing = false

                    }
                })
        } catch (e: Exception) {
            Toast.makeText(activity, e.toString(), Toast.LENGTH_SHORT)
        }
    }

    private fun viewsOnInit() {
        movies = ArrayList()
        this.adapter = activity?.applicationContext?.let { AdapterForMovies(it, movies) }!!
        recView.layoutManager = GridLayoutManager(activity, 3)
        recView.adapter = this.adapter
        recView.itemAnimator = DefaultItemAnimator()
        this.adapter.notifyDataSetChanged()

        jsonOnLoad()
    }
}