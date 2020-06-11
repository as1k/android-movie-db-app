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
    var isSaved: Boolean = false,

    @SerializedName("title")
    var title: String? = null,

    @SerializedName("overview")
    var review: String? = null,

    @SerializedName("release_date")
    var releaseDate: String? = null,

    @SerializedName("popularity")
    var popularity: Double? = null,

    @SerializedName("vote_average")
    var voteRating: Double? = null,

    @SerializedName("adultContent")
    var adultContent: Boolean = false,

    @SerializedName("backdrop_path")
    private var pathToBackground: String? = null,

    @SerializedName("poster_path")
    private var pathToPoster: String? = null

) : Serializable {

    fun getPathToPoster(): String {
        return "https://image.tmdb.org/t/p/w500$pathToPoster"
    }

    fun getPathToBackground(): String {
        return "https://image.tmdb.org/t/p/w500$pathToBackground"
    }
}
