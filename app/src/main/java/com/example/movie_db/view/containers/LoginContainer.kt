package com.example.movie_db.view.containers

import com.example.movie_db.model.data.authentication.User
import com.example.movie_db.model.repository.MovieRepository
import com.example.movie_db.model.repository.UserRepository
import com.example.movie_db.view.MoviesViewModelFactory

class LoginContainer(val movieRepository: MovieRepository, val userRepository: UserRepository) {

    val loginData = User()

    val loginViewModelFactory = MoviesViewModelFactory(movieRepository, userRepository)
}