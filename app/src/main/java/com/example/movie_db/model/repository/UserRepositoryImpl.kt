package com.example.movie_db.model.repository

import com.example.movie_db.model.network.MovieApi
import com.example.movie_db.model.network.MovieApiResponse
import com.google.gson.JsonObject
import io.reactivex.Single

class UserRepositoryImpl(private val movieApi : MovieApi): UserRepository {

    override fun getTokenRemote(apiKey: String): Single<MovieApiResponse<JsonObject>> =
        movieApi.getToken(apiKey)
            .map {response ->
                if(response.isSuccessful) {
                    val jsonObject = response.body()!!
                    MovieApiResponse.Success(jsonObject)
                } else {
                    MovieApiResponse.Error<JsonObject>("Get token response error")
                }
            }

    override fun validateWithLoginRemote(apiKey: String, body: JsonObject): Single<MovieApiResponse<JsonObject>> =
        movieApi.validateWithLogin(apiKey, body)
            .map {response ->
                if(response.isSuccessful) {
                    val jsonObject = response.body()!!
                    MovieApiResponse.Success(jsonObject)
                } else {
                    MovieApiResponse.Error<JsonObject>("Validate login response error")
                }
            }

    override fun getCurrentAccountRemote(
        apiKey: String,
        sessionId: String
    ): Single<MovieApiResponse<JsonObject>> =
        movieApi.getCurrentAccount(apiKey, sessionId)
            .map {response ->
                if(response.isSuccessful) {
                    val jsonObject = response.body()!!
                    MovieApiResponse.Success(jsonObject)
                } else {
                    MovieApiResponse.Error<JsonObject>("Get current account response error")
                }
            }

    override fun getSessionRemote(apiKey: String, body: JsonObject): Single<MovieApiResponse<JsonObject>> =
        movieApi.getSession(apiKey, body)
            .map {response ->
                if(response.isSuccessful) {
                    val jsonObject = response.body()!!
                    MovieApiResponse.Success(jsonObject)
                } else {
                    MovieApiResponse.Error<JsonObject>("Get session response error")
                }
            }

    override fun deleteSessionRemote(apiKey: String, body: JsonObject): Single<MovieApiResponse<JsonObject>> =
        movieApi.deleteSession(apiKey, body)
            .map {response ->
                if(response.isSuccessful) {
                    val jsonObject = response.body()!!
                    MovieApiResponse.Success(jsonObject)
                } else {
                    MovieApiResponse.Error<JsonObject>("Delete session response error")
                }
            }
}