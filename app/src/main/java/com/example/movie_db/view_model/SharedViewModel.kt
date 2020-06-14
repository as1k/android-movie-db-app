package com.example.movie_db.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.movie_db.model.data.movie.Movie

class SharedViewModel: ViewModel() {
    val savedMovies = MutableLiveData<Movie>()

    fun setMovie(movie:Movie){
        savedMovies.value = movie
    }
}