package com.example.movie_db.view.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.movie_db.R

class SignUpActivity : AppCompatActivity() {

    private lateinit var fullName: EditText
    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var btnRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_up_activity)

        bindViews()
        btnRegister.setOnClickListener{
            if(fullName.text.toString() != "" && username.text.toString() != "" && password.text.toString() != "") {
                Toast.makeText(
                    this@SignUpActivity,
                    "${fullName.text}, You have successfully registered!",
                    Toast.LENGTH_SHORT
                ).show()
                onBackPressed()
            } else {
                Toast.makeText(
                    this@SignUpActivity,
                    "Fill all blanks",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun bindViews() {
        fullName = findViewById(R.id.sign_up_full_name)
        username = findViewById(R.id.reg_et_username)
        password = findViewById(R.id.reg_et_passsword)
        btnRegister = findViewById(R.id.reg_btn)
    }
}
