package com.example.movie_db.model.repository

import com.example.movie_db.model.data.movie.Movie
import com.example.movie_db.model.data.movie.MovieResponse
import com.google.gson.JsonObject

interface MovieRepository {
    fun insertMoviesDB(movies: List<Movie>)
    fun insertMovieInfoDB(movie: Movie)
    fun getMoviesDB(): List<Movie>
    fun getMovieInfoDB(id: Int?): Movie
    fun getLikedLocalDS(id: Int?): Int
    fun getAllLikedLocalDS(liked: Boolean): List<Movie>
    fun setLikeLocalDS(liked: Boolean, id: Int?)
    fun getIdOfflineLocalDS(liked: Boolean?): List<Int>
    fun getMovieOfflineLocalDS(liked: Boolean?): List<Movie>

//    fun updateMovieIsSaved(isSaved: Boolean, id: Int)

    suspend fun getMoviesCoroutine(apiKey: String, page: Int) : List<Movie>?
    suspend fun getMovieCoroutine(movieId: Int?, apiKey: String): Movie?
    suspend fun getSavedMoviesCoroutine(accountId: Int?, apiKey: String, sessionId: String?): List<Movie>?
    suspend fun addRemoveSavedCoroutine(accountId: Int?, apiKey: String, sessionId: String?, body: JsonObject): JsonObject?
    suspend fun isSavedCoroutine(movieId: Int?, apiKey: String, sessionId: String?): JsonObject?
}