package com.c1c

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialog
import androidx.core.widget.doAfterTextChanged
import com.c1c.ObjectSingleton.selectedCoach
import com.c1c.databinding.ActivitySearchBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    lateinit var coachList: ArrayList<CoachModel>
    lateinit var coachOroginalList: ArrayList<CoachModel>
    lateinit var coachListFiltered: ArrayList<CoachModel>
    lateinit var coachAdapter: GenericAdapter<CoachModel>
    lateinit var loadingDialog: AppCompatDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        coachList = arrayListOf()
        coachListFiltered = arrayListOf()
        coachOroginalList = arrayListOf()
        coachAdapter = GenericAdapter(coachList, R.layout.list_item_coaches, BR.coach, object :
            ItemClickInterface<CoachModel> {
            override fun onItemClick(v: View?, item: CoachModel, position: Int) {
                selectedCoach = item
                launchActivity<ProfileActivity> {  }
            }
        })
        binding.run {
            toastValidation("loading...")
            getCoaches()
            recycleList.adapter = coachAdapter
            searchCoach.doAfterTextChanged {
                if(coachList.isNotEmpty()) {
                    var filtered:ArrayList<CoachModel> = arrayListOf()
                    val sItem = searchCoach.text.toString().toLowerCase()
                    if(sItem.length > 0) {
                        for(item:CoachModel in coachList) {
                            if(item.name.toLowerCase().contains(sItem)) {
                                filtered.add(item)
                            }
                        }
                        coachOroginalList = filtered
                    } else {
                        coachOroginalList = coachList
                    }
                    coachAdapter.notifyDataSetChanged(coachOroginalList)
                }
            }
        }
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
                    coachAdapter.notifyDataSetChanged(coachList)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun onResume() {
        super.onResume()
        if(ObjectSingleton.closeActivity) {
            ObjectSingleton.closeActivity = false
            finish()
        }
    }
}