package com.example.movie_db.model.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.movie_db.model.data.movie.Movie

@Dao
interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: List<Movie>)

    @Query("SELECT * FROM movie_table")
    fun getAll(): List<Movie>

    @Query("SELECT * FROM movie_table WHERE isSaved=1")
    fun getFavorite(): List<Movie>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMovieInfo(movie: Movie)

    @Query("SELECT * FROM movie_table WHERE id = :id")
    fun getMovieInfo(id: Int): Movie
}
