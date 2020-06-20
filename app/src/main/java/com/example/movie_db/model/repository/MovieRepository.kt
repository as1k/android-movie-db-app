package com.example.movie_db.model.repository

import com.example.movie_db.model.data.movie.Movie
import com.google.gson.JsonObject

interface MovieRepository {
    // local
    fun insertMovieListLocal(movies: List<Movie>)
    fun insertMovieInfoLocal(movie: Movie)
    fun getMovieListLocal(): List<Movie>
    fun getMovieInfoByIdLocal(id: Int?): Movie
    fun checkIsLikedByIdLocal(id: Int?): Int
    fun getLikedMoviesLocal(liked: Boolean): List<Movie>
    fun setLikeStatusByIdLocal(liked: Boolean, id: Int?)
    fun getLikedMoviesIdLocal(liked: Boolean?): List<Int>

    // remote
    suspend fun getMovieListRemote(apiKey: String, page: Int) : List<Movie>?
    suspend fun getMovieRemote(movieId: Int?, apiKey: String): Movie?
    suspend fun getLikedMovieListRemote(accountId: Int?, apiKey: String, sessionId: String?): List<Movie>?
    suspend fun likeUnlikeMoviesCoroutineRemote(accountId: Int?, apiKey: String, sessionId: String?, body: JsonObject): JsonObject?
    suspend fun isLikedRemote(movieId: Int?, apiKey: String, sessionId: String?): JsonObject?
}