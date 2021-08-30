package com.example.alan.sdkdemo.contact

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import com.example.alan.sdkdemo.R
import com.example.alan.sdkdemo.contact.cloud.ContactCloudFragment

class ContactActivity : AppCompatActivity() {
    private lateinit var flContainer: FrameLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cantact)
        flContainer = findViewById(R.id.fl_content)
        supportFragmentManager.beginTransaction()
                .add(R.id.fl_content, ContactCloudFragment.newInstance(), "first")
                .addToBackStack(null)
                .commitAllowingStateLoss()
    }
}