package com.example.movie_db.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.movie_db.BuildConfig
import com.example.movie_db.R
import com.example.movie_db.Retrofit
import com.example.movie_db.classes.UserResponse
import com.example.movie_db.classes.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Exception
import java.lang.reflect.Type
import kotlin.coroutines.CoroutineContext

class Activator : AppCompatActivity(), CoroutineScope {

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_up_activity)
        val savedUser: SharedPreferences =
            this.getSharedPreferences("current_user", Context.MODE_PRIVATE)
        val user = savedUser.getString("current_user", null)
        val type: Type = object : TypeToken<UserResponse>() {}.type
        User.user = Gson().fromJson<UserResponse>(user, type)
        if (User.user != null && User.user!!.sessionId != null)
            getAccountCoroutine(User.user!!.sessionId.toString())
        else {
            val intent = Intent(this, SignInActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun saveSession() {
        val savedUser: SharedPreferences =
            this.getSharedPreferences("current_user", Context.MODE_PRIVATE)
        val userEditor = savedUser.edit()
        val user: String = Gson().toJson(User.user)
        userEditor.putString("current_user", user)
        userEditor.apply()
    }

    private fun loginSuccess(user: UserResponse, session: String) {
        User.user = user
        User.user!!.sessionId = session
        saveSession()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun getAccountCoroutine(session: String) {
        launch {
            try {
                val response = Retrofit.getPostApi()
                    .getCurrentAccountCoroutine(BuildConfig.MOVIE_DB_API_KEY, session)
                if (response.isSuccessful) {
                    val account = Gson().fromJson(response.body(), UserResponse::class.java)
                    if (account != null)
                        loginSuccess(account, session)
                    else {
                        User.user = null
                        val intent = Intent(this@Activator, SignInActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                }
            } catch (e: Exception) {
                val intent = Intent(this@Activator, MainActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
    }
}
