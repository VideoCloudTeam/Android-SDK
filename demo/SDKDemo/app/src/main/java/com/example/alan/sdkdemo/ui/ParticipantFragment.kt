package com.example.alan.sdkdemo.ui

import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ItemDecoration
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.alan.sdkdemo.R
import com.vcrtc.VCRTC
import com.vcrtc.entities.ConferenceStatus
import com.vcrtc.entities.Participant
import com.vcrtc.entities.Stage
import kotlinx.coroutines.*
import okhttp3.Dispatcher
import java.lang.Exception

class ParticipantFragment : Fragment(), ZJConferenceActivity.ConferenceCallBack {
    private var vc: VCRTC? = null
    private var dataList: MutableList<Participant> = mutableListOf()
    private var participant: Map<String, Participant>? = null
    private var uuid: String? = null
    private var adapter: ParticipantAdapter? = null
    private lateinit var recycler: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_participant, container, false)
        (activity as ZJConferenceActivity).apply {
            uuid = this.myUUID
            vc = this.vcrtc
            setConferenceCallBack(this@ParticipantFragment)
        }
        rootView.findViewById<ImageView>(R.id.iv_back).setOnClickListener { activity?.supportFragmentManager?.popBackStack() }
        initRecycler(rootView)
        participant = vc?.participants
        GlobalScope.launch(Dispatchers.IO){
            handleDataListAsync().await()
            refreshView()
        }

        return rootView
    }

    private fun initRecycler(rootView: View) {
        recycler = rootView.findViewById(R.id.recycler)
        adapter = ParticipantAdapter(mutableListOf(), activity!!)
        recycler.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
        recycler.adapter = adapter
    }

    @Synchronized
    private fun handleDataListAsync(): Deferred<Boolean> {
        dataList.clear()
        val deferred = CompletableDeferred<Boolean>()
        try {
            participant?.filter { !it.value.display_name.startsWith("_Cloud_robot_") }?.forEach{
                if (uuid == it.value.uuid){
                    dataList.add(0, it.value)
                }else{
                    dataList.add(it.value)
                }
            }
            deferred.complete(true)
        }catch (e: Exception){
            deferred.complete(false)
        }
        return deferred
    }

    private suspend fun refreshView() = withContext(Dispatchers.Main){
        adapter?.updateList(dataList)
    }

    companion object {

        @JvmStatic
        fun newInstance() = ParticipantFragment()
    }

    private val handleDataRunnable = Runnable {
        GlobalScope.launch(Dispatchers.IO){
            val hasHandle = handleDataListAsync().await()
            if (hasHandle){
                refreshView()
            }
        }
    }

    private val handler = Handler()



    override fun onAddParticipant(participant: Participant?) {
        handler.apply { removeCallbacks(handleDataRunnable) }.postDelayed(handleDataRunnable, 500)
    }

    override fun onUpdateParticipant(participant: Participant?) {
        handler.apply { removeCallbacks(handleDataRunnable) }.postDelayed(handleDataRunnable, 500)
    }

    override fun onRemoveParticipant(uuid: String?) {
        handler.apply { removeCallbacks(handleDataRunnable) }.postDelayed(handleDataRunnable, 500)
    }

    override fun onStageVoice(stages: MutableList<Stage>?) {
    }

    override fun onConferenceUpdate(status: ConferenceStatus?) {

    }

    override fun onDestroy() {
        handler.removeCallbacks(handleDataRunnable)
        super.onDestroy()
    }
}