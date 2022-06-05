package com.example.audioquora

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.audioquora.databinding.ActivityNewQuestionBinding
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.Serializable

class singleans(var textlist:ArrayList<String>,var audiolink:ArrayList<String>) :Serializable
class answerslist(var ans:singleans,var name:String,var likes:Int)  :Serializable
class questions(var title:String,var vote:Int,var name:String,var answers:ArrayList<answerslist>)
class firebasedocument(var id:String,var qeus:questions)
class newQuestion : AppCompatActivity() {
    lateinit var bindnq:ActivityNewQuestionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindnq= ActivityNewQuestionBinding.inflate(layoutInflater)
        setContentView(bindnq.root)
        bindnq.updatenq.setOnClickListener{
            val db = Firebase.firestore
            val qn=questions(bindnq.enternewquestion.text.toString(),0,"abhay",ArrayList<answerslist>())
            db.collection("allquestions").add(qn)
            val intent= Intent(this,MainActivity::class.java)
            startActivity(intent)
        }

    }
}