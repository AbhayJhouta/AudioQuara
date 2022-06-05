package com.example.audioquora

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PackageManagerCompat.LOG_TAG
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.audioquora.databinding.ActivityMainBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.common.primitives.UnsignedBytes.toInt
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.model.Document
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.io.IOException

class MainActivity : AppCompatActivity() {
    lateinit var ask:Button
    var ACTIVITY_REQUEST_CODE=100
    lateinit var bindmain:ActivityMainBinding
    lateinit var storage:FirebaseStorage
    lateinit var alldocuments:ArrayList<firebasedocument>
    lateinit var recveiw : RecyclerView
    lateinit var adapter: mainrecyclerview
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindmain= ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindmain.root)
        buttoncontrols()
        val db = Firebase.firestore

        db.collection("allquestions")
            .get()
            .addOnSuccessListener { result ->
                alldocuments=(snaptoarray(result))
                recveiw=findViewById(R.id.recview)
                recveiw.setHasFixedSize(true)
                recveiw.isNestedScrollingEnabled=true
                recveiw.layoutManager = LinearLayoutManager(this)
                adapter= mainrecyclerview(alldocuments,this)
                recveiw.adapter=adapter
            }
            .addOnFailureListener { exception ->
                Log.d("error", "Error getting documents: ", exception)
            }
    }
    fun buttoncontrols()
    {
        bindmain.ask.setOnClickListener(View.OnClickListener {
            val intent= Intent(this,newQuestion::class.java)
            startActivity(intent)
        })
    }
    fun snaptoarray(alldocuments:QuerySnapshot):ArrayList<firebasedocument>
    { val al=ArrayList<firebasedocument>()
        for(d in alldocuments)
        {    val doc=d.data

           al.add(firebasedocument(d.id,questions(doc.get("title").toString(),(doc.get("vote") as Long).toInt(),doc.get("name").toString(),anytosnwerlist(doc.get("answers")))))
        }

        return al
    }
    fun anytosnwerlist(tmp:Any?):ArrayList<answerslist>
    {
        val hmp=tmp as ArrayList<HashMap<String,HashMap<String,ArrayList<String>>>>
      val anslist=ArrayList<answerslist>()
        for(i in 0..hmp.size-1)
        {
            val tempsin=singleans(hmp[i]["ans"]?.get("textlist")!!,hmp[i]["ans"]?.get("audiolink")!!)
            val tempal=answerslist(tempsin,hmp[i]["name"].toString(),(hmp[i]["likes"]as Long).toInt())
            anslist.add(tempal)
        }

      return anslist
    }

}