package com.example.movie_db.model.repository

import com.example.movie_db.model.network.MovieApi
import com.google.gson.JsonObject

class UserRepositoryImpl(private val movieApi : MovieApi): UserRepository {

    override suspend fun getTokenRemote(apiKey: String): JsonObject? =
        movieApi.getTokenCoroutine(apiKey).body()

    override suspend fun validateWithLoginRemote(apiKey: String, body: JsonObject): JsonObject? =
        movieApi.validateWithLoginCoroutine(apiKey, body).body()

    override suspend fun getCurrentAccountRemote(
        apiKey: String,
        sessionId: String
    ): JsonObject? =
        movieApi.getCurrentAccountCoroutine(apiKey, sessionId).body()

    override suspend fun getSessionRemote(apiKey: String, body: JsonObject): JsonObject? =
        movieApi.getSessionCoroutine(apiKey, body).body()

    override suspend fun deleteSessionRemote(apiKey: String, body: JsonObject): JsonObject? =
        movieApi.deleteSessionCoroutine(apiKey, body).body()
}