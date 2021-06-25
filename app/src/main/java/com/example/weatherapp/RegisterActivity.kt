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

class RegisterActivity : AppCompatActivity() {

    private lateinit var username : EditText
    private lateinit var password : EditText
    private lateinit var confirmPassword : EditText
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        username = findViewById(R.id.register_et_username)
        password = findViewById(R.id.register_et_password)
        confirmPassword = findViewById(R.id.register_et_confirm_Password)
        supportActionBar?.hide()
        auth = Firebase.auth
    }


    fun registerUser(view: View) {
        var validation = areFieldsValid()
        if (validation){

            auth.createUserWithEmailAndPassword(username.text.toString(), password.text.toString())
                .addOnCompleteListener(this){task ->
                    if(task.isSuccessful){
                        Toast.makeText(this, "Your account was created", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                    } else{
                        Toast.makeText(this, "Something went wrong. Try again later!", Toast.LENGTH_LONG).show()
                    }
                }
        }else{
            Toast.makeText(this, "Something is not right!", Toast.LENGTH_LONG).show()
        }
    }

    private fun areFieldsValid(): Boolean {
        var validationResult = false
        if (username.text.isEmpty() && !username.text.contains("@")){
            username.error = "This field is required and must contain @"
        }else if(password.text.length < 8){
            password.error = "Length of password has to be at least 8 characters"
        }else if (confirmPassword.text.length < 8){
            confirmPassword.error = "Length of password has to be at least 8 characters"
        }else if(password.text.toString() != confirmPassword.text.toString()){
            password.error = "Passwords do not match"
            confirmPassword.error = "Passwords do not match"
        }else{
            validationResult = true
        }
        return validationResult
    }

    fun loginUser(view: View) {
        startActivity(Intent(this, LoginActivity::class.java))
    }
}