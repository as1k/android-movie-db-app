package com.example.movie_db.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.ProgressBar

class MainActivity : AppCompatActivity() {

    private lateinit var login: EditText
    private lateinit var password: EditText
    private lateinit var loginBtn: Button
    private lateinit var progressBar: ProgressBar

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
        var token: TokenResponse?
        Retrofit.getPostApi()
            .getToken(BuildConfig.MOVIE_DB_API_KEY)
            .enqueue(object : Callback<JsonObject> {
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    noSuchUser()
                    progressBar.visibility = View.GONE
                }

                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        token = Gson().fromJson(response.body(), TokenResponse::class.java)
                        if (token != null) {
                            val request = token!!.requestToken
                            val body = JsonObject().apply {
                                addProperty("username", login)
                                addProperty("password", password)
                                addProperty("request_token", request)
                            }
                            getLoginResponse(body)
                        }
                    } else {
                        progressBar.visibility = View.GONE
                    }
                }

            })
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

    fun getAccount(session: String?) {
        var user: UserResponse?
        Retrofit.getPostApi().getCurrentAccount(
            BuildConfig.MOVIE_DB_API_KEY,
            session!!
        ).enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                noSuchUser()
                progressBar.visibility = View.GONE
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    user = Gson().fromJson(
                        response.body(),
                        UserResponse::class.java
                    )
                    if (user != null) {
                        loginSuccess(user!!, session)
                    }
                } else {
                    progressBar.visibility = View.GONE
                }
            }

        })
    }

    fun getSession(body: JsonObject) {
        var session: SessionResponse?
        Retrofit.getPostApi().getSession(
            BuildConfig.MOVIE_DB_API_KEY, body)
            .enqueue(object : Callback<JsonObject> {
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    noSuchUser()
                }

                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        session = Gson().fromJson(
                            response.body(),
                            SessionResponse::class.java
                        )
                        if (session != null) {
                            val sessionId = session!!.sessionId
                            getAccount(sessionId)
                        }
                    } else {
                        progressBar.visibility = View.GONE
                    }
                }
            })
    }

    fun getLoginResponse(body: JsonObject) {
        var loginResponse: LoginResponse?
        Retrofit.getPostApi().login(
            BuildConfig.MOVIE_DB_API_KEY,
            body
        ).enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                progressBar.visibility = View.GONE
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    loginResponse = Gson().fromJson(response.body(), LoginResponse::class.java)
                    if (loginResponse != null) {
                        val body = JsonObject().apply {
                            addProperty(
                                "request_token",
                                loginResponse!!.requestToken.toString()
                            )
                        }
                        getSession(body)
                    }
                } else {
                    noSuchUser()
                    progressBar.visibility = View.GONE
                }
            }
        })
    }
}
