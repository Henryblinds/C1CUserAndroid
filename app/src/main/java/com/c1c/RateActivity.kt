package com.c1c

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialog
import com.c1c.databinding.ActivityRateBinding
import com.google.firebase.database.*
import java.util.ArrayList

class RateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRateBinding
    var rateCoach = 5.0f
    lateinit var loadingDialog: AppCompatDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadingDialog = DialogFactory.showLoadingDialog(this@RateActivity)

        binding.run {

            imageBack.setOnClickListener {
                finish()
            }

            rate.rating = 5.0f
            rate.setOnRatingBarChangeListener { ratingBar, fl, b ->
                if(fl < 1.0f){
                    rate.rating = 1.0f
                }
                rateCoach = fl
            }

            rateSubmit.setOnClickListener {
                loadingDialog.show()
                val dataref = FirebaseDatabase.getInstance().reference
                val coachRef = dataref.child("rating/${ObjectSingleton.selectedCoach!!.coachId}")
                val key = coachRef.push().key!!
                val modelCoach = RatingModel(
                    key,
                    ObjectSingleton.selectedCoach!!.coachId,
                    ObjectSingleton.booked!!.name,
                    rateCoach.toString(),
                    textFeedBack.text!!.toString(),
                    ObjectSingleton.booked!!.date
                )
                coachRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val coachList: ArrayList<RatingModel> = arrayListOf()
                        if(snapshot.exists()) {
                            for(childSnap: DataSnapshot in snapshot.children) {
                                coachList.add(childSnap.getValue(RatingModel::class.java)!!)
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

    }

    fun saveToFirebase(ref: DatabaseReference, cList: ArrayList<RatingModel>) {
        ref.setValue(cList)
            .addOnFailureListener {e ->
                loadingDialog.dismiss()
                toastValidation("${e.message}")
            }
            .addOnSuccessListener {
                loadingDialog.dismiss()
                finish()
                toastValidation("Booked Successfully")
            }
    }


}