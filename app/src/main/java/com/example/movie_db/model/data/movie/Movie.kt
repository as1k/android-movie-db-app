package com.example.movie_db.model.data.movie

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "movie_table")
data class Movie(
    @PrimaryKey
    @SerializedName("id")
    var id: Int,

    @SerializedName("isSaved")
    var isSaved: Boolean,

    @SerializedName("title")
    var title: String?,

    @SerializedName("overview")
    var review: String?,

    @SerializedName("release_date")
    var releaseDate: String?,

    @SerializedName("popularity")
    var popularity: Double,

    @SerializedName("vote_average")
    var voteRating: Double,

    @SerializedName("adultContent")
    var adultContent: Boolean,

    @SerializedName("backdrop_path")
    private var pathToBackground: String,

    @SerializedName("poster_path")
    private var pathToPoster: String

) : Serializable {

    fun getPathToPoster(): String {
        return "https://image.tmdb.org/t/p/w500$pathToPoster"
    }

    fun getPathToBackground(): String {
        return "https://image.tmdb.org/t/p/w500$pathToBackground"
    }
}
