package com.example.movie_db.view_model

import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.movie_db.BuildConfig
import com.example.movie_db.model.data.authentication.User
import com.example.movie_db.model.repository.UserRepository
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class ProfileViewModel(
    private var userRepository: UserRepository? = null
) : ViewModel(), CoroutineScope {

    private val job = Job()
    val liveData = MutableLiveData<State>()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCleared() {
        super.onCleared()
        job.complete()
    }

    fun logout(rootView: ViewGroup) {
        launch {
            liveData.value = State.ShowLoading
            try {
                val body = JsonObject().apply {
                    addProperty("session_id", User.user?.sessionId)
                }
                userRepository?.deleteSessionCoroutine(BuildConfig.MOVIE_DB_API_KEY, body)
                liveData.value = State.Result(true)
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
    }
}