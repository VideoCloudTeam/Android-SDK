package com.example.alan.sdkdemo.contact

import com.zijing.xjava.sip.header.Contact

/**
 * Created by ricardo
 * 8/31/21.
 */
interface ItemClick {
    fun onItemClickListener(bean: ContactBean, isPeople: Boolean)
}