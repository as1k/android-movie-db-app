package com.example.movie_db.model.repository

import com.example.movie_db.model.network.MovieApiResponse
import com.google.gson.JsonObject
import io.reactivex.Single

interface UserRepository {

    fun getTokenRemote(
        apiKey: String
    ): Single<MovieApiResponse<JsonObject>>

    fun validateWithLoginRemote(
        apiKey: String, body: JsonObject
    ): Single<MovieApiResponse<JsonObject>>

    fun getCurrentAccountRemote(
        apiKey: String, sessionId: String
    ): Single<MovieApiResponse<JsonObject>>

    fun getSessionRemote(
        apiKey: String, body: JsonObject
    ): Single<MovieApiResponse<JsonObject>>

    fun deleteSessionRemote(
        apiKey: String, body: JsonObject
    ): Single<MovieApiResponse<JsonObject>>
}