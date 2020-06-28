package com.example.movie_db.view_model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.movie_db.BuildConfig
import com.example.movie_db.model.data.movie.Movie
import com.example.movie_db.model.data.authentication.CurrentUser
import com.example.movie_db.model.data.movie.FavouriteResponse
import com.example.movie_db.model.network.MovieApiResponse
import com.example.movie_db.model.repository.MovieRepository
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.lang.Exception

class MovieListViewModel(private var movieRepository: MovieRepository) : ViewModel() {

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

    fun getPopularMovieList(page: Int = 1) {
        if (page == 1) liveData.value = State.ShowLoading
        disposable.add(
            movieRepository.getMovieListRemote(BuildConfig.MOVIE_DB_API_KEY, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    when (result) {
                        is MovieApiResponse.Success<List<Movie>> -> {
                            Log.d("my_debug", result.result.toString())
                            for (movie in result.result) {
                                isFavourite(movie)
                            }
                            movieRepository.insertMovieListLocal(result.result)
                            liveData.value = State.HideLoading
                            liveData.value = State.Result(result.result)
                        }
                        is MovieApiResponse.Error -> {
                            Log.d("my_debug", result.error)
                            movieRepository.getMovieListLocal()
                        }
                    }
                },
                { error ->
                    error.printStackTrace()
                    Log.d("my_debug", error.toString())
                })
        )
    }

    private fun isFavourite(movie: Movie) {
        disposable.add(
            movieRepository.isLikedRemote(
                movie.id,
                BuildConfig.MOVIE_DB_API_KEY,
                CurrentUser.user?.sessionId
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({ result ->
                    when (result) {
                        is MovieApiResponse.Success<JsonObject> -> {
                            Log.d("my_debug", result.result.toString())
                            val like = Gson().fromJson(
                                result.result,
                                FavouriteResponse::class.java
                            ).favorite
                            if (like) {
                                movieRepository.setLikeStatusByIdLocal(true, movie.id)
                                movie.liked = true
                            } else {
                                movieRepository.setLikeStatusByIdLocal(false, movie.id)
                                movie.liked = false
                            }
                        }
                        is MovieApiResponse.Error -> {
                            movieRepository.checkIsLikedByIdLocal(movie.id)
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

    fun getLikedMovieList() {
        liveData.value = State.ShowLoading
        disposable.add(
            movieRepository.getLikedMovieListRemote(
                CurrentUser.user?.userId,
                BuildConfig.MOVIE_DB_API_KEY,
                CurrentUser.user?.sessionId
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    when (result) {
                        is MovieApiResponse.Success<List<Movie>> -> {
                            Log.d("my_debug", result.result.toString())
                            for (favourite in result.result) {
                                favourite.liked = true
                                movieRepository.setLikeStatusByIdLocal(true, favourite.id)
                            }
                            liveData.value = State.HideLoading
                            liveData.value = State.Result(result.result)
                        }
                        is MovieApiResponse.Error -> {
                            Log.d("my_debug", result.error)
                            movieRepository.getLikedMoviesLocal(true)
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
            updateFavourite(body)
            movieRepository.setLikeStatusByIdLocal(movie.liked, movie.id)
            movieRepository.insertMovieInfoLocal(movie)
            isFavourite(movie)
        } catch (e: Exception) {
            Log.d("my_debug", e.toString())
        }
    }

    private fun updateFavourite(body: JsonObject) {
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

    sealed class State {
        object ShowLoading : State()
        object HideLoading : State()
        data class Result(val list: List<Movie>?) : State()
    }
}
