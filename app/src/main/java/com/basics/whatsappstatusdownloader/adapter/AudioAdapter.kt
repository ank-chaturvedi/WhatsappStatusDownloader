package com.basics.whatsappstatusdownloader.adapter

import android.content.Context
import android.content.Intent
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.basics.whatsappstatusdownloader.R
import com.basics.whatsappstatusdownloader.model.StatusModel
import com.squareup.picasso.Picasso
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.channels.FileChannel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AudioAdapter(val context: Context,val filesList:ArrayList<StatusModel>):RecyclerView.Adapter<AudioAdapter.AudioViewHolder>() {
    var mListener: OnClickListener? = null

    interface OnClickListener {
        fun onClick(position: Int)
    }


    fun setOnClickListener(listener: OnClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_audio,parent,false)
        return AudioViewHolder(view)
    }

    override fun getItemCount() = filesList.size

    override fun onBindViewHolder(holder: AudioViewHolder, position: Int) {
        val file = filesList[position]



        setFileDateAndTime(file.path,holder)
        holder.deleteAudio.setOnClickListener {
            val path:String = file.path
            val file = File(path)
            file.delete()
            Toast.makeText(context,"File deleted successfully!Please Refresh", Toast.LENGTH_SHORT).show()
            mListener!!.onClick(position)
        }

        holder.shareAudio.setOnClickListener {
            shareAudio(file)
        }


    }

    private fun shareAudio(file: StatusModel) {
        val uriToImage = file.uri
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.apply {
            type = "*/*"
            putExtra(Intent.EXTRA_STREAM, uriToImage)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Audio"))



    }

    private fun setFileDateAndTime(
        path: String,
        holder: AudioAdapter.AudioViewHolder
    ) {

        val file = File(path)
        val lastModified = file.lastModified()

        val lastModifiedDate = DateFormat.format("yyyy-MM-dd hh:mm:ss a", Date(lastModified))

        Log.d("date",lastModifiedDate.toString())

        val currentDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val date = DateFormat.format("yyyy-MM-dd", Date(lastModified)).toString()

        if(currentDate.compareTo(date) == 0){
            holder.mediaDateTime.text = "Today"+" "+lastModifiedDate.substring(11)
        }else{
            holder.mediaDateTime.text = lastModifiedDate.toString()
        }


    }






    inner class AudioViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val mediaImage: ImageView = itemView.findViewById(R.id.audioImage)
        val shareAudio:ImageView = itemView.findViewById(R.id.shareAudio)
        val deleteAudio:ImageView = itemView.findViewById(R.id.deleteAudio)
        val mediaDateTime: TextView = itemView.findViewById(R.id.dateTimeTv)
    }
}