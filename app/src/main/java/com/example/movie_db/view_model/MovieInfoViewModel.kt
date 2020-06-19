package com.example.movie_db.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.movie_db.BuildConfig
import com.example.movie_db.model.data.authentication.User
import com.example.movie_db.model.data.movie.Movie
import com.example.movie_db.model.data.movie.SavingResponse
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
            var movie = withContext(Dispatchers.IO) {
                try {
                    val response = movieRepository
                        .getMovieCoroutine(movieId, BuildConfig.MOVIE_DB_API_KEY)
                    if (response != null) {
                        if (response.liked) {
                            movieRepository.setLikeDB(true, response.id)
                        }
                    }
                    response
                } catch (e: Exception) {
                    movieRepository.getMovieInfoDB(movieId)
                }
            }
            liveData.value = State.HideLoading
            liveData.value = State.Result(movie)
        }
    }

    fun likeMovie(movie: Movie) {
        movie.liked = !movie.liked
        val body = JsonObject().apply {
            addProperty("media_type", "movie")
            addProperty("media_id", movie.id)
            addProperty("favorite", movie.liked)
        }
        updateFavourite(body)
        movieRepository.insertMovieInfoDB(movie)
    }

    private fun updateFavourite(body: JsonObject) {
        launch {
            try {
                movieRepository.addRemoveSavedCoroutine(
                    User.user?.userId,
                    BuildConfig.MOVIE_DB_API_KEY,
                    User.user?.sessionId,
                    body
                )
            } catch (e: Exception) { }
        }
    }

    fun isFavoriteMovie(movieId: Int?) {
        launch {
            val likeInt = withContext(Dispatchers.IO) {
                try {
                    val response = movieRepository.isSavedCoroutine(
                        movieId,
                        BuildConfig.MOVIE_DB_API_KEY,
                        User.user?.sessionId
                    )
                    val like = Gson().fromJson(
                        response,
                        SavingResponse::class.java
                    ).favorite
                    if (like) {
                        movieRepository.setLikeDB(true, movieId)
                        1
                    } else {
                        movieRepository.setLikeDB(false, movieId)
                        0
                    }
//                        liveData.value = State.IsFavorite(like)
                } catch (e: Exception) {
                    movieRepository.getLikedDB(movieId)
//                    liveData.value = State.IsFavorite(movie.liked)
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
