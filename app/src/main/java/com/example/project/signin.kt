package com.example.project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.signin.*

class signin : AppCompatActivity(), TextWatcher {

     private lateinit var mAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signin)

        mAuth = FirebaseAuth.getInstance()
        email_et.addTextChangedListener(this@signin)
        password_et.addTextChangedListener(this@signin)



        btn_signin.setOnClickListener {
            val email =  email_et.text.toString()
            val pass = password_et.text.toString()
            if(email_et.text.isEmpty())
            {
                email_et.error = "Please Enter your Email"
                email_et.requestFocus()
                return@setOnClickListener
            }
            //يشوف الايميل مكتوب صح ولا لا
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                email_et.error = "Enter valid Email"
                email_et.requestFocus()
                return@setOnClickListener
            }

            if(password_et.text.isEmpty()&& pass.length < 6)
            {
                password_et.error = "Enter password more than 6 digits"
                password_et.requestFocus()
                return@setOnClickListener
            }
           // progressBar.visibility = View.VISIBLE


            login()
           // signin(email , pass)


        }


        forget_pass.setOnClickListener {
            val i_foget = Intent(this@signin , ForgetPass::class.java)
            startActivity(i_foget)
        }

        btn_signup.setOnClickListener {
            val i = Intent(this@signin , signup::class.java)
            startActivity(i)
        }


    }



    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        btn_signin.isEnabled = email_et.text.trim().isNotEmpty()
                && password_et.text.trim().isNotEmpty()
    }

     fun login(){

        var email = email_et.text.toString()
        var pass = password_et.text.toString()

        if (email.isNotEmpty() && pass.isNotEmpty())
        {
            mAuth?.signInWithEmailAndPassword(email ,pass)?.addOnCompleteListener {

                if(it.isSuccessful)
                {
               // progressBar.visability = View.GONNE
                    IsVerfied()

                }else
                {
                    Toast.makeText(this , it.exception.toString(),Toast.LENGTH_LONG).show()
                   // progressBar.visability = View.GONNE
                }
            }
        }
    }
    fun IsVerfied(){

        var user =  mAuth?.currentUser
        if(user!!.isEmailVerified )
        {
            var intentToMain = Intent(this  , MapsActivity::class.java)
            //intentToMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intentToMain)
        }else
        {
            Toast.makeText(this , "please verify your acc",Toast.LENGTH_LONG).show()
        }
    }


}

