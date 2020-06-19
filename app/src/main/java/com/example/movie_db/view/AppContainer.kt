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
import com.example.movie_db.model.repository.MovieRepository
import com.example.movie_db.model.repository.MovieRepositoryImpl
import com.example.movie_db.model.repository.UserRepository
import com.example.movie_db.model.repository.UserRepositoryImpl
import com.example.movie_db.view.containers.LoginContainer

class AppContainer private constructor(context: Context) {
    companion object {
        private lateinit var INSTANCE: AppContainer
        private var initialized = false

        fun init(context: Context) {
            INSTANCE = AppContainer(context)
            initialized = true
        }

        fun getMovieRepository() : MovieRepository = INSTANCE.movieRepository
        fun getUserRepository() : UserRepository = INSTANCE.userRepository
    }

    private val movieDao: MovieDao = MovieDatabase.getDatabase(context).movieDao()
    private val retrofit: PostApi by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PostApi::class.java)
    }

    private val movieRepository: MovieRepository by lazy {
        MovieRepositoryImpl(retrofit, movieDao)
    }

    private val userRepository: UserRepository by lazy {
        UserRepositoryImpl(retrofit)
    }
}