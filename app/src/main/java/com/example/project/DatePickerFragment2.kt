package com.example.project;

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.DialogFragment
import android.content.DialogInterface
import android.os.Bundle
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import com.example.project.R
import java.util.*

class DatePickerFragment2: DialogFragment(), DatePickerDialog.OnDateSetListener  {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c: Calendar = Calendar.getInstance()
        val year: Int = c.get(Calendar.YEAR)
        val month: Int = c.get(Calendar.MONTH)
        val day: Int = c.get(Calendar.DAY_OF_MONTH)
        return DatePickerDialog(getActivity()!!, this, year, month, day)
    }
    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        val tv: TextView = activity!!.findViewById(R.id.txt_date_picker1) as TextView
        val m =p2+1
        tv.setText("$p3/$m/$p1")
    }

    override fun onCancel(dialog: DialogInterface?) {
        Toast.makeText(activity,"date picker Canceled.", Toast.LENGTH_SHORT).show()
        super.onCancel(dialog)
        val tv:TextView = activity.findViewById(R.id.txt_date_picker1) as TextView
        tv.text = "DD-MM-YYYY"
    }
}