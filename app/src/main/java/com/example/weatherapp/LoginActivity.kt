package com.example.weatherapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var username: EditText
    private lateinit var password: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth
        username = findViewById(R.id.login_et_username)
        password = findViewById(R.id.login_et_password)

        supportActionBar?.hide()
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    fun forgotPassword(view: View) {
        Toast.makeText(this, "Forgot password clicked", Toast.LENGTH_SHORT).show()
    }

    fun loginUser(view: View) {
        var validation = checkValidation()
        if (validation) {
            auth.signInWithEmailAndPassword(username.text.toString(), password.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        startActivity(Intent(this, MainActivity::class.java))
                        Toast.makeText(this, "Welcome back ${username.text}", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun checkValidation(): Boolean {
        var valid = false
        if (username.text.isEmpty() && !username.text.contains("@")) {
            username.error = "Field cannot be empty and must contain @"
        } else if (password.text.length < 8) {
            password.error = "Length of password has to be at least 8 character"
        } else {
            valid = true
        }
        return valid
    }

    fun registerUser(view: View) {
        startActivity(Intent(this, RegisterActivity::class.java))
    }
}