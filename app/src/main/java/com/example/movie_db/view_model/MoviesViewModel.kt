package com.example.movie_db.view_model

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.movie_db.BuildConfig
import com.example.movie_db.model.data.movie.Movie
import com.example.movie_db.model.data.authentication.User
import com.example.movie_db.model.data.movie.SavingResponse
import com.example.movie_db.model.data.movie.SelectedMovie
import com.example.movie_db.model.repository.MovieRepository
import com.example.movie_db.view.activities.MovieInfoActivity
import com.google.gson.Gson
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

    fun addToFavourites(item: Movie) {
        lateinit var selectedMovie: SelectedMovie

        if (!item.isSaved) {
            item.isSaved = true
            selectedMovie = SelectedMovie("movie", item.id, item.isSaved)
        } else {
            item.isSaved = false
            selectedMovie = SelectedMovie("movie", item.id, item.isSaved)
        }
        addRemoveFavourites(selectedMovie)
    }

    private fun addRemoveFavourites(selectedMovie: SelectedMovie) {
        launch {
            try {
                val body = JsonObject().apply {
                    addProperty("media_type", "movie")
                    addProperty("media_id", selectedMovie.movieId)
                    addProperty("favorite", selectedMovie.isSaved)
                }
                movieRepository.addRemoveSavedCoroutine(
                    User.user?.userId!!,
                    BuildConfig.MOVIE_DB_API_KEY,
                    User.user?.sessionId.toString(),
                    body
                )
            } catch (e: Exception) {
                withContext(Dispatchers.IO) {
                    movieRepository.updateMovieIsSaved(
                        selectedMovie.isSaved,
                        selectedMovie.movieId
                    )
//                    val movieStatus =
//                        MovieStatus(selectedMovie.movieId, selectedMovie.isSaved)
//                    movieRepository.insertLocalMovieStatus(movieStatus)
                }
            }
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
        data class Result(val list: List<Movie>) : State()
        data class IsFavorite(val isFavorite: Boolean) : State()
    }

//    private fun updateFavourites() {
//        val moviesToUpdate = movieRepository.getLocalMovieStatuses()
//        if (!moviesToUpdate.isNullOrEmpty()) {
//            for (movie in moviesToUpdate) {
//                val selectedMovie = SelectedMovie(
//                    movieId = movie.movieId,
//                    isSaved = movie.selectedStatus
//                )
//                addRemoveFavourites(selectedMovie)
//            }
//        }
//        movieRepository.deleteLocalMovieStatuses()
//    }
}
