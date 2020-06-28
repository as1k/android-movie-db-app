package com.example.movie_db.model.data.movie

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "movie_table")
data class Movie(
    @PrimaryKey
    @SerializedName("id")
    var id: Int = 0,

    @SerializedName("title")
    var title: String = "",

    @SerializedName("overview")
    var overview: String = "",

    @SerializedName("release_date")
    var releaseDate: String = "",

    @SerializedName("popularity")
    var popularity: Double = 0.0,

    @SerializedName("vote_average")
    var voteAverage: Double = 0.0,

    @SerializedName("include_adult")
    var includeAdult: Boolean = false,

    @SerializedName("backdrop_path")
    var backdropPath: String = "",

    @SerializedName("poster_path")
    var posterPath: String = "",

    @SerializedName("liked")
    var liked: Boolean = false,

    @Ignore var position: Int = 0
) : Serializable