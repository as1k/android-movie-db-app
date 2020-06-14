package com.example.movie_db.view_model

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.movie_db.BuildConfig
import com.example.movie_db.model.data.movie.Movie
import com.example.movie_db.model.data.authentication.User
import com.example.movie_db.model.network.Retrofit
import com.example.movie_db.model.repository.MovieRepository
import com.example.movie_db.model.repository.MovieRepositoryImpl
import com.example.movie_db.view.activities.MovieInfoActivity
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import java.lang.Exception
import kotlin.coroutines.CoroutineContext

class MoviesViewModel(
    private val context: Context,
    private var movieRepository: MovieRepository
) : ViewModel(), CoroutineScope {

    private val job = Job()
    val liveData = MutableLiveData<State>()

//    private val movieDao: MovieDao = MovieDatabase.getDatabase(context = context).movieDao()
//    private val movieRepository: MovieRepository? = MovieRepositoryImpl(Retrofit, movieDao)

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    fun getMovies(page: Int) {
        launch {
            liveData.value = State.ShowLoading
            val list = withContext(Dispatchers.IO) {
                try {
                    if (MovieInfoActivity.notSynced) {
                        val savedMovieList = movieRepository?.getMoviesDB()
                        if (savedMovieList != null)
                            for (movie in savedMovieList) {
                                val body = JsonObject().apply {
                                    addProperty("media_type", "movie")
                                    addProperty("media_id", movie.id)
                                    addProperty("favorite", movie.isSaved)
                                }
                                movieRepository?.addRemoveSavedCoroutine(
                                    User.user?.userId,
                                    BuildConfig.MOVIE_DB_API_KEY,
                                    User.user?.sessionId,
                                    body
                                )
                            }
                        MovieInfoActivity.notSynced = false
                    }
                    try {
                        val result = movieRepository?.getMoviesCoroutine(BuildConfig.MOVIE_DB_API_KEY, page)
                        if (!result.isNullOrEmpty()) {
                            movieRepository?.insertMoviesDB(result as List<Movie>)
                        }
                        result
                    } catch (e: Exception) {
                        movieRepository?.getMoviesDB() ?: emptyList()
                    }
                } catch (e: Exception) {
                    movieRepository?.getMoviesDB() ?: emptyList()
                }
            }
            liveData.value = State.HideLoading
            liveData.value = State.Result(list as List<Movie>)
        }
    }

    fun getSavedMovies() {
        launch {
            liveData.value = State.ShowLoading
            val list = withContext(Dispatchers.IO) {
                try {
                    if (MovieInfoActivity.notSynced) {
                        val savedMovieList = movieRepository?.getMoviesDB()
                        if (savedMovieList != null)
                            for (movie in savedMovieList) {
                                val body = JsonObject().apply {
                                    addProperty("media_type", "movie")
                                    addProperty("media_id", movie.id)
                                    addProperty("favorite", movie.isSaved)
                                }
                                movieRepository?.addRemoveSavedCoroutine(
                                    User.user?.userId,
                                    BuildConfig.MOVIE_DB_API_KEY,
                                    User.user?.sessionId,
                                    body
                                )
                            }
                        MovieInfoActivity.notSynced = false
                    }

                    try {
                        val result = movieRepository?.getSavedMoviesCoroutine(
                            User.user?.userId!!,
                            BuildConfig.MOVIE_DB_API_KEY,
                            User.user?.sessionId.toString()
                        )
                        if (!result.isNullOrEmpty()) {
                            for (movie in result)
                                movie?.isSaved = true
                            movieRepository?.insertMoviesDB(result as List<Movie>)
                        }
                        result
                    } catch (e: Exception) {
                        movieRepository?.getFavoriteDB() ?: emptyList()
                    }
                } catch (e: Exception) {
                    movieRepository?.getFavoriteDB() ?: emptyList()
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
