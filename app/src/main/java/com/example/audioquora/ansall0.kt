package com.example.audioquora

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.audioquora.databinding.ActivityAnsall0Binding

class ansall0 : AppCompatActivity() {
    lateinit var ansallbind:ActivityAnsall0Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ansallbind= ActivityAnsall0Binding.inflate(layoutInflater)
        setContentView(ansallbind.root)
        val args = intent.getBundleExtra("BUNDLE")
        val anslist= args!!.getSerializable("ARRAYLIST") as ArrayList<answerslist>
        val question=intent.getStringExtra("q")
        ansallbind.aaQuestion.text="Q."+question
        val recveiw=ansallbind.aaRec
        recveiw.setHasFixedSize(true)
        recveiw.isNestedScrollingEnabled=true
        recveiw.layoutManager = LinearLayoutManager(this)
        val adapter= allansRecAdapter(anslist,this)
        recveiw.adapter=adapter

    }
}