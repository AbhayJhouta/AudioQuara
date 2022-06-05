package com.example.audioquora

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.audioquora.databinding.ActivityGiveansBinding
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


lateinit var givenansbind:ActivityGiveansBinding
class giveans : AppCompatActivity() {

    lateinit var db:FirebaseFirestore
    var recur=0;
    lateinit var quesid:String
    lateinit var audiolist:ArrayList<String>
    lateinit var textlist:ArrayList<TextView>
    lateinit var textstrlist:ArrayList<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        givenansbind= ActivityGiveansBinding.inflate(layoutInflater)
        setContentView(givenansbind.root)
        initilise()
         quesid=intent.getStringExtra("quesid").toString()
         db = Firebase.firestore
        setquestion(quesid.toString())
        setspeak()
        setupload()
    }
    fun setquestion(doc:String)
    {
        val docRef = db.collection("allquestions").document(doc)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    givenansbind.gaQuestion.text="Q."+document.data?.get("title").toString()
                } else {
                    Log.d("error", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("error", "get failed with ", exception)
            }
    }
    fun initilise()
    {
        audiolist=ArrayList<String>()
        textstrlist=ArrayList<String>()
        textlist=ArrayList<TextView>()
        textlist.add(givenansbind.gaFirst)

    }
    fun setspeak()
    {
        var mediaRecorder: MediaRecorder? = null
         var state: Boolean = false
        var rec:String="F"
        var output:String=""
        permission()
        givenansbind.gaSpeak.setOnClickListener(View.OnClickListener {
            if(rec=="F")
            {
                output = this.getExternalFilesDir(null)?.absolutePath+"/recording$recur.mp3"
                recur++
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    mediaRecorder = MediaRecorder(applicationContext)
                }
                else
                    mediaRecorder = MediaRecorder()
                Log.d("erroru","st")
                mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
                mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                mediaRecorder?.setOutputFile(output)
                try{
                    mediaRecorder?.prepare()
                }
                catch (e:Exception)
                {
                    Log.d("erroru","failed prepare");
                }
                try {
                mediaRecorder?.start();
            } catch (e: java.lang.Exception) {
                Log.d("erroru", "not started recording");
            }
                rec="O"
            }
            else
            {
                try {
                    mediaRecorder?.stop();
                    mediaRecorder?.release()
                } catch (e:Exception) {
                    Log.d("erroru", "not stoped recording");
                }
                audiolist.add(output)
                rec="F"
                val im:ImageButton= ImageButton(this)
                val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 150)
                im.setLayoutParams(layoutParams)
                im.setBackgroundColor(Color.WHITE)
                im.setImageResource(R.drawable.audio)
                im.setOnClickListener{
                    val now=recur-1
                    val player = MediaPlayer().apply {
                        try {
                            setDataSource(audiolist[now])
                            prepare()
                            start()
                        } catch (e: Exception) {
                            Log.e("erroru", audiolist[now].toString())
                        }
                    }
                }
                givenansbind.llay.addView(im)
                val tv=EditText(this)
                tv.hint="write more"
                tv.setBackgroundColor(Color.WHITE)
                textlist.add(tv)
                givenansbind.llay.addView(tv)
            }

        })
    }
    fun permission()
    {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            val permissions = arrayOf(android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE)
            ActivityCompat.requestPermissions(this, permissions,0)
        }
    }
    fun setupload()
    {  givenansbind.gaUpload.setOnClickListener{
        for(tv in textlist)
            textstrlist.add(tv.text.toString())
       val audiolink=ArrayList<String>()
        for(ad in audiolist) {
            val storage = Firebase.storage
            val ref = storage.reference
            val dref = ref.child("allvoice")
            val randomString = UUID.randomUUID().toString().substring(0,10)
            val audref = dref.child(randomString.toString()+".mp3")
            var file = Uri.fromFile(File(ad))
            Log.d("fg",ad)
            val uploadTask= audref.putFile(file)
            val path=audref.toString()
            audiolink.add(path)
            uploadTask.addOnFailureListener{
                 Toast.makeText(this,"audio upload  failed",Toast.LENGTH_SHORT).show()
            }
        }
        val uptask=db.collection("allquestions").document(quesid).update("answers",
            FieldValue.arrayUnion(answerslist(singleans(textstrlist,audiolink),"ajay",0)))
        Toast.makeText(this,"Wait till success confirmation",Toast.LENGTH_SHORT).show()
        uptask.addOnSuccessListener {
            Toast.makeText(this,"upload success",Toast.LENGTH_SHORT).show()
            val intent=Intent(this,MainActivity::class.java)
            startActivity(intent)
        }.addOnFailureListener {
            Toast.makeText(this,"upload failed",Toast.LENGTH_SHORT).show()
        }
      }



    }
}