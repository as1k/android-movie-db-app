package com.example.movie_db.view_model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.movie_db.BuildConfig
import com.example.movie_db.model.data.authentication.CurrentUser
import com.example.movie_db.model.data.movie.Movie
import com.example.movie_db.model.data.movie.FavouriteResponse
import com.example.movie_db.model.network.MovieApiResponse
import com.example.movie_db.model.repository.MovieRepository
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.lang.Exception

class MovieInfoViewModel(private val movieRepository: MovieRepository) : ViewModel() {

    val liveData = MutableLiveData<State>()
    private var disposable = CompositeDisposable()

//    private val job = Job()
//    override val coroutineContext: CoroutineContext
//        get() = Dispatchers.Main + job

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
//        job.cancel()
    }

    fun getMovie(movieId: Int?) {
        liveData.value = State.ShowLoading
        disposable.add(
            movieRepository.getMovieRemote(movieId, BuildConfig.MOVIE_DB_API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        when (result) {
                            is MovieApiResponse.Success<Movie> -> {
                                Log.d("my_debug", result.result.toString())
                                if (result.result.liked) {
                                    movieRepository.setLikeStatusByIdLocal(true, result.result.id)
                                }
                                liveData.value = State.HideLoading
                                liveData.value = State.Result(result.result)
                            }
                            is MovieApiResponse.Error -> {
                                Log.d("my_debug", result.error)
                                movieRepository.getMovieInfoByIdLocal(movieId)
                            }
                        }
                    },
                    { error ->
                        error.printStackTrace()
                        Log.d("my_debug", error.toString())
                    })
        )
    }

    fun addToFavourites(movie: Movie) {
        try {
            movie.liked = !movie.liked
            val body = JsonObject().apply {
                addProperty("media_type", "movie")
                addProperty("media_id", movie.id)
                addProperty("favorite", movie.liked)
            }
            updateFavourites(body)
            movieRepository.insertMovieInfoLocal(movie)
        } catch (e: Exception) {
            Log.d("my_debug", e.toString())
        }
    }

    private fun updateFavourites(body: JsonObject) {
        disposable.add(
            movieRepository.likeUnlikeMoviesRemote(
                CurrentUser.user?.userId,
                BuildConfig.MOVIE_DB_API_KEY,
                CurrentUser.user?.sessionId,
                body
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                { result ->
                    when (result) {
                        is MovieApiResponse.Success<JsonObject> -> {
                            Log.d("my_debug", result.result.toString())
                        }
                        is MovieApiResponse.Error -> {
                            Log.d("my_debug", result.error)
                        }
                    }
                },
                { error ->
                    error.printStackTrace()
                    Log.d("my_debug", error.toString())
                })
        )
    }

    fun isLikedMovie(movieId: Int?) {
        disposable.add(
            movieRepository.isLikedRemote(
                movieId,
                BuildConfig.MOVIE_DB_API_KEY,
                CurrentUser.user?.sessionId
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                { result ->
                    when (result) {
                        is MovieApiResponse.Success<JsonObject> -> {
                            Log.d("my_debug", result.result.toString())
                            val like = Gson().fromJson(
                                result.result,
                                FavouriteResponse::class.java
                            ).favorite
                            if (like) {
                                movieRepository.setLikeStatusByIdLocal(true, movieId)
                                liveData.value = State.IsFavorite(1)
                            } else {
                                movieRepository.setLikeStatusByIdLocal(false, movieId)
                                liveData.value = State.IsFavorite(0)
                            }
                        }
                        is MovieApiResponse.Error -> {
                            Log.d("my_debug", result.error)
                            movieRepository.checkIsLikedByIdLocal(movieId)
                        }
                    }
                },
                { error ->
                    error.printStackTrace()
                    Log.d("my_debug", error.toString())
                })
        )
    }

    sealed class State {
        object ShowLoading : State()
        object HideLoading : State()
        data class Result(val movie: Movie?) : State()
        data class IsFavorite(val likeInt: Int?) : State()
    }
}
