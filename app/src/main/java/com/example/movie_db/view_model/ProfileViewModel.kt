package com.example.movie_db.view_model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.movie_db.BuildConfig
import com.example.movie_db.model.data.authentication.CurrentUser
import com.example.movie_db.model.network.MovieApiResponse
import com.example.movie_db.model.repository.UserRepository
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class ProfileViewModel(private var userRepository: UserRepository? = null) : ViewModel() {

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


    fun logout() {
        liveData.value = State.ShowLoading
        val body = JsonObject().apply {
            addProperty("session_id", CurrentUser.user?.sessionId)
        }
        disposable.add(
            userRepository?.deleteSessionRemote(BuildConfig.MOVIE_DB_API_KEY, body)
                !!.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                { result ->
                    when (result) {
                        is MovieApiResponse.Success<JsonObject> -> {
                            Log.d("my_debug", result.result.toString())
                            liveData.value = State.Result(true)
                            liveData.value = State.HideLoading
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
                    Log.d("delete_profile", error.toString())
                })
        )
    }

    sealed class State {
        object ShowLoading : State()
        object HideLoading : State()
        data class Result(val isSuccess: Boolean) : State()
    }
}