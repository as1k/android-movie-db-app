package com.example.movie_db.view.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.movie_db.R
import com.example.movie_db.model.data.authentication.CurrentUser
import com.example.movie_db.model.data.authentication.UserResponse
import com.example.movie_db.view_model.AuthViewModel
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.lang.Exception

class SignInActivity : AppCompatActivity() {

    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var btnLogin: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var signUpTextView: TextView
    private val authViewModel: AuthViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_in_activity)
        bindViews()
        setData()
    }

    private fun bindViews() {
        username = findViewById(R.id.et_username)
        password = findViewById(R.id.et_password)
        btnLogin = findViewById(R.id.btn_login)
        progressBar = findViewById(R.id.progress_bar)
        signUpTextView = findViewById(R.id.tv_link_sign_up)
        progressBar.visibility = View.GONE
    }

    private fun setData() {
        authViewModel.liveData.observe(this, Observer { result ->
            when (result) {
                is AuthViewModel.State.ShowLoading -> {
                    progressBar.visibility = View.VISIBLE
                }
                is AuthViewModel.State.HideLoading -> {
                    progressBar.visibility = View.GONE
                }
                is AuthViewModel.State.Result -> {
                    if (!result.isSuccessful)
                        noSuchUserToast()
                }
                is AuthViewModel.State.Account -> {
                    loginSuccessful(result.user, result.session)
                }
            }
        })

        btnLogin.setOnClickListener {
            if(username.text.toString() != "" && password.text.toString() != "") {
                authViewModel.getToken(
                    username.text.toString(),
                    password.text.toString()
                )
                progressBar.visibility = View.VISIBLE
            }
            else {
                Toast.makeText(this@SignInActivity, "Fill all blanks", Toast.LENGTH_SHORT).show()
            }
        }

        signUpTextView.setOnClickListener {
            try {
                val intent = Intent(this, SignUpActivity::class.java)
                startActivity(intent)
            }
            catch (e: Exception) {
                Log.d("my_debug", e.toString())
            }
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
        CurrentUser.user!!.sessionId = session
        saveSession()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun noSuchUserToast() {
        Toast.makeText(this@SignInActivity, "Can't find such user", Toast.LENGTH_SHORT).show()
    }
}
