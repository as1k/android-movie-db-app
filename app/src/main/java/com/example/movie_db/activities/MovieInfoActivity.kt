package com.example.movie_db.activities

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.movie_db.R
import com.example.movie_db.Retrofit
import com.example.movie_db.BuildConfig
import com.example.movie_db.classes.User
import com.example.movie_db.classes.SavingResponse
import com.example.movie_db.classes.Movie
import com.google.gson.Gson
import com.google.gson.JsonObject
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import com.example.movie_db.classes.*
import java.lang.Exception

class MovieInfoActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var title: TextView
    private lateinit var back: ImageButton
    private lateinit var review: TextView
    private lateinit var imagePoster: ImageView
    private lateinit var rating: TextView
    private lateinit var releaseDate: TextView
    private lateinit var popularity: TextView
    private lateinit var adultContent: TextView
    private lateinit var save: ImageButton
    private var isSaved: Boolean = false
    private var movieId: Int = 1
    private val job = Job()

    companion object {
        var notSynced: Boolean = false
    }

    private var movieDao: MovieDao? = null

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.movie_info_activity)

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        title = findViewById(R.id.title)
        review = findViewById(R.id.overview)
        imagePoster = findViewById(R.id.poster)
        releaseDate = findViewById(R.id.releasedate)
        adultContent = findViewById(R.id.adult)
        rating = findViewById(R.id.rate)
        popularity = findViewById(R.id.popularity)
        back = findViewById(R.id.back)
        save = findViewById(R.id.save)
        movieId = intent.getIntExtra("movie_id", 1)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        movieDao = MovieDatabase.getDatabase(context = this).movieDao()

        back.setOnClickListener {
            onBackPressed()
        }

        swipeRefreshLayout.setOnRefreshListener {
            getMovieCoroutine()
        }

        save.setOnClickListener {
            if (isSaved) {
                Glide.with(this).load(R.drawable.ic_bookmark_filled).into(save)
            } else {
                Glide.with(this).load(R.drawable.ic_bookmark).into(save)
            }
            saveMovieCoroutine(!isSaved)
            getMovieCoroutine()
        }
        getMovieCoroutine()
    }

    private fun getMovieCoroutine() {
        launch {
            try {
                val response = Retrofit.getPostApi()
                    .getMovieCoroutine(movieId, BuildConfig.MOVIE_DB_API_KEY)
                if (response.isSuccessful) {
                    val result = Gson().fromJson(response.body(), Movie::class.java)
                    writeInViews(result)
                    isSavedCoroutine()
                    if (result == null) {
                        movieDao?.insertMovieInfo(result as Movie)
                    }
                    result
                } else {
                    movieDao?.getMovieInfo(movieId)
                }
            } catch (e: Exception) {
                writeInViews(movieDao?.getMovieInfo(movieId)!!)
            }
        }
    }

    private fun saveMovieCoroutine(isFavorite: Boolean) {
        launch {
            try {
                val body = JsonObject().apply {
                    addProperty("media_type", "movie")
                    addProperty("media_id", movieId)
                    addProperty("favorite", isFavorite)
                }
                Retrofit.getPostApi().addRemoveSavedCoroutine(
                    User.user?.userId,
                    BuildConfig.MOVIE_DB_API_KEY,
                    User.user?.sessionId,
                    body
                )
            } catch (e: Exception) {
                val movie = movieDao?.getMovieInfo(movieId)
                if (isSaved) {
                    movie?.isSaved = false
                    isSaved = false
                } else {
                    movie?.isSaved = true
                    isSaved = true
                }
                movieDao?.insertMovieInfo(movie!!)
                notSynced = true
            }
        }
    }

    private fun isSavedCoroutine() {
        launch {
            val response = Retrofit.getPostApi().isSavedCoroutine(
                movieId,
                BuildConfig.MOVIE_DB_API_KEY,
                User.user?.sessionId
            )
            if (response.isSuccessful) {
                val like = Gson().fromJson(
                    response.body(),
                    SavingResponse::class.java
                ).favorite
                isSaved = if (like) {
                    save.setImageResource(R.drawable.ic_bookmark)
                    true
                } else {
                    save.setImageResource(R.drawable.ic_bookmark_filled)
                    false
                }
            }
        }
    }

    fun writeInViews(movie: Movie) {
        review.text = movie.review
        Glide.with(this@MovieInfoActivity).load(movie.getPathToBackground())
            .into(imagePoster)
        title.text = movie.title
        releaseDate.text = movie.releaseDate
        if (movie.adultContent)
            adultContent.text = "18+"
        else
            adultContent.text = "12+"
        rating.text = movie.voteRating.toString()
        popularity.text = movie.popularity.toString()
        isSaved = movie.isSaved
        if (isSaved)
            save.setImageResource(R.drawable.ic_bookmark)
        else
            save.setImageResource(R.drawable.ic_bookmark_filled)
    }
}
