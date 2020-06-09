package com.example.movie_db.view_model

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.movie_db.BuildConfig
import com.example.movie_db.model.data.authentication.LoginResponse
import com.example.movie_db.model.data.authentication.SessionResponse
import com.example.movie_db.model.data.authentication.TokenResponse
import com.example.movie_db.model.data.authentication.UserResponse
import com.example.movie_db.model.network.Retrofit
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class AuthViewModel(context: Context) : ViewModel(), CoroutineScope {
    private val job = Job()
    var liveData = MutableLiveData<State>()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    fun onLoggingIn(login: String, password: String) {
        launch {
            liveData.value = State.ShowLoading
            val response =
                Retrofit.getPostApi()
                    .getTokenCoroutine(BuildConfig.MOVIE_DB_API_KEY)
            if (response.isSuccessful) {
                val token = Gson().fromJson(response.body(), TokenResponse::class.java)
                if (token != null) {
                    val request = token.requestToken
                    val body = JsonObject().apply {
                        addProperty("username", login)
                        addProperty("password", password)
                        addProperty("request_token", request)
                    }
                    getLoginResponse(body)
                }
            } else {
                liveData.value = State.Result(false)
            }
        }
    }

    private fun getLoginResponse(body: JsonObject) {
        launch {
            val response = Retrofit.getPostApi()
                .loginCoroutine(BuildConfig.MOVIE_DB_API_KEY, body)
            if (response.isSuccessful) {
                val loginResponse = Gson().fromJson(response.body(), LoginResponse::class.java)
                if (loginResponse != null) {
                    val body = JsonObject().apply {
                        addProperty(
                            "request_token",
                            loginResponse.requestToken.toString()
                        )
                    }
                    getSession(body)
                }
            } else {
                liveData.value = State.Result(false)
            }
        }
    }

    private fun getSession(body: JsonObject) {
        launch {
            val response = Retrofit.getPostApi()
                .getSessionCoroutine(BuildConfig.MOVIE_DB_API_KEY, body)
            if (response.isSuccessful) {
                val session = Gson().fromJson(response.body(), SessionResponse::class.java)
                if (session != null) {
                    val sessionId = session.sessionId
                    getAccount(sessionId)
                }
            } else {
                liveData.value = State.Result(false)
            }
        }
    }

    fun getAccount(session: String) {
        launch {
            val response = Retrofit.getPostApi()
                .getCurrentAccountCoroutine(BuildConfig.MOVIE_DB_API_KEY, session)
            if (response.isSuccessful) {
                val account = Gson().fromJson(response.body(), UserResponse::class.java)
                if (account != null)
                    liveData.value = State.Account(account, session)
            } else {
                liveData.value = State.Result(false)
            }
            liveData.value = State.HideLoading
        }
    }

    sealed class State {
        object ShowLoading : State()
        object HideLoading : State()
        data class Result(val isSuccess: Boolean) : State()
        data class Account(val user: UserResponse, val session: String) : State()
    }

}
