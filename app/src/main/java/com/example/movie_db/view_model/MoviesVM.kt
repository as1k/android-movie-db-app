package com.example.movie_db.view_model

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.movie_db.BuildConfig
import com.example.movie_db.model.data.movie.Movie
import com.example.movie_db.model.data.authentication.User
import com.example.movie_db.model.database.MovieDao
import com.example.movie_db.model.database.MovieDatabase
import com.example.movie_db.model.network.Retrofit
import com.example.movie_db.view.activities.MovieInfoActivity
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import java.lang.Exception
import kotlin.coroutines.CoroutineContext

class MoviesVM(context: Context) : ViewModel(), CoroutineScope {

    private val job = Job()
    val liveData = MutableLiveData<State>()
    private val movieDao: MovieDao = MovieDatabase.getDatabase(context = context).movieDao()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    fun getMovies() {
        launch {
            liveData.value = State.ShowLoading
            val list = withContext(Dispatchers.IO) {
                try {
                    if (MovieInfoActivity.notSynced) {
                        val savedMovieList = movieDao?.getAll()
                        if (savedMovieList != null)
                            for (movie in savedMovieList) {
                                val body = JsonObject().apply {
                                    addProperty("media_type", "movie")
                                    addProperty("media_id", movie.id)
                                    addProperty("favorite", movie.isSaved)
                                }
                                Retrofit.getPostApi().addRemoveSavedCoroutine(
                                    User.user?.userId,
                                    BuildConfig.MOVIE_DB_API_KEY,
                                    User.user?.sessionId,
                                    body
                                )
                            }
                        MovieInfoActivity.notSynced = false
                    }
                    val response = Retrofit.getPostApi()
                        .getMoviesCoroutine(BuildConfig.MOVIE_DB_API_KEY)
                    if (response.isSuccessful) {
                        val result = response.body()?.getResults()
                        if (!result.isNullOrEmpty()) {
                            movieDao?.insertAll(result as List<Movie>)
                        }
                        result
                    } else {
                        movieDao?.getAll() ?: emptyList()
                    }
                } catch (e: Exception) {
                    movieDao?.getAll() ?: emptyList()
                }
            }
            liveData.value = State.HideLoading
            liveData.value = State.Result(list as List<Movie>)
        }
    }

    sealed class State {
        object ShowLoading : State()
        object HideLoading : State()
        data class Result(val list: List<Movie>) : State()
    }

}
