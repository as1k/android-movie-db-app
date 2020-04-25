package com.example.movie_db

import com.example.movie_db.classes.MovieResponse
import com.google.gson.JsonObject

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

object Retrofit {
    fun getPostApi(): PostApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(PostApi::class.java)
    }
}

interface PostApi {
    @GET("authentication/token/new")
    fun getToken(
        @Query("api_key") apiKey: String
    ): Call<JsonObject>

    @POST("authentication/token/validate_with_login")
    fun login(
        @Query("api_key") apiKey: String,
        @Body body: JsonObject
    ): Call<JsonObject>

    @GET("account")
    fun getCurrentAccount(
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String
    ): Call<JsonObject>

    @GET("movie/popular")
    fun getMovies(@Query("api_key") apiKey: String): Call<MovieResponse>

    @GET("movie/{movie_id}")
    fun getMovie(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String
    ): Call<JsonObject>

    @GET("account/{account_id}/favorite/movies")
    fun getSavedMovies(
        @Path("account_id") id: Int,
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String
    ): Call<MovieResponse>

    @POST("account/{account_id}/favorite")
    fun addRemoveSaved(
        @Path("account_id") accountId: Int?,
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String?,
        @Body body: JsonObject
    ): Call<JsonObject>

    @GET("movie/{movie_id}/account_states")
    fun isSaved(
        @Path("movie_id") movieId: Int?,
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String?
    ): Call<JsonObject>

    @POST("authentication/session/new")
    fun getSession(
        @Query("api_key") apiKey: String,
        @Body body: JsonObject
    ): Call<JsonObject>

    @HTTP(
        method = "DELETE",
        path = "authentication/session",
        hasBody = true
    )

    fun deleteSession(
        @Query("api_key") apiKey: String,
        @Body body: JsonObject
    ): Call<JsonObject>
}
