package com.example.movie_db.model.repository

import com.google.gson.JsonObject

interface UserRepository {

    suspend fun getTokenRemote(apiKey: String): JsonObject?

    suspend fun validateWithLoginRemote(apiKey: String, body: JsonObject): JsonObject?

    suspend fun getCurrentAccountRemote(apiKey: String, sessionId: String): JsonObject?

    suspend fun getSessionRemote(apiKey: String, body: JsonObject): JsonObject?

    suspend fun deleteSessionRemote(apiKey: String, body: JsonObject): JsonObject?
}