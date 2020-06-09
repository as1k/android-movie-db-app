package com.example.movie_db.model.network

import com.example.movie_db.BuildConfig
import com.example.movie_db.model.data.movie.MovieResponse
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import kotlinx.coroutines.Deferred

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
    //user
    @GET("authentication/token/new")
    suspend fun getTokenCoroutine(
        @Query("api_key") apiKey: String
    ): Deferred<Response<JsonObject>>

    @POST("authentication/token/validate_with_login")
    suspend fun loginCoroutine(
        @Query("api_key") apiKey: String,
        @Body body: JsonObject
    ): Deferred<Response<JsonObject>>

    @GET("account")
    suspend fun getCurrentAccountCoroutine(
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String
    ): Deferred<Response<JsonObject>>

    // movie
    @GET("movie/popular")
    suspend fun getMoviesCoroutine(
        @Query("api_key") apiKey: String
    ): Deferred<Response<MovieResponse>>

    @GET("movie/{movie_id}")
    suspend fun getMovieCoroutine(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String
    ): Deferred<Response<JsonObject>>

    @GET("account/{account_id}/favorite/movies")
    suspend fun getSavedMoviesCoroutine(
        @Path("account_id") id: Int,
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String
    ): Deferred<Response<MovieResponse>>

    @POST("account/{account_id}/favorite")
    suspend fun addRemoveSavedCoroutine(
        @Path("account_id") accountId: Int?,
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String?,
        @Body body: JsonObject
    ): Deferred<Response<JsonObject>>

    @GET("movie/{movie_id}/account_states")
    suspend fun isSavedCoroutine(
        @Path("movie_id") movieId: Int?,
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String?
    ): Deferred<Response<JsonObject>>

    @POST("authentication/session/new")
    suspend fun getSessionCoroutine(
        @Query("api_key") apiKey: String,
        @Body body: JsonObject
    ): Deferred<Response<JsonObject>>

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
