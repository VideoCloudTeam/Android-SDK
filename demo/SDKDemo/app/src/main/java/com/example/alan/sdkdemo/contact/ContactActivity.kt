package com.example.alan.sdkdemo.contact

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.example.alan.sdkdemo.R
import com.example.alan.sdkdemo.contact.cloud.ContactCloudFragment
import com.example.alan.sdkdemo.contact.cloud.ContactDetailFragment
import com.example.alan.sdkdemo.contact.cloud.SearchCloudFragment
import com.vcrtc.VCRTCPreferences

class ContactActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var flContainer: FrameLayout
    lateinit var vcrp: VCRTCPreferences
    private val fragments = mutableListOf<Fragment>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cantact)
        flContainer = findViewById(R.id.fl_content)
        vcrp = VCRTCPreferences(this)
        fragments.add(ContactCloudFragment.newInstance(""))
        supportFragmentManager.beginTransaction()
                .add(R.id.fl_content, fragments[0], "first")
                .addToBackStack(null)
                .commitAllowingStateLoss()
        findViewById<ImageView>(R.id.iv_back).setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.iv_back -> {
                finish()
            }
        }
    }

    fun showNextDepartment(id: String){
        supportFragmentManager.beginTransaction()
                .replace(R.id.fl_content, ContactCloudFragment.newInstance(id), "")
                .addToBackStack(null)
                .commitAllowingStateLoss()
    }

    fun showContactDetail(id: String){
        supportFragmentManager.beginTransaction()
                .replace(R.id.fl_content, ContactDetailFragment.newInstance(id), "")
                .addToBackStack(null)
                .commitAllowingStateLoss()
    }

    fun showContactSearch(){
        supportFragmentManager.beginTransaction()
                .replace(R.id.fl_content, SearchCloudFragment.newInstance(), "")
                .addToBackStack(null)
                .commitAllowingStateLoss()
    }
}