package com.example.movie_db.model.repository

import android.util.Log
import com.example.movie_db.model.data.movie.Movie
import com.example.movie_db.model.database.MovieDao
import com.example.movie_db.model.network.MovieApi
import com.example.movie_db.model.network.MovieApiResponse
import com.google.gson.JsonObject
import io.reactivex.Single

class MovieRepositoryImpl(
    private val movieApi : MovieApi,
    private var movieDao: MovieDao
): MovieRepository {

    // local
    override fun getMovieListLocal(): List<Movie> {
        return movieDao.getAll()
    }

    override fun getMovieInfoByIdLocal(id: Int?): Movie {
        return movieDao.getMovieInfoById(id)
    }

    override fun insertMovieListLocal(movies: List<Movie>) {
        return movieDao.insertAll(movies)
    }

    override fun insertMovieInfoLocal(movie: Movie) {
        return movieDao.insertMovieInfo(movie)
    }

    //favorite movies
    override fun checkIsLikedByIdLocal(id: Int?): Int {
        return movieDao.checkIsLikedById(id)
    }

    override fun getLikedMoviesLocal(liked: Boolean): List<Movie> {
        return movieDao.getLikedMovies(liked)
    }

    override fun setLikeStatusByIdLocal(liked: Boolean, id: Int?) {
        Log.d("my_debug", "movierepository impl setLikeStatusByIdLocal occured")
        return movieDao.setLikeStatusById(liked, id)
    }

    override fun getLikedMoviesIdLocal(liked: Boolean?): List<Int> {
        return movieDao.getLikedMoviesId(liked)
    }

    // remote
    override fun getMovieListRemote(
        apiKey: String,
        page: Int
    ): Single<MovieApiResponse<List<Movie>>> =
        movieApi.getMovieList(apiKey, page)
            .map { response ->
                if (response.isSuccessful) {
                    val movieList = response.body()?.results ?: emptyList()
                    MovieApiResponse.Success(movieList)
                } else {
                    MovieApiResponse.Error<List<Movie>>("Get movie list response error")
                }
            }

    override fun getMovieRemote(movieId: Int?, apiKey: String): Single<MovieApiResponse<Movie>> =
        movieApi.getMovie(movieId, apiKey)
            .map { response ->
                if (response.isSuccessful) {
                    val movie = response.body()!!
                    MovieApiResponse.Success(movie)
                } else {
                    MovieApiResponse.Error<Movie>("Get Movie response Error")
                }
            }

    override fun getLikedMovieListRemote(
        accountId: Int?, apiKey: String, sessionId: String?
    ): Single<MovieApiResponse<List<Movie>>> =
        movieApi.getLikedMovieList(accountId, apiKey, sessionId)
            .map { response ->
                if(response.isSuccessful) {
                    val likedMovieList = response.body()?.results ?: emptyList()
                    MovieApiResponse.Success(likedMovieList)
                } else {
                    MovieApiResponse.Error<List<Movie>>("Get liked movie list response error")
                }
            }

    override fun likeUnlikeMoviesRemote(
        accountId: Int?, apiKey: String, sessionId: String?, body: JsonObject
    ): Single<MovieApiResponse<JsonObject>> =
        movieApi.likeUnlikeMovies(accountId, apiKey, sessionId, body)
            .map { response ->
                if(response.isSuccessful) {
                    val jsonObject = response.body()!!
                    MovieApiResponse.Success(jsonObject)
                } else {
                    MovieApiResponse.Error<JsonObject>("Like Unlike Movies response error")
                }
            }

    override fun isLikedRemote(
        movieId: Int?, apiKey: String, sessionId: String?
    ): Single<MovieApiResponse<JsonObject>> =
        movieApi.isLiked(movieId, apiKey, sessionId)
            .map { response ->
                if(response.isSuccessful) {
                    val jsonObject = response.body()!!
                    MovieApiResponse.Success(jsonObject)
                } else {
                    MovieApiResponse.Error<JsonObject>("Is liked response error")
                }
            }
}