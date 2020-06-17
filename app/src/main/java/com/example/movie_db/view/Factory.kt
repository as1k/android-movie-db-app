package com.example.movie_db.view

import com.example.movie_db.model.repository.MovieRepository
import com.example.movie_db.model.repository.UserRepository
import com.example.movie_db.view_model.AuthViewModel
import com.example.movie_db.view_model.MovieInfoViewModel
import com.example.movie_db.view_model.MoviesViewModel

interface Factory {
    fun createMovieList(): MoviesViewModel
    fun createMovie(): MovieInfoViewModel
    fun createUser(): AuthViewModel
}

class MoviesViewModelFactory(
    private val movieRepository: MovieRepository,
    private val userRepository: UserRepository
) : Factory {

    override fun createMovieList(): MoviesViewModel {
        return MoviesViewModel(movieRepository)
    }

    override fun createMovie(): MovieInfoViewModel {
        return MovieInfoViewModel(movieRepository)
    }

    override fun createUser(): AuthViewModel {
        return AuthViewModel(userRepository)
    }
}