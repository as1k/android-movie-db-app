package com.example.movie_db.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.example.movie_db.R
import com.example.movie_db.Retrofit
import com.example.movie_db.BuildConfig
import com.example.movie_db.classes.TokenResponse
import com.example.movie_db.classes.UserResponse
import com.example.movie_db.classes.User
import com.example.movie_db.classes.LoginResponse
import com.example.movie_db.classes.SessionResponse
import com.google.gson.Gson
import com.google.gson.JsonObject
import android.widget.ProgressBar
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var login: EditText
    private lateinit var password: EditText
    private lateinit var loginBtn: Button
    private lateinit var progressBar: ProgressBar
    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_in_activity)
        bindView()
        setData()
    }

    private fun bindView(){
        login = findViewById(R.id.et_username)
        password = findViewById(R.id.et_password)
        loginBtn = findViewById(R.id.login_btn)
        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.GONE
    }

    private fun setData(){
        loginBtn.setOnClickListener {
            onLoggingIn(
                login.text.toString(),
                password.text.toString()
            )
            progressBar.visibility = View.VISIBLE
        }
    }

    private fun onLoggingIn(login: String, password: String) {
        launch {
            withContext(Dispatchers.IO) {
                try {
                    val response =
                        Retrofit.getPostApi().getTokenCoroutine(BuildConfig.MOVIE_DB_API_KEY)
                    if (response.isSuccessful) {
                        val token = Gson().fromJson(response.body(), TokenResponse::class.java)
                        if (token != null) {
                            Log.d("Done", "Token Created")
                            val request = token.requestToken
                            val body = JsonObject().apply {
                                addProperty("username", login)
                                addProperty("password", password)
                                addProperty("request_token", request)
                            }
                            getLoginResponseCoroutine(body)
                        } else {
                            Log.e("Error", "Cannot create token")
                        }
                        progressBar.visibility = View.GONE
                    } else {
                        noSuchUser()
                        Log.e("Error", "Cannot create token")
                        progressBar.visibility = View.GONE
                    }
                } catch (e: Exception) {
                    Log.e("Error", "Cannot create token")
                }
            }
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

    fun noSuchUser() {
        Toast.makeText(this@MainActivity, "Can't find such user", Toast.LENGTH_SHORT).show()
    }

    fun loginSuccess(user: UserResponse, session: String) {
        User.user = user
        User.user!!.sessionId = session
        saveSession()
        val intent = Intent(this, FragmentsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun getAccountCoroutine(session: String?) {
        launch {
            val response = Retrofit.getPostApi()
                .getCurrentAccountCoroutine(BuildConfig.MOVIE_DB_API_KEY, session!!)
            if (response.isSuccessful) {
                val account = Gson().fromJson(response.body(), UserResponse::class.java)
                if (account != null)
                    loginSuccess(account, session)
                progressBar.visibility = View.GONE
            } else {
                progressBar.visibility = View.GONE
                noSuchUser()
            }
        }
    }

    private fun getSessionCoroutine(body: JsonObject) {
        launch {
            withContext(Dispatchers.IO) {
                try {
                    val response = Retrofit.getPostApi()
                        .getSessionCoroutine(BuildConfig.MOVIE_DB_API_KEY, body)
                    if (response.isSuccessful) {
                        val session = Gson().fromJson(response.body(), SessionResponse::class.java)
                        if (session != null) {
                            val sessionId = session.sessionId
                            getAccountCoroutine(sessionId)
                        } else {
                            Log.e("Error", "Cannot create session_id")
                        }
                        progressBar.visibility = View.GONE
                    } else {
                        Log.e("Error", "Cannot create session_id")
                        progressBar.visibility = View.GONE
                        noSuchUser()
                    }
                } catch (e: Exception) {
                    Log.e("Error", "Cannot create session_id")
                }
            }
        }
    }

    private fun getLoginResponseCoroutine(body: JsonObject) {
        launch {
            withContext(Dispatchers.IO) {
                try {
                    val response = Retrofit.getPostApi()
                        .loginCoroutine(BuildConfig.MOVIE_DB_API_KEY, body)
                    if (response.isSuccessful) {
                        val loginResponse =
                            Gson().fromJson(response.body(), LoginResponse::class.java)
                        if (loginResponse != null) {
                            Log.d("Done", "Token Created")
                            val body = JsonObject().apply {
                                addProperty(
                                    "request_token",
                                    loginResponse.requestToken.toString()
                                )
                            }
                            getSessionCoroutine(body)
                        }
                        progressBar.visibility = View.GONE
                    } else {
                        progressBar.visibility = View.GONE
                        noSuchUser()
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        this@MainActivity,
                        "Incorrect data", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}
