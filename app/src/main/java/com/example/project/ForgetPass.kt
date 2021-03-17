package com.example.project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.forget_pass.*


class ForgetPass : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.forget_pass)


        btn_reset.setOnClickListener {
            val emailAddress = email_reset.text.toString()

            if(email_reset.text.isEmpty())
            {
                email_reset.error = "Enter your Email"
                email_reset.requestFocus()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()){
                email_reset.error = "Enter valid Email"
                email_reset.requestFocus()
                return@setOnClickListener
            }



            val auth = FirebaseAuth.getInstance()


       auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                     Toast.makeText(this , "Email is send.", Toast.LENGTH_LONG).show()
                      var i_signin = Intent(this  , signin::class.java)
                      startActivity(i_signin)
                 }
              }


        }

        btn_reset.setOnClickListener {
            var i_signup = Intent(this  , signup::class.java)
            startActivity(i_signup)
        }


    }
}
