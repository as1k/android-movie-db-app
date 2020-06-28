package com.example.movie_db.model.network

import com.example.movie_db.model.data.movie.Movie
import com.example.movie_db.model.data.movie.MovieResponse
import com.google.gson.JsonObject
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.*

interface MovieApi {
    // movie
    @GET("movie/popular")
    fun getMovieList(
        @Query("api_key") apiKey: String,
        @Query("page") page:Int
    ): Single<Response<MovieResponse>>

    @GET("movie/{movie_id}")
    fun getMovie(
        @Path("movie_id") movieId: Int?,
        @Query("api_key") apiKey: String
    ): Single<Response<Movie>>

    @GET("account/{account_id}/favorite/movies")
    fun getLikedMovieList(
        @Path("account_id") id: Int?,
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String?
    ): Single<Response<MovieResponse>>

    @POST("account/{account_id}/favorite")
    fun likeUnlikeMovies(
        @Path("account_id") accountId: Int?,
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String?,
        @Body body: JsonObject
    ): Single<Response<JsonObject>>

    @GET("movie/{movie_id}/account_states")
    fun isLiked(
        @Path("movie_id") movieId: Int?,
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String?
    ): Single<Response<JsonObject>>

    //user
    @GET("authentication/token/new")
    fun getToken(
        @Query("api_key") apiKey: String
    ): Single<Response<JsonObject>>

    @POST("authentication/token/validate_with_login")
    fun validateWithLogin(
        @Query("api_key") apiKey: String,
        @Body body: JsonObject
    ): Single<Response<JsonObject>>

    @GET("account")
    fun getCurrentAccount(
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String
    ): Single<Response<JsonObject>>

    @POST("authentication/session/new")
    fun getSession(
        @Query("api_key") apiKey: String,
        @Body body: JsonObject
    ): Single<Response<JsonObject>>

    @HTTP(
        method = "DELETE",
        path = "authentication/session",
        hasBody = true
    )
    fun deleteSession(
        @Query("api_key") apiKey: String,
        @Body body: JsonObject
    ): Single<Response<JsonObject>>
}
