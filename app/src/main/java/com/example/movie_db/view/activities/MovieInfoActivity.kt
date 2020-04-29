package com.example.movie_db.view.activities

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.movie_db.R
import com.example.movie_db.model.data.movie.Movie
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.movie_db.view_model.MovieInfoVM
import com.example.movie_db.view_model.VMProviderFactory

class MovieInfoActivity : AppCompatActivity() {

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
    private lateinit var movieInfoVM: MovieInfoVM

    companion object {
        var notSynced: Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.movie_info_activity)

        val vmProviderFactory = VMProviderFactory(this)
        movieInfoVM = ViewModelProvider(this, vmProviderFactory)
            .get(MovieInfoVM::class.java)

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

        back.setOnClickListener {
            onBackPressed()
        }

        swipeRefreshLayout.setOnRefreshListener {
            movieInfoVM.isFavoriteMovie(movieId)
            movieInfoVM.getMovie(movieId)
            observe()
        }

        fun refresh() {
            movieInfoVM.isFavoriteMovie(movieId)
            movieInfoVM.getMovie(movieId)
            observe()
        }

        save.setOnClickListener {
            if (isSaved) {
                Glide.with(this).load(R.drawable.ic_bookmark_filled).into(save)
            } else {
                Glide.with(this).load(R.drawable.ic_bookmark).into(save)
            }
            movieInfoVM.likeMovie(!isSaved, movieId)
            refresh()
        }

        movieInfoVM.isFavoriteMovie(movieId)
        movieInfoVM.getMovie(movieId)
        observe()
    }

    fun observe() {
        movieInfoVM.liveData.observe(this, Observer { result ->
            when (result) {
                is MovieInfoVM.State.ShowLoading -> {
                    swipeRefreshLayout.isRefreshing = true
                }
                is MovieInfoVM.State.HideLoading -> {
                    swipeRefreshLayout.isRefreshing = false
                }
                is MovieInfoVM.State.Result -> {
                    writeInViews(result.movie)
                }
                is MovieInfoVM.State.IsFavorite -> {
                    isSaved = result.isFavorite
                    if (isSaved) {
                        Glide.with(this).load(R.drawable.ic_bookmark).into(save)
                    } else {
                        Glide.with(this).load(R.drawable.ic_bookmark_filled).into(save)
                    }
                }
            }
        })
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
    }

}
