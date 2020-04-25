package com.example.movie_db.classes

import com.google.gson.annotations.SerializedName

data class SessionResponse (
    @SerializedName("success")
    val isSuccess: Boolean? = null,

    @SerializedName("session_id")
    val sessionId: String? = null
)
