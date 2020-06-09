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
import com.example.movie_db.model.repository.UserRepositoryImpl

class AuthViewModel(context: Context) : ViewModel(), CoroutineScope {

    private var userRepository = UserRepositoryImpl(Retrofit)

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
            val token = userRepository
                    .getTokenCoroutine(BuildConfig.MOVIE_DB_API_KEY)
            try {
                val token = Gson().fromJson(token, TokenResponse::class.java)
                if (token != null) {
                    val request = token.requestToken
                    val body = JsonObject().apply {
                        addProperty("username", login)
                        addProperty("password", password)
                        addProperty("request_token", request)
                    }
                    getLoginResponse(body)
                }
            } catch (e: Exception) {
                liveData.value = State.Result(false)
            }
        }
    }

    private fun getLoginResponse(body: JsonObject) {
        launch {
            try {
                val response = userRepository
                    .loginCoroutine(BuildConfig.MOVIE_DB_API_KEY, body)

                val loginResponse = Gson().fromJson(response, LoginResponse::class.java)
                if (loginResponse != null) {
                    val body = JsonObject().apply {
                        addProperty(
                            "request_token",
                            loginResponse.requestToken.toString()
                        )
                    }
                    getSession(body)
                }
            } catch (e: Exception) {
                liveData.value = State.Result(false)
            }
        }
    }

    private fun getSession(body: JsonObject) {
        launch {
            try {
                val response = userRepository
                    .getSessionCoroutine(BuildConfig.MOVIE_DB_API_KEY, body)
                val session = Gson().fromJson(response, SessionResponse::class.java)
                if (session != null) {
                    val sessionId = session.sessionId
                    getAccount(sessionId)
                }
            } catch (e: Exception) {
                liveData.value = State.Result(false)
            }
        }
    }

    fun getAccount(session: String) {
        launch {
            try {
                val response = userRepository
                    .getCurrentAccountCoroutine(BuildConfig.MOVIE_DB_API_KEY, session)
                val account = Gson().fromJson(response, UserResponse::class.java)
                if (account != null)
                    liveData.value = State.Account(account, session)
            } catch (e: Exception) {
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
