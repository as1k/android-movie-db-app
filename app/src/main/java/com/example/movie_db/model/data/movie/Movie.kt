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
    var review: String = "",

    @SerializedName("release_date")
    var releaseDate: String = "",

    @SerializedName("popularity")
    var popularity: Double = 0.0,

    @SerializedName("vote_average")
    var voteRating: Double = 0.0,

    @SerializedName("adultContent")
    var adultContent: Boolean = false,

    @SerializedName("backdrop_path")
    var pathToBackground: String = "",

    @SerializedName("poster_path")
    var pathToPoster: String = "",

    var liked: Boolean = false,

    @Ignore var position: Int = 0
) : Serializable