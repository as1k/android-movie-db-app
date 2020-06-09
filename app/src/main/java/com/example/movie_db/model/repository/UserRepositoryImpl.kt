package com.example.movie_db.model.repository

import com.example.movie_db.model.network.Retrofit
import com.google.gson.JsonObject

class UserRepositoryImpl(private val movieApi : Retrofit): UserRepository {

    override suspend fun getTokenCoroutine(apiKey: String): JsonObject? =
        movieApi.getPostApi().getTokenCoroutine(apiKey).await().body()

    override suspend fun loginCoroutine(apiKey: String, body: JsonObject): JsonObject? =
        movieApi.getPostApi().loginCoroutine(apiKey, body).await().body()

    override suspend fun getCurrentAccountCoroutine(
        apiKey: String,
        sessionId: String
    ): JsonObject? =
        movieApi.getPostApi().getCurrentAccountCoroutine(apiKey, sessionId).await().body()

    override suspend fun getSessionCoroutine(apiKey: String, body: JsonObject): JsonObject? =
        movieApi.getPostApi().getSessionCoroutine(apiKey, body).await().body()
}