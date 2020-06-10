package com.example.movie_db.model.repository

import com.example.movie_db.model.data.authentication.UserResponse
import com.example.movie_db.model.data.movie.MovieResponse
import com.google.gson.JsonObject
import retrofit2.Response

interface UserRepository {

    suspend fun getTokenCoroutine(apiKey: String): JsonObject?

    suspend fun loginCoroutine(apiKey: String,body: JsonObject): JsonObject?

    suspend fun getCurrentAccountCoroutine (apiKey: String, sessionId: String) : JsonObject?

    suspend fun getSessionCoroutine (apiKey: String, body: JsonObject) : JsonObject?
}