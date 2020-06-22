package com.example.movie_db.view_model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.movie_db.BuildConfig
import com.example.movie_db.model.data.movie.Movie
import com.example.movie_db.model.data.authentication.CurrentUser
import com.example.movie_db.model.repository.MovieRepository
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.movie_list_item.view.*
import kotlinx.coroutines.*
import java.lang.Exception
import kotlin.coroutines.CoroutineContext

class MovieListViewModel(
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

    fun getPopularMovieList(page: Int = 1) {
        launch {
            if (page == 1) liveData.value = State.ShowLoading
            val list = withContext(Dispatchers.IO) {
                try {
                    val response = movieRepository
                        .getMovieListRemote(BuildConfig.MOVIE_DB_API_KEY, page)
                    Log.d("my_debug", "movie list viewmodel getpopmovies response:")
                    val favResponse = movieRepository.getLikedMovieListRemote(
                        CurrentUser.user?.userId,
                        BuildConfig.MOVIE_DB_API_KEY,
                        CurrentUser.user?.sessionId
                    )
                    Log.d("my_debug", "movie list viewmodel getfavmovies response:")
                    if (!response.isNullOrEmpty()) {
                        Log.d("my_debug", "Bug is here!!")
                        movieRepository.insertMovieListLocal(response)
                    }
                    if (!response.isNullOrEmpty()) {
                        Log.d("my_debug", "Getting response" + response[5].toString())
                        Log.d("my_debug", favResponse?.get(5).toString())

                        for (popular in response) {
                            for (favourite in favResponse!!) {
                                if(popular.id == favourite.id) {
                                    popular.liked = true
                                    movieRepository.setLikeStatusByIdLocal(true, popular.id)
                                }
                            }
                        }
                    }
                    response
                } catch (e: Exception) {

                    Log.d("my_debug", e.toString())
                    Log.d("my_debug", "movie list viewmodel getmovielistlocal")
                    movieRepository.getMovieListLocal() ?: emptyList()
                }
            }
            liveData.value = State.HideLoading
            liveData.value = State.Result(list)
        }
    }

    fun getLikedMovieList(){
        launch {
            liveData.value = State.ShowLoading

            val list = withContext(Dispatchers.IO) {
                try {
                    val response = movieRepository.getLikedMovieListRemote(
                        CurrentUser.user?.userId,
                        BuildConfig.MOVIE_DB_API_KEY,
                        CurrentUser.user?.sessionId
                    )
                    if (!response.isNullOrEmpty()) {
                        for (favourite in response) {
                            favourite.liked = true
                            movieRepository.setLikeStatusByIdLocal(true, favourite.id)
                        }
                    }
                    response
                } catch (e: Exception) {
                    movieRepository.getLikedMoviesLocal(true)
                }
            }
            liveData.value = State.HideLoading
            liveData.value = State.Result(list)
        }
    }

    fun addToFavourites(movie: Movie) {
        try {
            movie.liked = !movie.liked
            val body = JsonObject().apply {
                addProperty("media_type", "movie")
                addProperty("media_id", movie.id)
                addProperty("favorite", movie.liked)
            }
            updateFavourite(body)
            movieRepository.insertMovieInfoLocal(movie)
            movieRepository.setLikeStatusByIdLocal(movie.liked, movie.id)
            Log.d("my_debug", "viewmodel addToFavourites occured")
        }
        catch (e: Exception) {
            Log.d("my_debug", e.toString())
        }
    }

    private fun updateFavourite(body: JsonObject) {
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

    sealed class State {
        object ShowLoading : State()
        object HideLoading : State()
        data class Result(val list: List<Movie>?) : State()
    }
}
