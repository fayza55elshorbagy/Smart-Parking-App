package com.badr.book

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.start_date.*


class StartDate : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.start_date)

        database = FirebaseDatabase.getInstance().reference
        txt_date_picker.setOnClickListener {
            val mDatePicker = DatePickerFragment()
            mDatePicker.show(this.fragmentManager, "Date Picker")
        }

        startt.setOnClickListener {
            // Initialize a new TimePickerFragment
            val newFragment = TimePickerFragment()
            // Show the time picker dialog
            newFragment.show(this.fragmentManager, "Time Picker")
        }
        database.child("ParkirApp").child("GARAGE").child("FCI_Garage")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    val text = p0.message
                    Toast.makeText(this@StartDate, text, Toast.LENGTH_LONG).show()
                }

                override fun onDataChange(p0: DataSnapshot) {

                    val Name = p0.child("Name").getValue(String::class.java)
                    name.text = Name
                }

            })


        Continue_btn.setOnClickListener {
            var i_endDate = Intent(this, EndDate::class.java)
            i_endDate.putExtra("startTme",startt.text.toString())
            i_endDate.putExtra("startDate",txt_date_picker.text.toString())
            startActivity(i_endDate)
        }

    }
}
