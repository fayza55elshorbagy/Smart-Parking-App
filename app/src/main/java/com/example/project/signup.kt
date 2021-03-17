package com.example.project


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.project.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.sinup.*

class signup : AppCompatActivity() {

  /*  private  val  auth: FirebaseAuth by lazy{
         FirebaseAuth.getInstance()
    }*/
  private lateinit var auth: FirebaseAuth

   private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sinup)

       database = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()

        signup.setOnClickListener {

            val email = email_et.text.toString()
            val pass = pass_et.text.toString()
            val cpass = confpass_et.text.toString()

            //check on data
            if (fristN_et.text.isEmpty())
            {
                fristN_et.error = "please enter name"
                fristN_et.requestFocus()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                email_et.error = "Enter valid Email"
                email_et.requestFocus()
                return@setOnClickListener
            }

            if (pass.isEmpty() && pass.length < 6)
            {
                pass_et.error = "Enter password more than 6 digits"
                pass_et.requestFocus()
                return@setOnClickListener
            }
            if(!pass.equals(cpass)){
                Toast.makeText(this,"Password Not matching",Toast.LENGTH_SHORT).show()
                pass_et.requestFocus()
                confpass_et.requestFocus()
                return@setOnClickListener
            }

            if(phone_et.text.isEmpty() || phone_et.length() < 11)
            {
                phone_et.error = "please enter your phone number"
                phone_et.requestFocus()
                return@setOnClickListener
            }


            signup()
        }
        btn_signin.setOnClickListener {
            var i = Intent(this,signin::class.java)
            startActivity(i)
        }



    }


     fun signup(){

         var email = email_et.text.toString()
         var pass = pass_et.text.toString()
         val name= fristN_et.text.toString()
         val phone = phone_et.text.toString()

     if (email.isNotEmpty() && pass.isNotEmpty())
     {
         auth.createUserWithEmailAndPassword(email ,pass).addOnCompleteListener {

             if(it.isSuccessful)
             {
                 val uid =auth.currentUser!!.uid
                 writeNewUser(uid , phone ,name)
                 Toast.makeText(this , "success check your email & sign in", Toast.LENGTH_LONG).show()
                 SendVerification()

             }else
             {
                 Toast.makeText(this , it.exception.toString(), Toast.LENGTH_LONG).show()
             }
         }
     }

 }

   private fun writeNewUser(userId: String,phone:String, name: String) {
        val user =UserData(phone , name)
        database.child("users").child(userId).setValue(user)
    }

 fun SendVerification(){
      var user =  auth.currentUser
      user?.sendEmailVerification()?.addOnCompleteListener {

          if(it.isSuccessful)
          {
              var i_signin = Intent(this  , signin::class.java)
              i_signin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
              startActivity(i_signin)
          }

      }
  }





}