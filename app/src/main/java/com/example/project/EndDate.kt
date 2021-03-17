package com.example.project;

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.ContextThemeWrapper
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlinx.android.synthetic.main.end_date.*
import kotlinx.android.synthetic.main.start_date.*
import java.util.*

class EndDate : AppCompatActivity() {
    var mData: ArrayList<Book>? = null
    var mData1: ArrayList<Book>? = null
    var mData2: ArrayList<Book>? = null
    var mAth : FirebaseAuth? = null

    private lateinit var database: DatabaseReference

    override fun onStart() {
        super.onStart()
        database.child("ParkirApp").child("Book").child("FCI_Garage").child("1")
            .addValueEventListener (object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                val text = p0.message
                Toast.makeText(this@EndDate, text, Toast.LENGTH_LONG).show()
            }

            override fun onDataChange(p0: DataSnapshot) {
                for (Data in p0.children) {
                    val tf = Data.getValue(Book::class.java)
                    mData?.add(tf!!)
                }
            }
        })
        database.child("ParkirApp").child("Book").child("FCI_Garage").child("2")
            .addValueEventListener (object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                val text = p0.message
                Toast.makeText(this@EndDate, text, Toast.LENGTH_LONG).show()
            }

            override fun onDataChange(p0: DataSnapshot) {
                for (Data in p0.children) {
                    val tf = Data.getValue(Book::class.java)
                    mData1?.add(tf!!)
                }
            }
        })

        database.child("ParkirApp").child("Book").child("FCI_Garage").child("3").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                val text = p0.message
                Toast.makeText(this@EndDate, text, Toast.LENGTH_LONG).show()
            }

            override fun onDataChange(p0: DataSnapshot) {
                for (Data in p0.children) {
                    val tf = Data.getValue(Book::class.java)
                    mData2?.add(tf!!)
                }
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.end_date)

        database = FirebaseDatabase.getInstance().reference
        mAth = FirebaseAuth.getInstance()
        mData = ArrayList()
        mData1 = ArrayList()
        mData2 = ArrayList()



        txt_date_picker1.setOnClickListener {
            val mDatePicker1 = DatePickerFragment2()
            mDatePicker1.show(this.fragmentManager, "Date Picker")

        }

        endd.setOnClickListener {
            // Initialize a new TimePickerFragment
            val newFragment = Time2PickerFragmen()
            // Show the time picker dialog
            newFragment.show(this.fragmentManager, "Time Picker")
        }

        opendate.setOnClickListener {
            endd.text= "23:59"
        }
        done_btn.setOnClickListener {
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            var s = intent
            val start = s.extras!!.getString("startTme")
            // val start = startt.text.toString()
            val end = endd.text.toString()
            val st = s.extras!!.getString("startDate")
            // val st = txt_date_picker.text.toString()
            val et = txt_date_picker1.text.toString()
            val df = DateTimeFormatter.ofPattern("d/M/yyyy", Locale.ENGLISH)
            val BookSD = LocalDate.parse(st, df)
            val BookED = LocalDate.parse(et, df)
            val BookStart = sdf.parse(start)
            val BookEnd = sdf.parse(end)

            if (st == "DD-MM-YYYY") {
                Toast.makeText(this, "Enter your choose", Toast.LENGTH_LONG).show()
            } else if (et == "DD-MM-YYYY") {
                Toast.makeText(this, "Enter your choose", Toast.LENGTH_LONG).show()
            } else if (start == "0:0") {
                Toast.makeText(this, "Enter your choose", Toast.LENGTH_LONG).show()

            } else if (end == "0:0") {
                Toast.makeText(this, "Enter your choose", Toast.LENGTH_LONG).show()

            } else if ((BookSD == BookED && BookEnd <= BookStart) || (BookSD > BookED)) {
                Toast.makeText(this, "Enter correct time", Toast.LENGTH_LONG).show()
            } else {

                readCompare(start, end, st, et)
                done_btn.isEnabled = false
            }



        }


        database.child("ParkirApp").child("GARAGE").child("FCI_Garage")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    val text = p0.message
                    Toast.makeText(this@EndDate, text, Toast.LENGTH_LONG).show()
                }

                override fun onDataChange(p0: DataSnapshot) {

                    val Name = p0.child("Name").getValue(String::class.java)
                    nameend.text = Name
                }

            })





    }
    //to show that if there is an error
    fun alertcomplete() {

        val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogCustom))

        with(builder)
        {
            setTitle("choose time")
            setMessage("please choose anther time our garagr is compete at this time")
            setPositiveButton("OK", null)
            show()
        }

    }

    fun Thanks(){
        val mAlertDialog = androidx.appcompat.app.AlertDialog.Builder(this@EndDate)
        mAlertDialog.setMessage("Reservation Complete") //set alertdialog message
        mAlertDialog.setPositiveButton("Ok") { dialog, id ->
        }
        mAlertDialog.show()

    }
    //To write user
    fun writeNewUser(start: String, end: String, sd: String, ed: String,lan:Int,id:String) {
        val user = Book(start, end, sd, ed, lan,id)
         database.child("ParkirApp").child("users").child("FCI_Garage")
           .child(mAth!!.currentUser?.uid.toString()).setValue(user)
       // database.child("ParkirApp").child("users").child("FCI_Garage").push().setValue(user)
        // database.child("node").child("0").push().setValue(user)

    }
    //To compaire time in database and timechoosen
    fun Compare(
        array: ArrayList<Book>,
        start: String,
        end: String,
        sd: String,
        ed: String
    ): Boolean {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val df = DateTimeFormatter.ofPattern("d/M/yyyy", Locale.ENGLISH)
        val BookSD = LocalDate.parse(sd, df)
        val BookED = LocalDate.parse(ed, df)
        val BookStart = sdf.parse(start)
        val BookEnd = sdf.parse(end)


        var flag = true
        for (i in array!!) {
            val sdf1 = SimpleDateFormat("HH:mm", Locale.getDefault())
            val DBstart = sdf1.parse(i.StartTime!!)
            val DBend = sdf1.parse(i.EndTime!!)
            val DBsd = LocalDate.parse(i.StartDate, df)
            val DBed = LocalDate.parse(i.EndDate, df)

            if ((BookSD == DBsd) && (BookED == DBed)) {

                if ((BookStart >= DBstart && BookEnd <= DBend) ||
                    (BookStart <= DBstart && BookEnd >= DBend) ||
                    ((BookStart >= DBstart) && (BookStart <= DBend)) ||
                    ((BookStart <= DBstart) && (BookEnd >= DBstart)) ||
                    ((BookSD < BookED) &&
                            (((BookStart >= DBstart) && (BookEnd >= DBend)) ||
                                    ((BookStart <= DBstart) && (BookEnd >= DBend)) ||
                                    ((BookStart >= DBstart) && (BookStart <= DBend)) ||
                                    ((BookStart <= DBstart) && (BookEnd <= DBstart))))
                ) {
                    flag = false
                }

            } else if ((BookSD == DBsd) && (BookED != DBed)) {
                //(BookStart <= DBend )||( BookStart<= DBstart)
                if (BookSD == BookED) {
                    if (BookEnd >= DBstart) {
                        flag = false
                    }

                } else {
                    if (BookStart <= DBend) {
                        flag = false
                    }

                }
            } else if ((BookSD != DBsd) && (BookED == DBed)) {
                if (BookSD == BookED) {
                    if (BookStart <= DBend) {
                        flag = false
                    }
                } else {
                    if (BookEnd >= DBstart) {
                        flag = false
                    }
                }

            }

        }
        return flag

    }
    //To compaire booking date
    fun readCompare(start: String?, end: String, sd: String?, ed: String) {
        val user = BookNode(start, end, sd, ed)
        val flag = Compare(mData!!, start!!, end, sd!!, ed)
        if (flag == true) {
             database.child("ParkirApp").child("Book").child("FCI_Garage").child("1")
              .child(mAth!!.currentUser?.uid.toString()).setValue(user)
            //database.child("ParkirApp").child("Book").child("FCI_Garage").child("1")
                //.push().setValue(user)

             writeNewUser(start, end, sd, ed,1,mAth!!.currentUser?.uid.toString())
            //writeNewUser(start, end, sd, ed,1,"100")
            // done("one")
            Thanks()
            Handler().postDelayed({
                var i = Intent(this, Barrier::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(i)
                finish()
            }, 3000)



        } else {
            val flag1 = Compare(mData1!!, start, end, sd, ed)
            if (flag1 == true) {
                 database.child("ParkirApp").child("Book").child("FCI_Garage").child("2")
                     .child(mAth!!.currentUser?.uid.toString()).setValue(user)
                //database.child("ParkirApp").child("Book").child("FCI_Garage").child("2")
                  //  .push().setValue(user)
                //writeNewUser(start, end, sd, ed,2,"200")
                 writeNewUser(start, end, sd, ed,2,mAth!!.currentUser?.uid.toString())
                Thanks()
                Handler().postDelayed({
                    var i = Intent(this, Barrier::class.java)
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(i)
                    finish()
                }, 3000)


                //done("two")
            } else {
                val flag2 = Compare(mData2!!, start, end, sd, ed)
                if (flag2 == true) {
                    database.child("ParkirApp").child("Book").child("FCI_Garage").child("3")
                        .child(mAth!!.currentUser?.uid.toString()).setValue(user)
                //    database.child("ParkirApp").child("Book").child("FCI_Garage").child("3")
                  //      .push().setValue(user)
                    //writeNewUser(start, end, sd, ed,3,"300")
                      writeNewUser(start, end, sd, ed,3,mAth!!.currentUser?.uid.toString())
                    Thanks()
                    Handler().postDelayed({
                        var i = Intent(this, Barrier::class.java)
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(i)
                        finish()
                    }, 3000)


                    //  done("Three")
                } else {
                    alertcomplete()
                }


            }

        }
    }
}
