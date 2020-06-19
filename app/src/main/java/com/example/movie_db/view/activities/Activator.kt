package com.example.movie_db.view.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.movie_db.R
import com.example.movie_db.model.data.authentication.UserResponse
import com.example.movie_db.model.data.authentication.User
import com.example.movie_db.view_model.AuthViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import org.koin.androidx.viewmodel.ext.android.viewModel

class Activator : AppCompatActivity() {
    private val authViewModel: AuthViewModel by viewModel<AuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_up_activity)

        authViewModel.liveData.observe(this, Observer { result ->
            when (result) {
                is AuthViewModel.State.Result -> {
                    if (!result.isSuccess) {
                        val intent = Intent(this@Activator, MainActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                }
                is AuthViewModel.State.Account -> {
                    loginSuccess(result.user, result.session)
                }
            }
        })

        val savedUser: SharedPreferences =
            this.getSharedPreferences("current_user", Context.MODE_PRIVATE)
        val user = savedUser.getString("current_user", null)
        if (user != null) {
            val type: Type = object : TypeToken<UserResponse>() {}.type
            User.user = Gson().fromJson<UserResponse>(user, type)
            if (User.user!!.sessionId != null) {
                authViewModel.getAccount(User.user!!.sessionId.toString())
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }

        } else {
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
        User.user?.sessionId = session
        saveSession()
    }
}
