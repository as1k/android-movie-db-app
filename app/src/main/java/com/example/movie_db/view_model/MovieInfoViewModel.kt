package com.example.movie_db.view_model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.movie_db.BuildConfig
import com.example.movie_db.model.data.authentication.CurrentUser
import com.example.movie_db.model.data.movie.Movie
import com.example.movie_db.model.data.movie.FavouriteResponse
import com.example.movie_db.model.repository.MovieRepository
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Exception
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.*

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

    fun getMovie(movieId: Int?) {
        liveData.value = State.ShowLoading
        launch {
            val movie = withContext(Dispatchers.IO) {
                try {
                    val response = movieRepository
                        .getMovieRemote(movieId, BuildConfig.MOVIE_DB_API_KEY)
                    if (response != null) {
                        if (response.liked) {
                            movieRepository.setLikeStatusByIdLocal(true, response.id)
                        }
                    }
                    response
                } catch (e: Exception) {
                    movieRepository.getMovieInfoByIdLocal(movieId)
                }
            }
            liveData.value = State.HideLoading
            liveData.value = State.Result(movie)
        }
    }

    fun likeMovie(movie: Movie) {
        try {
            movie.liked = !movie.liked
            val body = JsonObject().apply {
                addProperty("media_type", "movie")
                addProperty("media_id", movie.id)
                addProperty("favorite", movie.liked)
            }
            likeUnlikeMovies(body)
            movieRepository.insertMovieInfoLocal(movie)
        } catch (e: Exception) {
            Log.d("my_debug", e.toString())
        }
    }

    private fun likeUnlikeMovies(body: JsonObject) {
        launch {
            try {
                movieRepository.likeUnlikeMoviesCoroutineRemote(
                    CurrentUser.user?.userId,
                    BuildConfig.MOVIE_DB_API_KEY,
                    CurrentUser.user?.sessionId,
                    body
                )
            } catch (e: Exception) { }
        }
    }

    fun isLikedMovie(movieId: Int?) {
        launch {
            val likeInt = withContext(Dispatchers.IO) {
                try {
                    val response = movieRepository.isLikedRemote(
                        movieId,
                        BuildConfig.MOVIE_DB_API_KEY,
                        CurrentUser.user?.sessionId
                    )
                    val like = Gson().fromJson(
                        response,
                        FavouriteResponse::class.java
                    ).favorite
                    if (like) {
                        movieRepository.setLikeStatusByIdLocal(true, movieId)
                        1
                    } else {
                        movieRepository.setLikeStatusByIdLocal(false, movieId)
                        0
                    }
                } catch (e: Exception) {
                    movieRepository.checkIsLikedByIdLocal(movieId)
                }
            }
            liveData.value = State.IsFavorite(likeInt)
        }
    }


    sealed class State {
        object ShowLoading : State()
        object HideLoading : State()
        data class Result(val movie: Movie?) : State()
        data class IsFavorite(val likeInt: Int?) : State()
    }
}
