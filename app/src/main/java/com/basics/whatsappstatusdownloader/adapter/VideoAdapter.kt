package com.basics.whatsappstatusdownloader.adapter

import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.media.ThumbnailUtils
import android.net.Uri
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


class VideoAdapter(val context: Context, val filesList: ArrayList<StatusModel>) :
    RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {

    var mListener: OnClickListener? = null

    interface OnClickListener {
        fun onClick(position: Int)
    }


    fun setOnClickListener(listener: OnClickListener) {
        mListener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_video, parent, false)
        return VideoViewHolder(view)
    }

    override fun getItemCount(): Int = filesList.size


    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val statusModel = filesList[position]


        val bmThumbnail = ThumbnailUtils.createVideoThumbnail(
            statusModel.path,
            MediaStore.Video.Thumbnails.MICRO_KIND
        );
        holder.mediaImage.setImageBitmap(bmThumbnail)
        holder.mediaName.text = "Video" + "\n"



        setFileDateAndTime(statusModel.path, holder)





        holder.shareImage.setOnClickListener {
            shareMedia(statusModel)
        }

        holder.mediaImage.setOnClickListener {
            openMediaPreviewActivity(statusModel)
        }

        holder.deleteImage.setOnClickListener {
            val path: String = statusModel.path
            val file = File(path)
            file.delete()
            Toast.makeText(context, "File deleted successfully", Toast.LENGTH_SHORT)
                .show()
            mListener!!.onClick(position)
        }


    }

    private fun openMediaPreviewActivity(statusModel: StatusModel) {
        val intent = Intent(context, MediaPreviewActivity::class.java)
        if (statusModel.uri.toString().endsWith(".mp4")) {
            intent.putExtra(MediaTypeConstant.TYPE, MediaTypeConstant.VIDEO_TYPE)
        } else {
            intent.putExtra(MediaTypeConstant.TYPE, MediaTypeConstant.IMAGE_TYPE)
        }
        intent.putExtra(MediaTypeConstant.PATH, statusModel.path)
        intent.putExtra(MediaTypeConstant.URI, statusModel.uri.toString())
        intent.putExtra(MediaTypeConstant.FILE_NAME, statusModel.name)
        context.startActivity(intent)


    }

    private fun shareMedia(file: StatusModel) {
        val uriToImage = file.uri
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.apply {
            type = "*/*"
            putExtra(Intent.EXTRA_STREAM, uriToImage)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Video"))


    }

    private fun setFileDateAndTime(
        path: String,
        holder: VideoViewHolder
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

    private fun checkFolder() {
        val path = context.getExternalFilesDir(null)!!.absolutePath + Constants.SAVE_FOLDER_NAME


        val file = File(path)

        var isDirCreated = file.isDirectory

        if (isDirCreated) {
            Log.d("folder", path)
        } else {
            file.mkdir()
        }


    }

    @Throws(IOException::class)
    fun copyFile(sourceFile: File?, destFile: File) {
        if (!destFile.parentFile.exists()) destFile.parentFile.mkdirs()
        if (!destFile.exists()) {
            destFile.createNewFile()
        }
        var source: FileChannel? = null
        var destination: FileChannel? = null
        try {
            source = FileInputStream(sourceFile).channel
            destination = FileOutputStream(destFile).channel
            destination.transferFrom(source, 0, source.size())
        } finally {
            if (source != null) {
                source.close()
            }
            if (destination != null) {
                destination.close()
            }
        }
    }


    inner class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mediaImage: ImageView = itemView.findViewById(R.id.mediaImage)
        val mediaName: TextView = itemView.findViewById(R.id.imageTextTv)
        val shareImage: ImageView = itemView.findViewById(R.id.shareImage)
        val deleteImage: ImageView = itemView.findViewById(R.id.deleteImage)
        val playImage: ImageView = itemView.findViewById(R.id.playImage)
        val mediaDateTime: TextView = itemView.findViewById(R.id.dateTimeTv)
    }


}