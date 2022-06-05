package com.example.audioquora

import android.content.Context
import android.graphics.Color
import android.media.MediaPlayer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File

class aaViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
     val lay=ItemView.findViewById<LinearLayout>(R.id.aa_lay)
     val upvote=ItemView.findViewById<Button>(R.id.aa_upvote)
    val downvote=ItemView.findViewById<Button>(R.id.aa_downvote)
    val by=ItemView.findViewById<TextView>(R.id.aaby)
}
class allansRecAdapter(val anslist:ArrayList<answerslist>,val context: Context) : RecyclerView.Adapter<aaViewHolder>() {

    override fun onCreateViewHolder(parent:ViewGroup, viewType: Int): aaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.singleanslay, parent, false)
        return aaViewHolder(view)
    }
    override fun onBindViewHolder(holder:aaViewHolder, position: Int) {
        val audlinks=anslist[position].ans.audiolink
        val tlist=anslist[position].ans.textlist
        for(i in 0..anslist[position].ans.audiolink.size-1)
        {   val tv=TextView(context)
            tv.text="• "+tlist[i].toString()
            tv.setTextColor(Color.BLACK)
            tv.setBackgroundColor(Color.WHITE)
            holder.lay.addView(tv)

            val im: ImageButton = ImageButton(context)
            val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 150)
            im.setLayoutParams(layoutParams)
            im.setImageResource(R.drawable.audio)
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
        tv.text="• "+tlist[anslist[position].ans.audiolink.size].toString()
        tv.setTextColor(Color.BLACK)
        holder.lay.addView(tv)
    }
    override fun getItemCount(): Int {
        return anslist.size
    }



}