package com.example.movie_db.model.repository

import com.example.movie_db.model.data.movie.Movie
import com.google.gson.JsonObject

interface MovieRepository {
    // local
    fun insertMoviesDB(movies: List<Movie>)
    fun insertMovieInfoDB(movie: Movie)
    fun getMoviesDB(): List<Movie>
    fun getMovieInfoDB(id: Int?): Movie
    fun getLikedDB(id: Int?): Int
    fun getAllLikedDB(liked: Boolean): List<Movie>
    fun setLikeDB(liked: Boolean, id: Int?)
    fun getLikedMovieIdDB(liked: Boolean?): List<Int>

    // remote
    suspend fun getMoviesCoroutine(apiKey: String, page: Int) : List<Movie>?
    suspend fun getMovieCoroutine(movieId: Int?, apiKey: String): Movie?
    suspend fun getSavedMoviesCoroutine(accountId: Int?, apiKey: String, sessionId: String?): List<Movie>?
    suspend fun addRemoveSavedCoroutine(accountId: Int?, apiKey: String, sessionId: String?, body: JsonObject): JsonObject?
    suspend fun isSavedCoroutine(movieId: Int?, apiKey: String, sessionId: String?): JsonObject?
}