package com.example.movie_db.model.network

sealed class MovieApiResponse<T> {
    data class Success<T>(val result: T) : MovieApiResponse<T>()
    data class Error<T>(val error: String) : MovieApiResponse<T>()
}