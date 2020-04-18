package com.example.movie_db

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SignUpActivity: AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_up_activity)
        val fullName = findViewById(R.id.sign_up_full_name) as? EditText
        val login = findViewById(R.id.reg_et_username) as? EditText
        val password = findViewById(R.id.reg_et_passsword) as? EditText

        val sharedPreferences = getSharedPreferences("UserInfo",0)

        (findViewById(R.id.reg_btn) as? Button)?.setOnClickListener{
            val firstNameValue = fullName?.text.toString()
            val loginVal = login?.text.toString()
            val passwordVal = password?.text.toString()

            if(loginVal.trim().length > 1){
                val redactor = sharedPreferences.edit()

                redactor.putString("firstName",firstNameValue)
                redactor.putString("username",loginVal)
                redactor.putString("password",passwordVal)
                redactor.apply()
                Toast.makeText(applicationContext,"New user registered!",Toast.LENGTH_SHORT).show()
                val toLogin = Intent(applicationContext,
                    MainActivity::class.java)
                startActivity(toLogin)
            }else{
                Toast.makeText(applicationContext,"Failed to sign up. The fields are not completed.",Toast.LENGTH_SHORT).show()
            }
        }
    }
}