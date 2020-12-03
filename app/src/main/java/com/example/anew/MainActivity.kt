package com.example.anew

import android.Manifest
import android.content.Intent
import android.icu.text.DateFormat.Field.MILLISECOND
import android.icu.util.Calendar.MILLISECOND
import android.icu.util.TimeUnit
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.MILLISECOND

class MainActivity : AppCompatActivity() {
    lateinit var stateAdapter: StateAdapter
    lateinit var t : TextView
    private var permission = (Manifest.permission.ACCESS_FINE_LOCATION)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        list.addHeaderView(LayoutInflater.from(this).inflate(R.layout.item_header,list,false))
        fetchResults()
        
        //checkPermission(permission)

        t = findViewById<TextView>(R.id.tv)
        t.setSingleLine()
        t.ellipsize =TextUtils.TruncateAt.MARQUEE
        t.marqueeRepeatLimit = -1
        t.isSelected=true

        btn.setOnClickListener(View.OnClickListener {
            var mainIntent = Intent(this, MainActivity3::class.java)
            startActivity(mainIntent)
        })


    }
    private fun fetchResults(){
        GlobalScope.launch {
            val response = withContext(Dispatchers.IO) { CLIENT.api.execute() }
            if(response.isSuccessful){
                val data = Gson().fromJson(response.body?.string(),Response::class.java)
                launch(Dispatchers.Main) {
                    bindCombinedData(data.statewise[0])
                    bindStateWiseData(data.statewise.subList(0,data.statewise.size))
                }
            }
        }
    }


    private fun bindStateWiseData(subList: List<StatewiseItem> ){
    stateAdapter  = StateAdapter(subList)
        list.adapter = stateAdapter


    }
    
    
    
    
    

    private fun bindCombinedData(data:StatewiseItem){
    val lastUpdateTime = data.lastupdatedtime
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
        lastUpdatedTv.text="Last Updated\n ${getTimeAgo(simpleDateFormat.parse(lastUpdateTime))}"
        confirmedTv.text = data.confirmed
        recoveredTv.text = data.recovered
        activeTv.text = data.active
        deathTv.text = data.deaths

    }

    fun getTimeAgo(past: Date): String {
        val now = Date()
        val seconds = java.util.concurrent.TimeUnit.MILLISECONDS.toSeconds(now.time - past.time)
        val minutes = java.util.concurrent.TimeUnit.MILLISECONDS.toMinutes(now.time - past.time)
        val hours = java.util.concurrent.TimeUnit.MILLISECONDS.toHours(now.time - past.time)

        return when {
            seconds < 60 -> {
                "Few seconds ago"
            }
            minutes < 60 -> {
                "$minutes minutes ago"
            }
            hours < 24 -> {
                "$hours hour ${minutes % 60} min ago"
            }
            else -> {
                SimpleDateFormat("dd/MM/yy, hh:mm a").format(past).toString()
            }
        }
    }



}