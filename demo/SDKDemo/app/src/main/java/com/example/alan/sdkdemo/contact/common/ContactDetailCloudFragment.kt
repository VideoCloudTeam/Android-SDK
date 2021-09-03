package com.example.alan.sdkdemo.contact.common

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.alan.sdkdemo.R
import com.example.alan.sdkdemo.contact.ContactActivity
import com.example.alan.sdkdemo.contact.ContactBean
import com.example.alan.sdkdemo.contact.HttpUtil
import com.example.alan.sdkdemo.contact.SPUtil
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject

class ContactDetailCloudFragment : Fragment() {
    private lateinit var tvName: TextView
    private lateinit var tvDepartment: TextView
    private lateinit var tvPosition: TextView
    private lateinit var tvNumber: TextView
    private var bean: ContactBean? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            bean = it.getSerializable("bean") as ContactBean?
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_contact_detail, container, false)
        rootView.apply {
            tvDepartment = findViewById(R.id.tv_depart)
            tvName = findViewById(R.id.tv_name)
            tvPosition = findViewById(R.id.tv_position)
            tvNumber = findViewById(R.id.tv_number)
        }
        tvName.text = bean?.usrNickName
        tvDepartment.text = bean?.departFatherList?.toString()
        tvPosition.text = bean?.duties?.toString()
        tvNumber.text = bean?.sipKey
        return rootView
    }



    companion object {
        @JvmStatic
        fun newInstance(contactBean: ContactBean) =
                ContactDetailCloudFragment().apply {
                    arguments = Bundle().apply {
                        putSerializable("bean", contactBean)
                    }
                }
    }
}