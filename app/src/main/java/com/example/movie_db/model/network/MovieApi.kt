package com.example.movie_db.model.network

import com.example.movie_db.model.data.movie.Movie
import com.example.movie_db.model.data.movie.MovieResponse
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.*

interface MovieApi {
    // movie
    @GET("movie/popular")
    suspend fun getMovieListCoroutine(
        @Query("api_key") apiKey: String,
        @Query("page") page:Int
    ): Response<MovieResponse>

    @GET("movie/{movie_id}")
    suspend fun getMovieCoroutine(
        @Path("movie_id") movieId: Int?,
        @Query("api_key") apiKey: String
    ): Response<Movie>

    @GET("account/{account_id}/favorite/movies")
    suspend fun getLikedMovieListCoroutine(
        @Path("account_id") id: Int?,
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String?
    ): Response<MovieResponse>

    @POST("account/{account_id}/favorite")
    suspend fun likeUnlikeMoviesCoroutine(
        @Path("account_id") accountId: Int?,
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String?,
        @Body body: JsonObject
    ): Response<JsonObject>

    @GET("movie/{movie_id}/account_states")
    suspend fun isLikedCoroutine(
        @Path("movie_id") movieId: Int?,
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String?
    ): Response<JsonObject>

    //user
    @GET("authentication/token/new")
    suspend fun getTokenCoroutine(
        @Query("api_key") apiKey: String
    ): Response<JsonObject>

    @POST("authentication/token/validate_with_login")
    suspend fun validateWithLoginCoroutine(
        @Query("api_key") apiKey: String,
        @Body body: JsonObject
    ): Response<JsonObject>

    @GET("account")
    suspend fun getCurrentAccountCoroutine(
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String
    ): Response<JsonObject>

    @POST("authentication/session/new")
    suspend fun getSessionCoroutine(
        @Query("api_key") apiKey: String,
        @Body body: JsonObject
    ): Response<JsonObject>

    @HTTP(
        method = "DELETE",
        path = "authentication/session",
        hasBody = true
    )
    suspend fun deleteSessionCoroutine(
        @Query("api_key") apiKey: String,
        @Body body: JsonObject
    ): Response<JsonObject>
}
