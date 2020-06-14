package com.example.movie_db.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.movie_db.BuildConfig
import com.example.movie_db.model.data.authentication.User
import com.example.movie_db.model.data.movie.Movie
import com.example.movie_db.model.data.movie.SavingResponse
import com.example.movie_db.model.repository.MovieRepository
import com.example.movie_db.view.activities.MovieInfoActivity
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Exception
import kotlin.coroutines.CoroutineContext

class MovieInfoViewModel(private val movieRepository: MovieRepository) : ViewModel(),
    CoroutineScope {
    private val job = Job()
    var liveData = MutableLiveData<State>()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    fun getMovie(movieId: Int) {
        launch {
            liveData.value = State.ShowLoading
            var movie: Movie
            try {
                val movieInfo = movieRepository
                    .getMovieCoroutine(movieId, BuildConfig.MOVIE_DB_API_KEY)
                if (movieInfo != null) {
                    val result = Gson().fromJson(movieInfo, Movie::class.java)
                    movie = result
                } else {
                    movieRepository.getMovieInfoDB(movieId)
                    movie = movieRepository?.getMovieInfoDB(movieId)
                }
            } catch (e: Exception) {
                movieRepository.getMovieInfoDB(movieId)
                movie = movieRepository.getMovieInfoDB(movieId)
            }
            liveData.value = State.HideLoading
            liveData.value = State.Result(movie)
        }
    }

    fun likeMovie(isFavorite: Boolean, movieId: Int) {
        launch {
            try {
                val body = JsonObject().apply {
                    addProperty("media_type", "movie")
                    addProperty("media_id", movieId)
                    addProperty("favorite", isFavorite)
                }
                movieRepository.addRemoveSavedCoroutine(
                    User.user?.userId,
                    BuildConfig.MOVIE_DB_API_KEY,
                    User.user?.sessionId,
                    body
                )
                val movie = movieRepository.getMovieInfoDB(movieId)
                movie.isSaved = !movie.isSaved
                movieRepository.insertMovieInfoDB(movie)
            } catch (e: Exception) {
                val movie = movieRepository.getMovieInfoDB(movieId)
                movie.isSaved = !movie.isSaved
                movieRepository.insertMovieInfoDB(movie)
                MovieInfoActivity.notSynced = true
            }
        }
    }

    fun isFavoriteMovie(movieId: Int) {
        launch {
            try {
                val isSavedMovie = movieRepository.isSavedCoroutine(
                    movieId,
                    BuildConfig.MOVIE_DB_API_KEY,
                    User.user?.sessionId
                )
                if (isSavedMovie != null) {
                    val like = Gson().fromJson(
                        isSavedMovie,
                        SavingResponse::class.java
                    ).favorite
                    liveData.value = State.IsFavorite(like)
                }
            } catch (e: Exception) {
                val movie = movieRepository.getMovieInfoDB(movieId)
                liveData.value = State.IsFavorite(movie.isSaved)
            }
        }
    }

    sealed class State {
        object ShowLoading : State()
        object HideLoading : State()
        data class Result(val movie: Movie) : State()
        data class IsFavorite(val isFavorite: Boolean) : State()
    }

}
