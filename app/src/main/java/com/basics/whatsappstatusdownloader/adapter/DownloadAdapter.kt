package com.basics.whatsappstatusdownloader.adapter

import android.content.Context
import android.content.Intent
import android.media.ThumbnailUtils
import android.provider.MediaStore
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
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DownloadAdapter(val context: Context, val filesList:ArrayList<StatusModel>):RecyclerView.Adapter<DownloadAdapter.DownloadViewHolder>() {
    var mListener: OnClickListener? = null

    interface OnClickListener {
        fun onClick(position: Int)
    }


    fun setOnClickListener(listener: OnClickListener) {
        mListener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DownloadViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_download,parent,false)
        return DownloadViewHolder(view)
    }

    override fun getItemCount() = filesList.size

    override fun onBindViewHolder(holder: DownloadViewHolder, position: Int) {
        val file = filesList[position]

        if(file.uri.toString().endsWith(".mp4")){
            val bmThumbnail = ThumbnailUtils.createVideoThumbnail(file.path, MediaStore.Video.Thumbnails.MICRO_KIND);
            holder.mediaImage.setImageBitmap(bmThumbnail);
            holder.mediaName.text = "Video"+"\n"
        }else{
            holder.playImage.visibility = View.GONE
            Picasso.get().load(file.uri).into(holder.mediaImage)
            holder.mediaName.text = "Image"+"\n"
        }
        setFileDateAndTime(file.path,holder)

        holder.deleteImage.setOnClickListener {
            val path:String = file.path
            val file = File(path)
            file.delete()
            Toast.makeText(context,"File deleted successfully", Toast.LENGTH_SHORT).show()
            mListener!!.onClick(position)
        }

        holder.shareImage.setOnClickListener {
            shareMedia(file)
        }

    }

    private fun shareMedia(file: StatusModel) {
        val uriToImage = file.uri
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.apply {
            type = "*/*"
            putExtra(Intent.EXTRA_STREAM, uriToImage)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Downloads"))



    }

    private fun setFileDateAndTime(
        path: String,
        holder: DownloadAdapter.DownloadViewHolder
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

    inner class DownloadViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        val mediaImage: ImageView = itemView.findViewById(R.id.mediaImage)
        val mediaName: TextView = itemView.findViewById(R.id.imageTextTv)
        val shareImage: ImageView = itemView.findViewById(R.id.shareImage)
        val deleteImage: ImageView = itemView.findViewById(R.id.deleteImage)
        val playImage: ImageView = itemView.findViewById(R.id.playImage)
        val mediaDateTime: TextView = itemView.findViewById(R.id.dateTimeTv)
    }
}