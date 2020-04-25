package com.example.movie_db.classes

import com.google.gson.annotations.SerializedName

data class UserResponse (

    var sessionId: String? = null,

    @SerializedName("id")
    val userId: Int? = null,

    @SerializedName("username")
    val userName: String? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("include_adult")
    val adultContentAllowed: Boolean? = null
)
