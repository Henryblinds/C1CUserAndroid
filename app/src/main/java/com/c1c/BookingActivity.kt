package com.c1c

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialog
import androidx.core.content.ContextCompat
import com.c1c.databinding.ActivityBookingBinding
import com.google.firebase.database.*
import com.google.firebase.database.R
import java.util.*

class BookingActivity : AppCompatActivity() , DatePickerDialog.OnDateSetListener {
    private lateinit var binding:ActivityBookingBinding
    lateinit var loadingDialog: AppCompatDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadingDialog = DialogFactory.showLoadingDialog(this@BookingActivity)
        binding.run {
            if(ObjectSingleton.selectedCoach != null) {
                coach = ObjectSingleton.selectedCoach
            }
            imageBack.setOnClickListener {
                finish()
            }

            textDate.setOnClickListener {
                openDob()
            }

            bookCoach.setOnClickListener {
                validate()
            }
        }
    }

    private fun openDob() {
        val calendar: Calendar = Calendar.getInstance()
//        calendar.add(Calendar.YEAR,-18)
        val datePicker: DatePickerDialog = DatePickerDialog(this@BookingActivity, AlertDialog.THEME_HOLO_LIGHT,this,
            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH))

        datePicker.show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar[Calendar.YEAR] = year
        calendar[Calendar.MONTH] = month
        calendar[Calendar.DAY_OF_MONTH] = dayOfMonth
        var mon = month + 1
        var monstr = "$mon"
        var daystr = "$dayOfMonth"
        if(mon < 10) {
            monstr  = "0$mon"
        }
        if(dayOfMonth < 10) {
            daystr = "0$dayOfMonth"
        }
        val bday = "$monstr/$daystr/${year}"
        binding.textDate.setText(bday)
    }

    fun validate() {

        binding.run {
            if(textName.text!!.isEmpty() ||
                textEmail.text!!.isEmpty() ||
                textNumber.text!!.isEmpty() ||
                textDate.text!!.isEmpty()) {
                toastValidation("All fields required.")
            } else {
                proceedSubmit()
            }
        }
    }

    fun proceedSubmit() {
        loadingDialog.show()
        val dataref = FirebaseDatabase.getInstance().reference
        val coachRef = dataref.child("booking")
        val key = coachRef.push().key!!
        binding.run {
            val modelCoach = BookingModel(
                key,
                ObjectSingleton.selectedCoach!!.coachId,
                textName.text!!.toString(),
                textEmail.text!!.toString(),
                textNumber.text!!.toString(),
                textDate.text!!.toString(),
                textNote.text!!.toString()
            )
            coachRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val coachList: ArrayList<BookingModel> = arrayListOf()
                    ObjectSingleton.booked = modelCoach
                    if(snapshot.exists()) {
                        for(childSnap: DataSnapshot in snapshot.children) {
                            coachList.add(childSnap.getValue(BookingModel::class.java)!!)
                        }
                        coachList.add(modelCoach)
                        saveToFirebase(coachRef, coachList)
                    } else {
                        coachList.add(modelCoach)
                        saveToFirebase(coachRef, coachList)
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })

        }

    }

    fun saveToFirebase(ref: DatabaseReference, cList: ArrayList<BookingModel>) {
        ref.setValue(cList)
            .addOnFailureListener {e ->
                loadingDialog.dismiss()
                ObjectSingleton.booked = null
                toastValidation("${e.message}")
            }
            .addOnSuccessListener {
                loadingDialog.dismiss()
                launchActivity<SuccessBookActivity> {  }
                finish()
                toastValidation("Booked Successfully")
            }
    }
}