package com.example.movie_db.model.data.movie

import com.google.gson.annotations.SerializedName

data class SelectedMovie(
    @SerializedName("media_type") val mediaType: String = "movie",
    @SerializedName("media_id") val movieId: Int,
    @SerializedName("favorite") var isSaved: Boolean
)