package com.example.movie_db.model.data.authentication

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
    val adultContentAllowed: Boolean? = null,


    @SerializedName("avatar")
    val avatar: Avatar
)
data class Avatar(
    @SerializedName("gravatar")
    val gravatar: Gravatar
)
data class Gravatar(
    @SerializedName("hash")
    val hash: String
)