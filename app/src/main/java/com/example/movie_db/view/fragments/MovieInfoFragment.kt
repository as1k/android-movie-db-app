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
import com.example.movie_db.view_model.MovieInfoViewModel
import com.example.movie_db.view_model.SharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MovieInfoFragment : Fragment() {

    private lateinit var poster: ImageView
    private lateinit var title: TextView
    private lateinit var overview: TextView
    private lateinit var releaseDate: TextView
    private lateinit var adultContent: TextView
    private lateinit var voteAverage: TextView
    private lateinit var popularity: TextView
    private lateinit var save: ImageButton
    private lateinit var back: ImageButton

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private val movieInfoViewModel by viewModel<MovieInfoViewModel>()
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
        getMovieCoroutine(id = movieId!!)
    }

    private fun bindViews(view: View) {
        poster = view.findViewById(R.id.poster)
        title = view.findViewById(R.id.title)
        overview = view.findViewById(R.id.overview)
        releaseDate = view.findViewById(R.id.release_date)
        adultContent = view.findViewById(R.id.adult_content)
        voteAverage = view.findViewById(R.id.vote_average)
        popularity = view.findViewById(R.id.popularity)
        save = view.findViewById(R.id.save)
        back = view.findViewById(R.id.back)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
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
                        save.setImageResource(R.drawable.ic_bookmark_clicked)
                        movie?.liked = true
                    } else {
                        save.setImageResource(R.drawable.ic_bookmark_not_clicked)
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
        movieInfoViewModel.isLikedMovie(id)
    }

    private fun likeMovie(movie: Movie) {
        movieInfoViewModel.likeMovie(movie)
    }

    private fun setData(movie: Movie) {
        Glide.with(this)
            .load("https://image.tmdb.org/t/p/w342${movie.backdropPath}")
            .into(poster)

        title.text = movie.title
        overview.text = movie.overview
        releaseDate.text = movie.releaseDate
        if (movie.includeAdult)
            adultContent.text = getString(R.string.adult_content)
        else
            adultContent.text = getString(R.string.not_adult_content)
        voteAverage.text = movie.voteAverage.toString()
        popularity.text = movie.popularity.toString()
        isFavoriteMovie(movie.id)

        save.setOnClickListener {
            if (!movie.liked) {
                save.setImageResource(R.drawable.ic_bookmark_clicked)
            } else {
                save.setImageResource(R.drawable.ic_bookmark_not_clicked)
            }
            likeMovie(movie)
            sharedViewModel.setMovie(movie)
        }
    }

    private fun onBackPressed() {
        back.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }
}