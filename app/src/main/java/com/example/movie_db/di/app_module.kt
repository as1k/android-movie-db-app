package com.example.movie_db.di

import android.content.Context
import com.example.movie_db.BuildConfig
import com.example.movie_db.model.database.MovieDao
import com.example.movie_db.model.database.MovieDatabase
import com.example.movie_db.model.network.MovieApi
import com.example.movie_db.model.repository.MovieRepository
import com.example.movie_db.model.repository.MovieRepositoryImpl
import com.example.movie_db.model.repository.UserRepository
import com.example.movie_db.model.repository.UserRepositoryImpl
import com.example.movie_db.view_model.AuthViewModel
import com.example.movie_db.view_model.MovieInfoViewModel
import com.example.movie_db.view_model.MovieListViewModel
import com.example.movie_db.view_model.ProfileViewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import org.koin.androidx.viewmodel.dsl.viewModel
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

val storageModule = module {
    single { getMovieDao(context = get()) }
}

val networkModule = module {
    single { createApiService() }
}

val repositoryModule = module {
    single<MovieRepository> { MovieRepositoryImpl(movieApi = get(), movieDao = get()) }
    single<UserRepository> { UserRepositoryImpl(movieApi = get()) }
}

val viewModelModule = module {
    viewModel { AuthViewModel(userRepository = get()) }
    viewModel { ProfileViewModel(userRepository = get()) }
    viewModel { MovieInfoViewModel(movieRepository = get()) }
    viewModel { MovieListViewModel(movieRepository = get()) }
}

val appModule = listOf(
    repositoryModule,
    storageModule,
    networkModule,
    viewModelModule
)

private fun getMovieDao(context: Context): MovieDao {
    return MovieDatabase.getDatabase(context).movieDao()
}

private fun createApiService(): MovieApi {
    return Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(MovieApi::class.java)
}