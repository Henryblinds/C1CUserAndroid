package com.c1c

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialog
import androidx.recyclerview.widget.LinearLayoutManager
import coil.api.load
import coil.transform.CircleCropTransformation
import com.c1c.ObjectSingleton.selectedCoach
import com.c1c.databinding.ActivityProfileBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    lateinit var loadingDialog: AppCompatDialog
    lateinit var profileAdapter:GenericAdapter<CoachProfileModel>
    lateinit var profileList: ArrayList<CoachProfileModel>
    lateinit var locationAdapter:GenericAdapter<CoachLocModel>
    lateinit var locationList: ArrayList<CoachLocModel>
    lateinit var ratingAdapter:GenericAdapter<RatingModel>
    lateinit var ratingList: ArrayList<RatingModel>
    lateinit var socialAdapter:GenericAdapter<CoachSocModel>
    lateinit var socialList: ArrayList<CoachSocModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadingDialog = DialogFactory.showLoadingDialog(this@ProfileActivity)
        profileList = arrayListOf()
        locationList = arrayListOf()
        ratingList = arrayListOf()
        socialList = arrayListOf()

        socialAdapter = GenericAdapter(socialList, R.layout.item_social_list, BR.coach, object : ItemClickInterface<CoachSocModel> {
            override fun onItemClick(v: View?, item: CoachSocModel, position: Int) {

            }
        })

        ratingAdapter = GenericAdapter(ratingList, R.layout.item_reviewlist, BR.coach, object : ItemClickInterface<RatingModel> {
            override fun onItemClick(v: View?, item: RatingModel, position: Int) {

            }
        })

        locationAdapter = GenericAdapter(locationList, R.layout.item_location_list, BR.coach, object : ItemClickInterface<CoachLocModel> {
            override fun onItemClick(v: View?, item: CoachLocModel, position: Int) {

            }
        })

        profileAdapter = GenericAdapter(profileList, R.layout.item_profile_list, BR.coach, object : ItemClickInterface<CoachProfileModel> {
            override fun onItemClick(v: View?, item: CoachProfileModel, position: Int) {

            }
        })

        binding.run {
            if(selectedCoach != null) {
                coach = selectedCoach
            }
            recycleProfile.apply {
                layoutManager = LinearLayoutManager(this@ProfileActivity)
                adapter = profileAdapter
            }

            recycleLocation.apply {
                layoutManager = LinearLayoutManager(this@ProfileActivity)
                adapter = locationAdapter
            }

            recycleSocial.apply {
                layoutManager = LinearLayoutManager(this@ProfileActivity)
                adapter = socialAdapter
            }

            recycleRate.apply {
                layoutManager = LinearLayoutManager(this@ProfileActivity)
                adapter = ratingAdapter
            }

            imageBack.setOnClickListener {
                finish()
            }

            bookCoach.setOnClickListener {
                launchActivity<BookingActivity> {  }
            }

            toastValidation("Loading details...")
            refreshdata()
            refreshLocdata()
            refreshRatingdata()
            refreshSocdata()
            getProfilePic()
        }
    }

    fun getProfilePic() {
        val dataref = FirebaseDatabase.getInstance().reference
        val profileRef = dataref.child("photos/${selectedCoach!!.coachId}")
        profileRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    val storage = Firebase.storage
                    val storageRef = storage.reference


                    val coachPhoto = snapshot.getValue(CoachPhoto::class.java)!!

                    val pathReference = storageRef.child("images/${coachPhoto.imagefile}")
//                    toastValidation(realData.imagefile)
                    pathReference.downloadUrl.addOnSuccessListener {
                        binding.profilepic.visibility = View.VISIBLE
                        binding.profilepic.load(it) {
                            transformations(CircleCropTransformation())
                        }
//                        Glide.with(this@CoachActivity)
//                            .load(it)
//                            .into(binding.profilePic)
                    }


                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    fun refreshdata() {
        val dataref = FirebaseDatabase.getInstance().reference
        val profileRef = dataref.child("coachprofile/${selectedCoach!!.coachId}")
        profileRef.addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                profileList = arrayListOf()
                val mList: ArrayList<CoachProfileModel> = arrayListOf()
                if(snapshot.exists()) {
                    for(childSnap: DataSnapshot in snapshot.children) {
                        mList.add(childSnap.getValue(CoachProfileModel::class.java)!!)
                    }
                    profileAdapter.notifyDataSetChanged(mList)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    fun refreshLocdata() {
        val dataref = FirebaseDatabase.getInstance().reference
        val profileRef = dataref.child("coachlocation/${selectedCoach!!.coachId}")
        profileRef.addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                locationList = arrayListOf()
                val mList: ArrayList<CoachLocModel> = arrayListOf()
                if(snapshot.exists()) {
                    for(childSnap: DataSnapshot in snapshot.children) {
                        mList.add(childSnap.getValue(CoachLocModel::class.java)!!)
                    }
                    locationAdapter.notifyDataSetChanged(mList)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    fun refreshSocdata() {
        val dataref = FirebaseDatabase.getInstance().reference
        val profileRef = dataref.child("coachsocial/${selectedCoach!!.coachId}")
        profileRef.addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                socialList = arrayListOf()
                val mList: ArrayList<CoachSocModel> = arrayListOf()
                if(snapshot.exists()) {
                    for(childSnap: DataSnapshot in snapshot.children) {
                        mList.add(childSnap.getValue(CoachSocModel::class.java)!!)
                    }
                    socialAdapter.notifyDataSetChanged(mList)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    fun refreshRatingdata() {
        val dataref = FirebaseDatabase.getInstance().reference
        val profileRef = dataref.child("rating/${selectedCoach!!.coachId}")
        profileRef.addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                ratingList = arrayListOf()
                val mList: ArrayList<RatingModel> = arrayListOf()
                if(snapshot.exists()) {
                    for(childSnap: DataSnapshot in snapshot.children) {
                        val mModel:RatingModel = childSnap.getValue(RatingModel::class.java)!!
                        mModel.rate = mModel.rate.take(1)
                        mList.add(mModel)
                    }
                    if(mList.size > 0) {
                        binding.noReview.visibility = View.GONE
                    }
                    ratingAdapter.notifyDataSetChanged(mList)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun onResume() {
        super.onResume()
        if(ObjectSingleton.closeActivity) {
            finish()
        }
    }
}