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

    override fun getMovieInfoDB(id: Int): Movie {
        return movieDao.getMovieInfo(id)
    }

    override fun getFavoriteDB(): List<Movie> {
        return movieDao.getFavorite();
    }

    override fun insertMoviesDB(movies: List<Movie>) {
        return movieDao.insertAll(movies)
    }

    override fun insertMovieInfoDB(movie: Movie) {
        return movieDao.insertMovieInfo(movie)
    }


    override suspend fun getMoviesCoroutine(apiKey: String, page: Int): List<Movie>? =
        movieApi.getPostApi().getMoviesCoroutine(apiKey, page).body()?.getResults()

    override suspend fun getMovieCoroutine(movieId: Int, apiKey: String): JsonObject? =
        movieApi.getPostApi().getMovieCoroutine(movieId, apiKey).body()

    override suspend fun getSavedMoviesCoroutine(
        accountId: Int,
        apiKey: String,
        sessionId: String
    ): List<Movie>? =
        movieApi.getPostApi().getSavedMoviesCoroutine(accountId, apiKey, sessionId).body()?.getResults()

    override suspend fun addRemoveSavedCoroutine(accountId: Int?, apiKey: String, sessionId: String?, body: JsonObject): JsonObject? =
        movieApi.getPostApi().addRemoveSavedCoroutine(accountId, apiKey, sessionId, body).body()

    override suspend fun isSavedCoroutine(movieId: Int?, apiKey: String, sessionId: String?): JsonObject? =
        movieApi.getPostApi().isSavedCoroutine(movieId, apiKey, sessionId).body()
}