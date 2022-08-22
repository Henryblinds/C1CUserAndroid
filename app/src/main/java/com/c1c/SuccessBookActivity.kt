package com.c1c

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.c1c.ObjectSingleton.closeActivity
import com.c1c.ObjectSingleton.fromRecords
import com.c1c.databinding.ActivityProfileBinding
import com.c1c.databinding.ActivitySuccessBookBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.ArrayList

class SuccessBookActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySuccessBookBinding
    lateinit var ratingList: ArrayList<RatingModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySuccessBookBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ratingList = arrayListOf()
        binding.run {
            nameCoach.text = if(fromRecords) {
                "Book Details"
            } else "Booking Successful"

            if(ObjectSingleton.selectedCoach != null) {
                coach = ObjectSingleton.selectedCoach
            }
            if(ObjectSingleton.booked != null) {
                booked = ObjectSingleton.booked
            }

            rateBtn.setOnClickListener {
                launchActivity<RateActivity> {  }
            }

            doneBtn.setOnClickListener {
                if(fromRecords) {
                    fromRecords = false
                } else {
                    closeActivity = true
                }
                finish()
            }

            val intent = Intent(Intent.ACTION_SEND)
//            intent.setType("text/plain")
            intent.data = Uri.parse("mailto:")
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(ObjectSingleton.selectedCoach!!.email))
            intent.putExtra(Intent.EXTRA_SUBJECT, "C1C App Booking")
            intent.putExtra(
                Intent.EXTRA_TEXT, "Hi Coach ${ObjectSingleton.selectedCoach!!.name},\n\n" +
                    "I'd like to book a session with you on ${ObjectSingleton.booked!!.date}, Please confirm your availability. Looking forward to your response \n" +
                    "Name: ${ObjectSingleton.booked!!.name} \n" +
                    "Email: ${ObjectSingleton.booked!!.email} \n" +
                    "Mobile: ${ObjectSingleton.booked!!.number} \n\n" +
                    "Thank you")

//            intent.setType("message/rfc822")
            startActivity(Intent.createChooser(intent, "Send Email"))
        }

    }

    override fun onResume() {
        super.onResume()
        refreshRatingdata()
    }

    fun refreshRatingdata() {
        val dataref = FirebaseDatabase.getInstance().reference
        val profileRef = dataref.child("rating/${ObjectSingleton.selectedCoach!!.coachId}")
        profileRef.addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                ratingList = arrayListOf()
                val mList: ArrayList<RatingModel> = arrayListOf()
                if(snapshot.exists()) {
                    for(childSnap: DataSnapshot in snapshot.children) {
                        mList.add(childSnap.getValue(RatingModel::class.java)!!)
                    }
                    ratingList = mList
                    if(ratingList.isNotEmpty()) {
                        var isRated = false
                        for(i: RatingModel in ratingList) {
                            if(i.coachId == ObjectSingleton.selectedCoach!!.coachId) {
                                if(i.dateBook == ObjectSingleton.booked!!.date) {
                                    isRated = true
                                }
                            }
                        }

                        if(fromRecords) {
                            if(!isRated) {
                                binding.rateBtn.visibility = View.VISIBLE
                            }
                        } else {
                            binding.rateBtn.visibility = View.GONE
                        }
                    }
                } else {
                    binding.rateBtn.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}