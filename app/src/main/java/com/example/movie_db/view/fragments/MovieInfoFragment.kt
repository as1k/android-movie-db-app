package com.example.movie_db.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.movie_db.R
import com.example.movie_db.model.data.movie.Movie
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.lifecycle.Observer
import com.example.movie_db.view.MoviesApplication
import com.example.movie_db.view_model.MovieInfoViewModel
import com.example.movie_db.view_model.SharedViewModel

class MovieInfoFragment : Fragment() {

    private lateinit var imagePoster: ImageView
    private lateinit var title: TextView
    private lateinit var review: TextView
    private lateinit var releaseDate: TextView
    private lateinit var adultContent: TextView
    private lateinit var rating: TextView
    private lateinit var popularity: TextView
    private lateinit var save: ImageButton
    private lateinit var back: ImageButton

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var movieInfoViewModel: MovieInfoViewModel
    private var movie: Movie? = null
    private var movieId: Int? = null
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.movie_info_activity, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViews(view)

        val bundle = this.arguments
        movieId = bundle?.getInt("id")

        onBackPressed()
        refresh()
        setViewModel()
        getMovieCoroutine(id = movieId!!)
    }

    private fun setViewModel() {
        val appContainer = (activity?.application as MoviesApplication).appContainer
        movieInfoViewModel = appContainer.moviesViewModelFactory.createMovie()
    }

    private fun getMovieCoroutine(id: Int?) {
        movieInfoViewModel.getMovie(id)
        movieInfoViewModel.liveData.observe(requireActivity(), Observer { result ->
            when (result) {
                is MovieInfoViewModel.State.ShowLoading -> {
                    swipeRefreshLayout.isRefreshing = true
                }
                is MovieInfoViewModel.State.HideLoading -> {
                    swipeRefreshLayout.isRefreshing = false
                }
                is MovieInfoViewModel.State.Result -> {
                    movie = result.movie
                    setData(movie!!)
                }
                is MovieInfoViewModel.State.IsFavorite -> {
                    if (result.likeInt == 1 || result.likeInt == 11) {
                        save.setImageResource(R.drawable.ic_bookmark)
                        movie?.liked = true
                    } else {
                        save.setImageResource(R.drawable.ic_bookmark_filled)
                        movie?.liked = false
                    }
                }
            }
        })
    }

    private fun refresh() {
        swipeRefreshLayout.setOnRefreshListener {
            getMovieCoroutine(movieId)
        }
    }

    private fun isFavoriteMovie(id: Int) {
        movieInfoViewModel.isFavoriteMovie(id)
    }

    private fun likeMovie(movie: Movie) {
        movieInfoViewModel.likeMovie(movie)
    }

    private fun setData(movie: Movie) {
        Glide.with(this)
            .load("https://image.tmdb.org/t/p/w342${movie.pathToBackground}")
            .into(imagePoster)

        title.text = movie.title
        review.text = movie.review
        releaseDate.text = movie.releaseDate
        if (movie.adultContent)
            adultContent.text = getString(R.string.adult_content)
        else
            adultContent.text = getString(R.string.not_adult_content)
        rating.text = movie.voteRating.toString()
        popularity.text = movie.popularity.toString()
        isFavoriteMovie(movie.id)

        save.setOnClickListener {
            if (!movie.liked) {
                save.setImageResource(R.drawable.ic_bookmark)
            } else {
                save.setImageResource(R.drawable.ic_bookmark_filled)
            }
            likeMovie(movie)
            sharedViewModel.setMovie(movie)
        }
    }

    private fun bindViews(view: View) {
        imagePoster = view.findViewById(R.id.poster)
        title = view.findViewById(R.id.title)
        review = view.findViewById(R.id.overview)
        releaseDate = view.findViewById(R.id.releasedate)
        adultContent = view.findViewById(R.id.adult)
        rating = view.findViewById(R.id.rate)
        popularity = view.findViewById(R.id.popularity)
        save = view.findViewById(R.id.save)
        back = view.findViewById(R.id.back)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
    }

    private fun onBackPressed() {
        back.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }
}

    ////////////////////////////////////////////

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.movie_info_activity)
//
//        val movieDao: MovieDao = MovieDatabase.getDatabase(this).movieDao()
//        movieInfoViewModel = MovieInfoViewModel(MovieRepositoryImpl(Retrofit, movieDao))
//
//        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
//        title = findViewById(R.id.title)
//        review = findViewById(R.id.overview)
//        imagePoster = findViewById(R.id.poster)
//        releaseDate = findViewById(R.id.releasedate)
//        adultContent = findViewById(R.id.adult)
//        rating = findViewById(R.id.rate)
//        popularity = findViewById(R.id.popularity)
//        back = findViewById(R.id.back)
//        save = findViewById(R.id.save)
//        movieId = intent.getIntExtra("movie_id", 1)
//        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
//
//        back.setOnClickListener {
//            onBackPressed()
//        }
//
//        swipeRefreshLayout.setOnRefreshListener {
//            movieInfoViewModel.isFavoriteMovie(movieId)
//            movieInfoViewModel.getMovie(movieId)
//            observe()
//        }
//
//        fun refresh() {
//            movieInfoViewModel.isFavoriteMovie(movieId)
//            movieInfoViewModel.getMovie(movieId)
//            observe()
//        }
//
//        save.setOnClickListener {
//            if (isSaved) {
//                Glide.with(this).load(R.drawable.ic_bookmark_filled).into(save)
//            } else {
//                Glide.with(this).load(R.drawable.ic_bookmark).into(save)
//            }
//            movieInfoViewModel.likeMovie(!isSaved, movieId)
//            refresh()
//        }
//
//        movieInfoViewModel.isFavoriteMovie(movieId)
//        movieInfoViewModel.getMovie(movieId)
//        observe()
//    }
//
//    fun observe() {
//        movieInfoViewModel.liveData.observe(this, Observer { result ->
//            when (result) {
//                is MovieInfoViewModel.State.ShowLoading -> {
//                    swipeRefreshLayout.isRefreshing = true
//                }
//                is MovieInfoViewModel.State.HideLoading -> {
//                    swipeRefreshLayout.isRefreshing = false
//                }
//                is MovieInfoViewModel.State.Result -> {
//                    writeInViews(result.movie)
//                }
//                is MovieInfoViewModel.State.IsFavorite -> {
//                    isSaved = result.isFavorite
//                    if (isSaved) {
//                        Glide.with(this).load(R.drawable.ic_bookmark).into(save)
//                    } else {
//                        Glide.with(this).load(R.drawable.ic_bookmark_filled).into(save)
//                    }
//                }
//            }
//        })
//    }
//
//    fun writeInViews(movie: Movie) {
//        review.text = movie.review
//        Glide.with(this@MovieInfoActivity).load("https://image.tmdb.org/t/p/w342${movie.pathToBackground}")
//            .into(imagePoster)
//        title.text = movie.title
//        releaseDate.text = movie.releaseDate
//        if (movie.adultContent)
//            adultContent.text = "18+"
//        else
//            adultContent.text = "12+"
//        rating.text = movie.voteRating.toString()
//        popularity.text = movie.popularity.toString()
//    }
//
//}
