package com.example.audioquora

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File
import android.os.Bundle
import android.view.Gravity
import java.io.Serializable


class mainViewHolder(view:View):RecyclerView.ViewHolder(view)
{
    lateinit var title:TextView
    lateinit var name:TextView
    lateinit var content:ScrollView
    lateinit var upvote:Button
    lateinit var downvote:Button
    lateinit var ansbut:Button
    lateinit var lay: LinearLayout
    lateinit var seeall:Button
    init {
        title=view.findViewById(R.id.mm_question)
        name=view.findViewById(R.id.mm_name)

        upvote=view.findViewById(R.id.upvote)
        downvote=view.findViewById(R.id.downvote)
        ansbut=view.findViewById(R.id.mm_ans)
        lay=view.findViewById(R.id.verlay)
        seeall=view.findViewById(R.id.mm_seemore)
    }
}
class mainrecyclerview(val alldoc:ArrayList<firebasedocument>,val context:Context):RecyclerView.Adapter<mainViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): mainViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.maininmain,parent,false)
        return mainViewHolder(view)
    }
    override fun onBindViewHolder(holder: mainViewHolder, position: Int) {
        holder.title.text="Q. "+alldoc[position].qeus.title
        holder.name.text="-"+alldoc[position].qeus.name
        holder.ansbut.setOnClickListener{
            val intent= Intent(context,giveans::class.java)
            intent.putExtra("quesid",alldoc[position].id)
            context.startActivity(intent)

        }
        holder.seeall.setOnClickListener{
            val intent=Intent(context,ansall0::class.java)
            val args = Bundle()
            args.putSerializable("ARRAYLIST",alldoc[position].qeus.answers as Serializable?)
            intent.putExtra("BUNDLE", args)
            intent.putExtra("q",alldoc[position].qeus.title)
            context.startActivity(intent)
        }
        if(alldoc[position].qeus.answers.size==0)
        {
            val tempimg=ImageView(context)
            tempimg.setImageResource(R.drawable.nobody)
            tempimg.setBackgroundColor(Color.WHITE)
             holder.lay.addView(tempimg)
        }
        else
        {   val audlinks=alldoc[position].qeus.answers[0].ans.audiolink
            val tlist=alldoc[position].qeus.answers[0].ans.textlist
            for(i in 0..alldoc[position].qeus.answers[0].ans.audiolink.size-1)
         {   val tv=TextView(context)
             tv.text="• "+tlist[i].toString()
             tv.setBackgroundColor(Color.WHITE)
             tv.setTextColor(Color.BLACK)
             holder.lay.addView(tv)

                 val im:ImageButton= ImageButton(context)
                 val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 150)
                 im.setLayoutParams(layoutParams)
                 im.setImageResource(R.drawable.audio)
                // im.adjustViewBounds=true
                im.setBackgroundColor(Color.WHITE)
                 im.setOnClickListener{
                     val storage = Firebase.storage
                     val localFile = File.createTempFile("audio", "mp3")
                     val temp=storage.getReferenceFromUrl(audlinks[i].toString())
                    // Log.d("draka",localFile.toString())
                     temp.getFile(localFile).addOnSuccessListener {
                         val player = MediaPlayer().apply {
                             try {
                                 setDataSource(localFile.toString())
                                 prepare()
                                 start()
                             } catch (e: Exception) {
                                 Log.e("erroru", localFile.toString())
                             }
                         }
                     }
                 }
                     holder.lay.addView(im)

         }
            val tv=TextView(context)
            tv.text=tlist[alldoc[position].qeus.answers[0].ans.audiolink.size].toString()
            holder.lay.addView(tv)

        }
    }
    override fun getItemCount(): Int {
        return alldoc.size
    }

}