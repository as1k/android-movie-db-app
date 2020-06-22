package com.example.movie_db.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.movie_db.model.data.movie.Movie

@Database(entities = [Movie::class], version = 1)
abstract class MovieDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao

    companion object {
        var database: MovieDatabase? = null
        private const val databaseName: String = "movie_database3.db"
        fun getDatabase(context: Context): MovieDatabase {
            if (database == null) {
                database = Room.databaseBuilder(
                    context.applicationContext,
                    MovieDatabase::class.java,
                    databaseName
                ).allowMainThreadQueries()
                    .build()
            }
            return database!!
        }
    }
}
