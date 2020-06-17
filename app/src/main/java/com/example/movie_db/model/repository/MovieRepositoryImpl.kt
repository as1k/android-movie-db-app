package com.example.movie_db.model.repository

import com.example.movie_db.model.data.movie.Movie
import com.example.movie_db.model.data.movie.MovieResponse
import com.example.movie_db.model.database.MovieDao
import com.example.movie_db.model.network.Retrofit
import com.example.movie_db.model.repository.MovieRepository
import com.google.gson.JsonObject

class MovieRepositoryImpl(
    private val movieApi : Retrofit,
    private var movieDao: MovieDao
): MovieRepository {

    override fun getMoviesDB(): List<Movie> {
        return movieDao.getAll()
    }

    override fun getMovieInfoDB(id: Int?): Movie {
        return movieDao.getMovieInfo(id)
    }

    override fun insertMoviesDB(movies: List<Movie>) {
        return movieDao.insertAll(movies)
    }

    override fun insertMovieInfoDB(movie: Movie) {
        return movieDao.insertMovieInfo(movie)
    }

//    override fun updateMovieIsSaved(isSaved: Boolean, id: Int) {
//        return movieDao.updateMovieIsSaved(isSaved, id)
//    }

    override fun getLikedLocalDS(id: Int?): Int {
        return movieDao.getLiked(id)
    }

    override fun getAllLikedLocalDS(liked: Boolean): List<Movie> {
        return movieDao.getFavorite(liked)
    }

    override fun setLikeLocalDS(liked: Boolean, id: Int?) {
        return movieDao.setLike(liked, id)
    }

    override fun getIdOfflineLocalDS(liked: Boolean?): List<Int> {
        return movieDao.getIdOffline(liked)
    }

    override fun getMovieOfflineLocalDS(liked: Boolean?): List<Movie> {
        return movieDao.getMovieOffline(liked)
    }


    override suspend fun getMoviesCoroutine(apiKey: String, page: Int): List<Movie>? =
        movieApi.getPostApi().getMoviesCoroutine(apiKey, page).body()?.results

    override suspend fun getMovieCoroutine(movieId: Int?, apiKey: String): Movie? =
        movieApi.getPostApi().getMovieCoroutine(movieId, apiKey).body()

    override suspend fun getSavedMoviesCoroutine(
        accountId: Int?,
        apiKey: String,
        sessionId: String?
    ): List<Movie>? =
        movieApi.getPostApi().getSavedMoviesCoroutine(accountId, apiKey, sessionId).body()?.results

    override suspend fun addRemoveSavedCoroutine(accountId: Int?, apiKey: String, sessionId: String?, body: JsonObject): JsonObject? =
        movieApi.getPostApi().addRemoveSavedCoroutine(accountId, apiKey, sessionId, body).body()

    override suspend fun isSavedCoroutine(movieId: Int?, apiKey: String, sessionId: String?): JsonObject? =
        movieApi.getPostApi().isSavedCoroutine(movieId, apiKey, sessionId).body()
}