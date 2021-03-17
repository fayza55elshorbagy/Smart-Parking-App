package com.example.project;

import android.app.Dialog
import android.app.DialogFragment
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.TextView
import android.widget.TimePicker
import java.util.*


class TimePickerFragment2 : DialogFragment(), TimePickerDialog.OnTimeSetListener{
    private lateinit var calendar:Calendar



  /*  override fun onStart() {
        super.onStart()
        (dialog as TimePickerDialog)
            .getButton(DialogInterface.BUTTON_NEGATIVE)
            .setText(R.string.myCancelString)
    }*/


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        return TimePickerDialog(
            activity, // Context
            android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth, // Theme
            this, // TimePickerDialog.OnTimeSetListener
            hour, // Hour of day
            minute, // Minute
            false // Is 24 hour view
        )//.apply {
        //  getButton(DialogInterface.BUTTON_NEGATIVE).setText(R.string.myCancelString)
        // }


    }


    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        // Do something with the returned time

        val tv:TextView = activity.findViewById(R.id.endd) as TextView
        //  tv.text = "${getHourAMPM(hourOfDay)}:$minute ${getAMPM(hourOfDay)}"
        tv.text = "$hourOfDay:$minute "
    }




    // When user cancel the time picker dialog
    override fun onCancel(dialog: DialogInterface?) {
        super.onCancel(dialog)
        val tv:TextView = activity.findViewById(R.id.endd) as TextView
       tv.text = "0:0"
    }


    // Custom method to get AM PM value from provided hour
    private fun getAMPM(hour:Int):String{
        return if(hour>11)"PM" else "AM"
    }


    // Custom method to get hour for AM PM time format
    private fun getHourAMPM(hour:Int):Int{
        // Return the hour value for AM PM time format
        var modifiedHour = if (hour>11)hour-12 else hour
        if (modifiedHour == 0){modifiedHour = 12}
        return modifiedHour
    }


}