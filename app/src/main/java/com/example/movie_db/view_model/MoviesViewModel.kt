package com.example.movie_db.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.movie_db.BuildConfig
import com.example.movie_db.model.data.movie.Movie
import com.example.movie_db.model.data.authentication.User
import com.example.movie_db.model.repository.MovieRepository
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import java.lang.Exception
import kotlin.coroutines.CoroutineContext

class MoviesViewModel(
    private var movieRepository: MovieRepository
) : ViewModel(), CoroutineScope {

    private val job = Job()
    val liveData = MutableLiveData<State>()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    fun getMovies(page: Int = 1) {
        launch {
            if (page == 1) liveData.value = State.ShowLoading
            val list = withContext(Dispatchers.IO) {
                try {
                    val response = movieRepository.getMoviesCoroutine(BuildConfig.MOVIE_DB_API_KEY, page)
                    val favResponse = movieRepository.getSavedMoviesCoroutine(
                        User.user?.userId,
                        BuildConfig.MOVIE_DB_API_KEY,
                        User.user?.sessionId
                    )
                    if (!response.isNullOrEmpty()) {
                        movieRepository.insertMoviesDB(response)
                    }
                    if (!response.isNullOrEmpty()) {
                        for (movie in response) {
                            for (favorite in favResponse!!) {
                                if(movie.id == favorite.id) {
                                    movie.liked = true
                                    movieRepository.setLikeDB(true, movie.id)
                                }
                            }
                        }
                    }
                    response
                } catch (e: Exception) {
                    movieRepository.getMoviesDB()
                }
            }
            liveData.value = State.HideLoading
            liveData.value = State.Result(list)
        }
    }

    fun getSavedMovies() {
        launch {
            liveData.value = State.ShowLoading
            val list = withContext(Dispatchers.IO) {
                try {
                    val response = movieRepository.getSavedMoviesCoroutine(
                        User.user?.userId,
                        BuildConfig.MOVIE_DB_API_KEY,
                        User.user?.sessionId
                    )
                    if (!response.isNullOrEmpty()) {
                        for (m in response) {
                            m.liked = true
                            movieRepository.setLikeDB(true, m.id)
                        }
                    }
                    response
                } catch (e: Exception) {
                    movieRepository.getAllLikedDB(true)
                }
            }
            liveData.value = State.HideLoading
            liveData.value = State.Result(list)
        }
    }

    fun addToFavourites(movie: Movie) {
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

    sealed class State {
        object ShowLoading : State()
        object HideLoading : State()
        data class Result(val list: List<Movie>?) : State()
    }
}
