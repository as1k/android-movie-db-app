package com.example.movie_db.view

import android.content.Context
import android.content.SharedPreferences
import com.example.movie_db.BuildConfig
import com.example.movie_db.R
import com.example.movie_db.model.database.MovieDao
import com.example.movie_db.model.database.MovieDatabase
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.movie_db.model.network.PostApi
import com.example.movie_db.model.repository.MovieRepositoryImpl
import com.example.movie_db.model.repository.UserRepositoryImpl
import com.example.movie_db.view.containers.LoginContainer

class AppContainer(context: Context) {
    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(PostApi::class.java)

    private val movieDao: MovieDao = MovieDatabase.getDatabase(context).movieDao()

    private val movieRepository = MovieRepositoryImpl(retrofit, movieDao)
    private val userRepository = UserRepositoryImpl(retrofit)

    val moviesViewModelFactory = MoviesViewModelFactory(movieRepository, userRepository)
}