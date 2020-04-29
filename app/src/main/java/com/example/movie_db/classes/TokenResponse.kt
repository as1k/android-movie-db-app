package com.example.movie_db.classes

import com.google.gson.annotations.SerializedName

data class TokenResponse (
    @SerializedName("request_token")
    val requestToken: String? = null,

    @SerializedName("success")
    val isSuccess: Boolean? = null,

    @SerializedName("expires_at")
    val expiresAt: String? = null
)
