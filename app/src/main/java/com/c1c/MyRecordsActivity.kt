package com.c1c

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialog
import androidx.core.widget.doAfterTextChanged
import com.c1c.ObjectSingleton.fromRecords
import com.c1c.databinding.ActivityMyRecordsBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MyRecordsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyRecordsBinding
    lateinit var locationAdapter:GenericAdapter<RecordModel>
    lateinit var locationList: ArrayList<RecordModel>
    lateinit var coachList: ArrayList<CoachModel>
    lateinit var bookingList: ArrayList<BookingModel>
    lateinit var locationListFiltered: ArrayList<RecordModel>
    lateinit var loadingDialog: AppCompatDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyRecordsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        coachList = arrayListOf()
        locationList = arrayListOf()
        bookingList = arrayListOf()
        locationListFiltered = arrayListOf()
        loadingDialog = DialogFactory.showLoadingDialog(this@MyRecordsActivity)
        locationAdapter = GenericAdapter(locationList, R.layout.list_item_records, BR.coach, object : ItemClickInterface<RecordModel> {
            override fun onItemClick(v: View?, item: RecordModel, position: Int) {
                ObjectSingleton.selectedCoach = item.coach
                ObjectSingleton.booked = item.booked
                fromRecords = true
                launchActivity<SuccessBookActivity> {  }
            }
        })
        getCoaches()
        getRecords()
        binding.run {
            recycleList.adapter = locationAdapter
            searchCoach.doAfterTextChanged {
                if(coachList.isNotEmpty()) {
                    if(bookingList.isNotEmpty()) {
                        var filtered:ArrayList<RecordModel> = arrayListOf()
                        val sItem = searchCoach.text.toString().toLowerCase()
                        if(sItem.length > 6) {
                            for(item:BookingModel in bookingList) {
                                if(item.email.toLowerCase().contains(sItem)) {
                                    for(i:CoachModel in coachList) {
                                        if(i.coachId == item.coachId) {
                                            filtered.add(RecordModel(
                                                i, item
                                            ))
                                        }
                                    }
                                }
                            }
                            locationListFiltered = filtered
                        } else {
                            locationListFiltered = arrayListOf()
                        }
                        locationAdapter.notifyDataSetChanged(locationListFiltered)
                    }
                }
            }
        }

    }
    fun getRecords() {
        val dataref = FirebaseDatabase.getInstance().reference
        val profileRef = dataref.child("booking")
        profileRef.addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                bookingList = arrayListOf()
                val mList: ArrayList<BookingModel> = arrayListOf()
                if(snapshot.exists()) {
                    for(childSnap: DataSnapshot in snapshot.children) {
                        mList.add(childSnap.getValue(BookingModel::class.java)!!)
                    }
                    bookingList = mList
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    fun getCoaches() {
        val dataref = FirebaseDatabase.getInstance().reference
        val profileRef = dataref.child("coaches")
        profileRef.addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                coachList = arrayListOf()
                val mList: ArrayList<CoachModel> = arrayListOf()
                if(snapshot.exists()) {
                    for(childSnap: DataSnapshot in snapshot.children) {
                        mList.add(childSnap.getValue(CoachModel::class.java)!!)
                    }
                    coachList = mList
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }


}