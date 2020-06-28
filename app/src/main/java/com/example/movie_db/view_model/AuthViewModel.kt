package com.example.movie_db.view_model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.movie_db.BuildConfig
import com.example.movie_db.model.data.authentication.LoginResponse
import com.example.movie_db.model.data.authentication.SessionResponse
import com.example.movie_db.model.data.authentication.UserResponse
import com.example.movie_db.model.network.MovieApiResponse
import com.example.movie_db.model.repository.UserRepository
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class AuthViewModel(private var userRepository: UserRepository) : ViewModel() {

    val liveData = MutableLiveData<State>()
    private var disposable = CompositeDisposable()

//    private val job = Job()
//    override val coroutineContext: CoroutineContext
//        get() = Dispatchers.Main + job

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
//        job.cancel()
    }


    fun getToken(login: String, password: String) {
        liveData.value = State.ShowLoading
        disposable.add(
            userRepository.getTokenRemote(BuildConfig.MOVIE_DB_API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        when (result) {
                            is MovieApiResponse.Success<JsonObject> -> {
                                Log.d("my_debug", result.result.toString())
                                val token = Gson().fromJson(result.result, LoginResponse::class.java)
                                val request = token.requestToken
                                val body = JsonObject().apply {
                                    addProperty("username", login)
                                    addProperty("password", password)
                                    addProperty("request_token", request)
                                }
                                validateWithLogin(body)
                            }
                            is MovieApiResponse.Error -> {
                                Log.d("my_debug", result.error)
                                liveData.value = State.Result(false)
                                liveData.value = State.HideLoading
                            }
                        }
                    },
                    { error ->
                        error.printStackTrace()
                        Log.d("my_debug", error.toString())
                    }
                )
        )
    }

    private fun validateWithLogin(body: JsonObject) {
        disposable.add(
            userRepository.validateWithLoginRemote(BuildConfig.MOVIE_DB_API_KEY, body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        when (result) {
                            is MovieApiResponse.Success<JsonObject> -> {
                                Log.d("my_debug", result.result.toString())
                                val loginResponse = Gson().fromJson(
                                    result.result,
                                    LoginResponse::class.java
                                )
                                val newBody = JsonObject().apply {
                                    addProperty(
                                        "request_token", loginResponse.requestToken.toString()
                                    )
                                }
                                getSession(newBody)
                            }
                            is MovieApiResponse.Error -> {
                                Log.d("my_debug", result.error)
                                liveData.value = State.Result(false)
                                liveData.value = State.HideLoading
                            }
                        }
                    },
                    { error ->
                        error.printStackTrace()
                        Log.d("my_debug", error.toString())
                    }
                )
        )
    }

    private fun getSession(body: JsonObject) {
        disposable.add(
            userRepository.getSessionRemote(BuildConfig.MOVIE_DB_API_KEY, body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        when (result) {
                            is MovieApiResponse.Success<JsonObject> -> {
                                Log.d("my_debug", result.result.toString())
                                val session = Gson().fromJson(
                                    result.result, SessionResponse::class.java
                                )
                                val sessionId = session.sessionId
                                getCurrentAccount(sessionId)
                            }
                            is MovieApiResponse.Error -> {
                                Log.d("my_debug", result.error)
                                liveData.value = State.Result(false)
                                liveData.value = State.HideLoading
                            }
                        }
                    },
                    { error ->
                        error.printStackTrace()
                        Log.d("my_debug", error.toString())
                    }
                )
        )
    }

    fun getCurrentAccount(session: String) {
        disposable.add(
            userRepository.getCurrentAccountRemote(BuildConfig.MOVIE_DB_API_KEY, session)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        when (result) {
                            is MovieApiResponse.Success<JsonObject> -> {
                                Log.d("my_debug", result.result.toString())
                                val account = Gson().fromJson(
                                    result.result, UserResponse::class.java
                                )
                                liveData.value = State.Account(account, session)
                            }
                            is MovieApiResponse.Error -> {
                                Log.d("my_debug", result.error)
                                liveData.value = State.Result(false)
                                liveData.value = State.HideLoading
                            }
                        }
                    },
                    { error ->
                        error.printStackTrace()
                        Log.d("my_debug", error.toString())
                    }
                )
        )
    }

    sealed class State {
        object ShowLoading : State()
        object HideLoading : State()
        data class Result(val isSuccessful: Boolean) : State()
        data class Account(val user: UserResponse, val session: String) : State()
    }
}
