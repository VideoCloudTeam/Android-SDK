package com.example.alan.sdkdemo.contact

import com.vcrtc.utils.OkHttpUtil
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Created by ricardo
 * 8/30/21.
 */
class ExtendFunc {

    suspend fun doPost(url: String, params: Map<String, String>, header: Map<String, String>): Deferred<String> {

        return suspendCancellableCoroutine {

            val deferred = CompletableDeferred<String>()
            OkHttpUtil.doPost(url, params, header, object : Callback{
                override fun onFailure(call: Call, e: IOException) {
                    it.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    val resp = response.body()?.string()
                    resp?.let { it1 -> deferred.complete(it1) }
                    it.resume(deferred)
                }
            })
        }



    }
}