package com.example.alan.sdkdemo.contact

import android.content.Context
import android.content.SharedPreferences

/**
 * Created by ricardo
 * 8/30/21.
 */
class SPUtil {


    companion object{
        private var sp: SharedPreferences? = null
        fun instance(context: Context): SPUtil {
            if (sp == null) {
                sp = context.getSharedPreferences("data", Context.MODE_PRIVATE)
            }
            return SPUtil()
        }
    }



    fun getSessionId(): String {
        return sp?.getString("sessionId", "")!!
    }

    fun setSessionId(sessionId: String){
        val edit = sp!!.edit()
        edit.putString("sessionId", sessionId)
        edit.apply()
    }

    fun isLogin():Boolean {
        return sp?.getBoolean("login", false)!!
    }

    fun setLogin(isLogin: Boolean){
        val edit = sp!!.edit()
        edit.putBoolean("login", isLogin)
        edit.apply()
    }
}