package com.example.movie_db.view.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.movie_db.R
import com.example.movie_db.model.data.authentication.UserResponse
import com.example.movie_db.model.data.authentication.CurrentUser
import com.example.movie_db.view_model.AuthViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import org.koin.androidx.viewmodel.ext.android.viewModel

class Activator : AppCompatActivity() {
    private val authViewModel: AuthViewModel by viewModel<AuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.sign_up_activity)

        authViewModel.liveData.observe(this, Observer { result ->
            when (result) {
                is AuthViewModel.State.Result -> {
                    if (!result.isSuccessful) {
                        val intent = Intent(this@Activator, SignInActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                }
                is AuthViewModel.State.Account -> {
                    loginSuccessful(result.user, result.session)
                }
            }
        })

        val savedUser: SharedPreferences =
            this.getSharedPreferences("current_user", Context.MODE_PRIVATE)
        val user = savedUser.getString("current_user", null)
        if (user != null) {
            val type: Type = object : TypeToken<UserResponse>() {}.type
            CurrentUser.user = Gson().fromJson<UserResponse>(user, type)
            if (CurrentUser.user!!.sessionId != null) {
                authViewModel.getCurrentAccount(CurrentUser.user!!.sessionId.toString())
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
        val user: String = Gson().toJson(CurrentUser.user)
        userEditor.putString("current_user", user)
        userEditor.apply()
    }

    private fun loginSuccessful(user: UserResponse, session: String) {
        CurrentUser.user = user
        CurrentUser.user?.sessionId = session
        saveSession()
    }
}
