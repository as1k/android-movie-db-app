package com.example.movie_db.model.data.authentication

import com.google.gson.annotations.SerializedName

data class LoginResponse (
    @SerializedName("success")
    val isSuccessful: Boolean? = null,

    @SerializedName("request_token")
    val requestToken: String? = null,

    @SerializedName("expires_at")
    val expiresAt: String? = null
)
