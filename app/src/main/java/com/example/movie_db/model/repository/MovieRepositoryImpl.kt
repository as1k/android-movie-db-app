package com.example.movie_db.model.repository

import android.util.Log
import com.example.movie_db.model.data.movie.Movie
import com.example.movie_db.model.database.MovieDao
import com.example.movie_db.model.network.MovieApi
import com.google.gson.JsonObject

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
    override suspend fun getMovieListRemote(apiKey: String, page: Int): List<Movie>? =
        movieApi.getMovieListCoroutine(apiKey, page).body()?.results

    override suspend fun getMovieRemote(movieId: Int?, apiKey: String): Movie? =
        movieApi.getMovieCoroutine(movieId, apiKey).body()

    override suspend fun getLikedMovieListRemote(
        accountId: Int?,
        apiKey: String,
        sessionId: String?
    ): List<Movie>? =
        movieApi.getLikedMovieListCoroutine(accountId, apiKey, sessionId).body()?.results

    override suspend fun likeUnlikeMoviesCoroutineRemote(accountId: Int?, apiKey: String, sessionId: String?, body: JsonObject): JsonObject? =
        movieApi.likeUnlikeMoviesCoroutine(accountId, apiKey, sessionId, body).body()

    override suspend fun isLikedRemote(movieId: Int?, apiKey: String, sessionId: String?): JsonObject? =
        movieApi.isLikedCoroutine(movieId, apiKey, sessionId).body()
}