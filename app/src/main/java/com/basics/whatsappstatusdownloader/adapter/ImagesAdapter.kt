package com.basics.whatsappstatusdownloader.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
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
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.basics.whatsappstatusdownloader.Constants
import com.basics.whatsappstatusdownloader.MediaPreviewActivity
import com.basics.whatsappstatusdownloader.MediaTypeConstant
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


class ImagesAdapter(val context: Context, val filesList: ArrayList<StatusModel>) :
    RecyclerView.Adapter<ImagesAdapter.ImagesViewHolder>() {

    var mListener:OnClickListener? = null

    interface OnClickListener{
        fun onClick(position: Int)
    }


    fun setOnClickListener(listener:OnClickListener){
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_images, parent, false)
        return ImagesViewHolder(view)
    }

    override fun getItemCount() = filesList.size

    override fun onBindViewHolder(holder: ImagesViewHolder, position: Int) {
        val file = filesList[position]

        Picasso.get().load(file.uri).into(holder.mediaImage)

        setFileDateAndTime(file.path, holder)



        holder.deleteImage.setOnClickListener {
            val path: String = file.path
            val file = File(path)
            file.delete()
            Toast.makeText(context, "File deleted successfully", Toast.LENGTH_SHORT)
                .show()
            mListener!!.onClick(position)
        }

        holder.shareImage.setOnClickListener {
            shareImage(file)
        }

        holder.mediaImage.setOnClickListener{
            openMediaPreviewActivity(file)
        }

    }
    private fun openMediaPreviewActivity(statusModel: StatusModel) {
        val intent = Intent(context, MediaPreviewActivity::class.java)
        intent.putExtra(MediaTypeConstant.TYPE, MediaTypeConstant.IMAGE_TYPE)
        intent.putExtra(MediaTypeConstant.PATH,statusModel.path)
        intent.putExtra(MediaTypeConstant.URI,statusModel.uri.toString())
        intent.putExtra(MediaTypeConstant.FILE_NAME,statusModel.name)
        context.startActivity(intent)


    }

    private fun shareImage(file: StatusModel) {
        val uriToImage = file.uri
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.apply {
            type = "image/*"
            putExtra(Intent.EXTRA_STREAM, uriToImage)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Image"))


    }

    private fun setFileDateAndTime(
        path: String,
        holder: ImagesAdapter.ImagesViewHolder
    ) {

        val file = File(path)
        val lastModified = file.lastModified()

        val lastModifiedDate = DateFormat.format("yyyy-MM-dd hh:mm:ss a", Date(lastModified))

        Log.d("date", lastModifiedDate.toString())

        val currentDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val date = DateFormat.format("yyyy-MM-dd", Date(lastModified)).toString()

        if (currentDate.compareTo(date) == 0) {
            holder.mediaDateTime.text = "Today" + " " + lastModifiedDate.substring(11)
        } else {
            holder.mediaDateTime.text = lastModifiedDate.toString()
        }


    }


    inner class ImagesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mediaImage: ImageView = itemView.findViewById(R.id.mediaImage)
        val mediaDateTime: TextView = itemView.findViewById(R.id.dateTimeTv)
        val shareImage: ImageView = itemView.findViewById(R.id.shareImage)
        val deleteImage: ImageView = itemView.findViewById(R.id.deleteImage)


    }
}