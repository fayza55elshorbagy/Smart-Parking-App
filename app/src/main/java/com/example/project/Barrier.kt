package com.example.project

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_barrier.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class Barrier : AppCompatActivity() {
    var mId: ArrayList<Book>? = null
    val database = FirebaseDatabase.getInstance()
    val myRef = database.reference
    var mAth : FirebaseAuth?= null

    var total_rate:Float? = null
    var total_user:Int?= null
    var rate :Float?= null
    var lan : Int? = null
    var flag : Boolean? = false

    override fun onStart() {
        super.onStart()
        mAth= FirebaseAuth.getInstance()
        myRef.child("ParkirApp").child("users").child("FCI_Garage")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    val text = p0.message
                    Toast.makeText(this@Barrier, text, Toast.LENGTH_LONG).show()
                }

                override fun onDataChange(p0: DataSnapshot) {
                    for (Data in p0.children) {
                        val tf = Data.getValue(Book::class.java)
                        mId?.add(tf!!)
                    }
                }

            }

            )
        myRef.child("ParkirApp").child("GARAGE").child("FCI_Garage")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    val text = p0.message
                    Toast.makeText(this@Barrier, text, Toast.LENGTH_LONG).show()
                }

                override fun onDataChange(p0: DataSnapshot) {
                    total_rate  = p0.child("total_rate").getValue(Float::class.java)
                    total_user =  p0.child("total_user").getValue(Int::class.java)
                    rate = p0.child("rate").getValue(Float::class.java)
                    val name = p0.child("Name").getValue(String::class.java)
                    grage_name.text = name
                }

            })


        myRef.child("ParkirApp").child("users").child("FCI_Garage")
            .child(mAth!!.currentUser!!.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    val text = p0.message
                    Toast.makeText(this@Barrier, text, Toast.LENGTH_LONG).show()
                }

                override fun onDataChange(p0: DataSnapshot) {

                     lan = p0.child("lan").getValue(Int::class.java)
                    node_no.text = lan.toString()

                    val start_Time = p0.child("startTime").getValue(String::class.java)
                    start_time.text = start_Time

                    val start_Date =  p0.child("startDate").getValue(String::class.java)
                    start_date.text = start_Date

                    val end_Time = p0.child("endTime").getValue(String::class.java)
                    end_time.text = end_Time

                    val end_Date =  p0.child("endDate").getValue(String::class.java)
                    end_date.text = end_Date

                }
            })

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barrier)
        mId = ArrayList()


        open.setOnClickListener {

            val f=  compId(mId!!)
            if (f == true) {

                myRef.child("ParkirApp").child("iot").child("FCI_Garage")
                    .child("Entrance").setValue(1)
                Toast.makeText(this, "Opend", Toast.LENGTH_LONG).show()
                open.isEnabled=false
                 checkLan()
                flag = true
                if (flag == true)
                {
                    CANCEL.visibility = View.GONE

                }
                //checkopen()



            } else {
                //Toast.makeText(this, "Check your Date!", Toast.LENGTH_LONG).show()
                //  println(mId)
                checkopen()
            }
        }

        leave.setOnClickListener {

                 myRef.child("ParkirApp").child("users").child("FCI_Garage").child(mAth!!.currentUser!!.uid)
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        val text = p0.message
                        Toast.makeText(this@Barrier, text, Toast.LENGTH_LONG).show()
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        val CheckPAY = p0.child("checkPAY").getValue(Boolean::class.java)
                        if (CheckPAY == false) {
                            // AlertDialog
                            checkpay()
                        }
                        else {
                            myRef.child("ParkirApp").child("iot").child("FCI_Garage")
                                .child("Exit").setValue(1)
                            rate ()

                        }

                    }


                })

        }



    }
    fun checkpay(){
        val mAlertDialog = AlertDialog.Builder(this@Barrier)
        mAlertDialog.setTitle("PAYMENT! ") //set alertdialog title
        mAlertDialog.setMessage("You Had to Pay Before You Go.") //set alertdialog message
        mAlertDialog.setPositiveButton("Ok") { dialog, id ->
            //perform some tasks here
              val i = Intent(this@Barrier, Reciept::class.java)
             startActivity(i)
        }
        mAlertDialog.setNeutralButton("Cancel") { _, _ ->

        }
        mAlertDialog.show()
    }

    fun checkLan(){
        val mAlertDialog = AlertDialog.Builder(this@Barrier)
        mAlertDialog.setTitle("WARNING! ") //set alertdialog title
        mAlertDialog.setMessage("OPENED \n You must stick  to your Lane    NUmber.") //set alertdialog message
        mAlertDialog.setPositiveButton("Ok") { dialog, id ->

        }
        mAlertDialog.show()
    }

    fun checkopen(){
        val mAlertDialog = AlertDialog.Builder(this@Barrier)
        mAlertDialog.setTitle("WARNING! ") //set alertdialog title
        mAlertDialog.setMessage("Please, Wait for your appointment.") //set alertdialog message
        mAlertDialog.setPositiveButton("Ok") { dialog, id ->
        }
        mAlertDialog.show()

        }
    fun rate (){

        val dialog = Dialog(this@Barrier)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.rating_dailog)
        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val ratingBar = dialog.findViewById<RatingBar>(R.id.ratingBar)

        ratingBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            val selectedRate = rating
            val tu= total_user?.plus(1)
            val t = total_rate?.plus(selectedRate)
            myRef.child("ParkirApp").child("GARAGE")
                .child("FCI_Garage").child("total_rate").setValue(t)
            myRef.child("ParkirApp").child("GARAGE")
                .child("FCI_Garage").child("total_user").setValue(tu)
            val rr = t!!.div(tu!!)
            myRef.child("ParkirApp").child("GARAGE")
                .child("FCI_Garage").child("rate").setValue(rr)

            //
            dialog.dismiss()
            DeleteUserData1(mAth!!.currentUser!!.uid)
            DeleteUserData2(mAth!!.currentUser!!.uid)
            var intent = Intent(this@Barrier,MapsActivity::class.java)
            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

        val noThanksBtn = dialog.findViewById<TextView>(R.id.Nothank_btn)

        noThanksBtn.setOnClickListener {

            dialog.dismiss()
            DeleteUserData1(mAth!!.currentUser!!.uid)
            DeleteUserData2(mAth!!.currentUser!!.uid)
            var intent = Intent(this@Barrier,MapsActivity::class.java)
            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
        dialog.show()


    }

    fun compId(array : ArrayList<Book>): Boolean {
        val currentHour = LocalDateTime.now().hour
        val currentminute = LocalDateTime.now().minute
        val time = "$currentHour:$currentminute"
        val date = SimpleDateFormat("kk:mm").parse(time)
        val currentTime = date.time

        val formatted = LocalDateTime.now().format(DateTimeFormatter.ofPattern("d/M/yyyy"))
        val df = DateTimeFormatter.ofPattern("d/M/yyyy", Locale.ENGLISH)
        val cuurentdate = LocalDate.parse(formatted, df)


        var flag = false
        for (i in array!!) {

            val DBstart = SimpleDateFormat("hh:mm").parse(i.StartTime!!).time
            val DBend = SimpleDateFormat("hh:mm").parse(i.EndTime!!).time
            val df = DateTimeFormatter.ofPattern("d/M/yyyy", Locale.ENGLISH)
            val startDate = LocalDate.parse(i.StartDate!!, df)
            val endDate = LocalDate.parse(i.EndDate!!, df)

            if (i.ID == mAth!!.currentUser!!.uid) {
                if (startDate == endDate) {
                    if (cuurentdate == startDate && (currentTime >= DBstart && currentTime < DBend))
                        flag = true
                } else if (cuurentdate > startDate && cuurentdate < endDate) {
                    flag = true
                } else if (startDate != endDate) {
                    if (cuurentdate == startDate && (currentTime >= DBstart)) {
                        flag = true
                    }
                    else if (cuurentdate == endDate && (currentTime < DBend)) {
                        flag = true
                    }
                }
            }
        }
        return flag
    }

    private fun DeleteUserData1(uid: String) {
        val dr =
            FirebaseDatabase.getInstance().getReference("ParkirApp").child("Book")
                .child("FCI_Garage").child(lan.toString()).child(uid)
        dr.removeValue()
    }
    private fun DeleteUserData2(uid: String) {
        val dr =
            FirebaseDatabase.getInstance().getReference("ParkirApp").child("users")
                .child("FCI_Garage").child(uid)
        dr.removeValue()
    }
    fun checkcancel() {
        val mAlertDialog = AlertDialog.Builder(this@Barrier)
        mAlertDialog.setMessage("Your Reservation has cancled.") //set alertdialog message
        mAlertDialog.setPositiveButton("Ok") { dialog, id ->
            //perform some tasks here
            //val i = Intent(this@Barrier, Reciept::class.java)
            //startActivity(i)
        }
        mAlertDialog.show()
    }



    fun cancel(view: View) {
        DeleteUserData1(mAth!!.currentUser!!.uid)
        checkcancel()
        Handler().postDelayed({
            //start welcome activity
            startActivity(Intent(this@Barrier , MapsActivity::class.java))
            //finish this activity
            finish()
        }, 1100)
    }


}

