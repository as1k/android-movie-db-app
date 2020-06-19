package com.example.movie_db.model.repository

import com.google.gson.JsonObject
import retrofit2.http.Body

interface UserRepository {

    suspend fun getTokenCoroutine(apiKey: String): JsonObject?

    suspend fun loginCoroutine(apiKey: String,body: JsonObject): JsonObject?

    suspend fun getCurrentAccountCoroutine(apiKey: String, sessionId: String): JsonObject?

    suspend fun getSessionCoroutine(apiKey: String, body: JsonObject): JsonObject?

    suspend fun deleteSessionCoroutine(apiKey: String, body: JsonObject): JsonObject?
}