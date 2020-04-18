package com.example.movie_db

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

object RetrofitService {

    fun getPostApi(): PostApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(PostApi::class.java)
    }
}

interface PostApi {
    @GET("movie/top_rated")
    fun getTrendingMovieList(@Query("api_key")apiKey: String): Call<MovieResponse>
    @GET("movie/popular")
    fun getMovies(@Query("api_key") apiKey: String): Call<MovieResponse>
}
