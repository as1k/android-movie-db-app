package com.example.movie_db.view_model

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.movie_db.BuildConfig
import com.example.movie_db.model.data.authentication.User
import com.example.movie_db.model.data.movie.Movie
import com.example.movie_db.model.data.movie.SavingResponse
import com.example.movie_db.model.database.MovieDao
import com.example.movie_db.model.database.MovieDatabase
import com.example.movie_db.model.network.Retrofit
import com.example.movie_db.view.activities.MovieInfoActivity
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Exception
import kotlin.coroutines.CoroutineContext

class MovieInfoViewModel(context: Context) : ViewModel(), CoroutineScope {
    private val job = Job()
    var liveData = MutableLiveData<State>()
    private val movieDao: MovieDao = MovieDatabase.getDatabase(context).movieDao()

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
                val response = Retrofit.getPostApi()
                    .getMovieCoroutine(movieId, BuildConfig.MOVIE_DB_API_KEY)
                if (response.isSuccessful) {
                    val result = Gson().fromJson(response.body(), Movie::class.java)
                    movie = result
                } else {
                    movieDao.getMovieInfo(movieId)
                    movie = movieDao.getMovieInfo(movieId)
                }
            } catch (e: Exception) {
                movieDao.getMovieInfo(movieId)
                movie = movieDao.getMovieInfo(movieId)
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
                Retrofit.getPostApi().addRemoveSavedCoroutine(
                    User.user?.userId,
                    BuildConfig.MOVIE_DB_API_KEY,
                    User.user?.sessionId,
                    body
                )
                val movie = movieDao.getMovieInfo(movieId)
                movie.isSaved = !movie.isSaved
                movieDao.insertMovieInfo(movie)
            } catch (e: Exception) {
                val movie = movieDao.getMovieInfo(movieId)
                movie.isSaved = !movie.isSaved
                movieDao.insertMovieInfo(movie)
                MovieInfoActivity.notSynced = true
            }
        }
    }

    fun isFavoriteMovie(movieId: Int) {
        launch {
            try {
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
                    liveData.value = State.IsFavorite(like)
                }
            } catch (e: Exception) {
                val movie = movieDao.getMovieInfo(movieId)
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
