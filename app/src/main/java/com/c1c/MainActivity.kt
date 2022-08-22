package com.c1c

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.c1c.databinding.ActivityMainBinding
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.run {
            searchBar.setOnClickListener {
                launchActivity<SearchActivity> {  }
            }
            myRecords.setOnClickListener {
                launchActivity<MyRecordsActivity> {  }
//                val myMail: SendMail = SendMail(this@MainActivity,"henry.dev09@gmail.com","C1C", "Success Booking")
//                myMail.execute()

//                try {

//                    val myMail = MailService("acc636083@gmail.com","henry.dev09@gmail.com","C1C", "Success Booking",
//                        "Success Booking", null)
//                    myMail.sendAuthenticated()
//                } catch (e: Exception) {
//
//                }
            }
        }
    }
}