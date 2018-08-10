package com.example.mrtayyab.webapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)


        val i = Intent(applicationContext, StartActivity::class.java)
        startActivity(i)
        finish()
    }
}
