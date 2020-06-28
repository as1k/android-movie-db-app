package com.example.movie_db.model.repository

import com.example.movie_db.model.data.movie.Movie
import com.example.movie_db.model.network.MovieApiResponse
import com.google.gson.JsonObject
import io.reactivex.Single

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
    fun getMovieListRemote(
        apiKey: String, page: Int
    ) : Single<MovieApiResponse<List<Movie>>>
    fun getMovieRemote(
        movieId: Int?, apiKey: String
    ): Single<MovieApiResponse<Movie>>
    fun getLikedMovieListRemote(
        accountId: Int?, apiKey: String, sessionId: String?
    ): Single<MovieApiResponse<List<Movie>>>
    fun likeUnlikeMoviesRemote(
        accountId: Int?, apiKey: String, sessionId: String?, body: JsonObject
    ): Single<MovieApiResponse<JsonObject>>
    fun isLikedRemote(
        movieId: Int?, apiKey: String, sessionId: String?
    ): Single<MovieApiResponse<JsonObject>>
}