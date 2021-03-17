package com.example.project;


import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import android.widget.TextView
import java.util.*
import android.app.DialogFragment
import android.content.DialogInterface
import android.widget.Toast
import com.example.project.R


class DatePickerFragment  : DialogFragment(), DatePickerDialog.OnDateSetListener {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c: Calendar = Calendar.getInstance()
        val year: Int = c.get(Calendar.YEAR)
        val month: Int = c.get(Calendar.MONTH)
        val day: Int = c.get(Calendar.DAY_OF_MONTH)
        return DatePickerDialog(getActivity()!!, this, year, month, day)
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        val tv: TextView = activity!!.findViewById(R.id.txt_date_picker) as TextView
        val m =month+1
        tv.setText("$day/$m/$year")
    }

    override fun onCancel(dialog: DialogInterface?) {
        Toast.makeText(activity,"date picker Canceled.", Toast.LENGTH_SHORT).show()
        super.onCancel(dialog)

    }
}