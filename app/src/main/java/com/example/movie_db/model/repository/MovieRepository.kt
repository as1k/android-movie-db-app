package com.example.movie_db.model.repository

import com.example.movie_db.model.data.movie.Movie
import com.example.movie_db.model.data.movie.MovieResponse
import com.google.gson.JsonObject

interface MovieRepository {
    fun getMoviesDB(): List<Movie>
    fun getMovieInfoDB(id: Int): Movie
    fun getFavoriteDB(): List<Movie>
    fun insertMoviesDB(movies: List<Movie>)
    fun insertMovieInfoDB(movie: Movie)

    suspend fun getMoviesCoroutine(apiKey: String) : List<Movie>?
    suspend fun getMovieCoroutine(movieId: Int, apiKey: String): JsonObject?
    suspend fun getSavedMoviesCoroutine(accountId: Int, apiKey: String, sessionId: String): List<Movie>?
    suspend fun addRemoveSavedCoroutine(accountId: Int?, apiKey: String, sessionId: String?, body: JsonObject): JsonObject?
    suspend fun isSavedCoroutine(movieId: Int?, apiKey: String, sessionId: String?): JsonObject?
}