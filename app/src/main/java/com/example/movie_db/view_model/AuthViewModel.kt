package com.example.movie_db.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.movie_db.BuildConfig
import com.example.movie_db.model.data.authentication.LoginResponse
import com.example.movie_db.model.data.authentication.SessionResponse
import com.example.movie_db.model.data.authentication.UserResponse
import com.example.movie_db.model.repository.UserRepository
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class AuthViewModel(private var userRepository: UserRepository? = null) : ViewModel(), CoroutineScope {

    private val job = Job()
    var liveData = MutableLiveData<State>()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    fun getToken(login: String, password: String) {
        launch {
            liveData.value = State.ShowLoading
            try {
                val token = userRepository?.getTokenRemote(BuildConfig.MOVIE_DB_API_KEY)
                val newToken = Gson().fromJson(token, LoginResponse::class.java)
                if (token != null) {
                    val request = newToken.requestToken
                    val body = JsonObject().apply {
                        addProperty("username", login)
                        addProperty("password", password)
                        addProperty("request_token", request)
                    }
                    validateWithLogin(body)
                }
            } catch (e: Exception) {
                liveData.value = State.Result(false)
            }
        }
    }

    private fun validateWithLogin(body: JsonObject) {
        launch {
            try {
                val response = userRepository
                    ?.validateWithLoginRemote(BuildConfig.MOVIE_DB_API_KEY, body)

                val loginResponse = Gson().fromJson(response, LoginResponse::class.java)
                if (loginResponse != null) {
                    val newBody = JsonObject().apply {
                        addProperty(
                            "request_token", loginResponse.requestToken.toString()
                        )
                    }
                    getSession(newBody)
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
                    ?.getSessionRemote(BuildConfig.MOVIE_DB_API_KEY, body)
                val session = Gson().fromJson(response, SessionResponse::class.java)
                if (session != null) {
                    val sessionId = session.sessionId
                    getCurrentAccount(sessionId)
                }
            } catch (e: Exception) {
                liveData.value = State.Result(false)
            }
        }
    }

    fun getCurrentAccount(session: String) {
        launch {
            try {
                val response = userRepository
                    ?.getCurrentAccountRemote(BuildConfig.MOVIE_DB_API_KEY, session)
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
        data class Result(val isSuccessful: Boolean) : State()
        data class Account(val user: UserResponse, val session: String) : State()
    }
}
