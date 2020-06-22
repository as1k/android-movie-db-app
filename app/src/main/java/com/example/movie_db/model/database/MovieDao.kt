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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMovieInfo(movie: Movie)

    @Query("SELECT * FROM movie_table")
    fun getAll(): List<Movie>

    @Query("SELECT * FROM movie_table WHERE id = :id")
    fun getMovieInfoById(id: Int?): Movie

    // favorite movies
    @Query("SELECT * FROM movie_table WHERE liked = :liked")
    fun getLikedMovies(liked: Boolean): List<Movie>

    @Query("update movie_table set liked = :likeCnt where id = :id")
    fun setLikeStatusById(likeCnt: Boolean, id: Int?)

    @Query("SELECT liked FROM movie_table where id = :id")
    fun checkIsLikedById(id: Int?): Int

    @Query("SELECT id FROM movie_table where liked = :liked")
    fun getLikedMoviesId(liked: Boolean?): List<Int>
}
