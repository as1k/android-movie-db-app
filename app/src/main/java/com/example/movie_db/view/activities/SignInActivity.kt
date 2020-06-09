package com.example.movie_db.view.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.movie_db.R
import com.example.movie_db.model.data.authentication.User
import com.example.movie_db.model.data.authentication.UserResponse
import com.example.movie_db.view_model.AuthViewModel
import com.example.movie_db.view_model.ViewModelProviderFactory
import com.google.gson.Gson

class SignInActivity : AppCompatActivity() {

    private lateinit var login: EditText
    private lateinit var password: EditText
    private lateinit var loginBtn: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_in_activity)
        bindView()
        setData()
    }

    private fun bindView() {
        login = findViewById(R.id.et_username)
        password = findViewById(R.id.et_password)
        loginBtn = findViewById(R.id.login_btn)
        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.GONE
    }

    private fun setData() {

        val viewModelProviderFactory = ViewModelProviderFactory(this)
        authViewModel = ViewModelProvider(this, viewModelProviderFactory)
            .get(AuthViewModel::class.java)
        authViewModel.liveData.observe(this, Observer { result ->
            when (result) {
                is AuthViewModel.State.ShowLoading -> {
                    progressBar.visibility = View.VISIBLE
                }
                is AuthViewModel.State.HideLoading -> {
                    progressBar.visibility = View.GONE
                }
                is AuthViewModel.State.Result -> {
                    if (!result.isSuccess)
                        noSuchUser()
                }
                is AuthViewModel.State.Account -> {
                    loginSuccess(result.user, result.session)
                }
            }
        })

        loginBtn.setOnClickListener {
            authViewModel.onLoggingIn(
                login.text.toString(),
                password.text.toString()
            )
            progressBar.visibility = View.VISIBLE
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

    private fun noSuchUser() {
        Toast.makeText(this@SignInActivity, "Can't find such user", Toast.LENGTH_SHORT).show()
    }

    private fun loginSuccess(user: UserResponse, session: String) {
        User.user = user
        User.user!!.sessionId = session
        saveSession()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

}
