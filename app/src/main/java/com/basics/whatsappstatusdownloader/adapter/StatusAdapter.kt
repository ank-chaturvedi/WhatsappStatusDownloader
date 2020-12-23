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



class StatusAdapter(val context: Context,val filesList:ArrayList<StatusModel>):RecyclerView.Adapter<StatusAdapter.StatusViewHolder>() {








    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatusViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_status,parent,false)
        return StatusViewHolder(view)
    }

    override fun getItemCount(): Int = filesList.size


    override fun onBindViewHolder(holder: StatusViewHolder, position: Int) {
        val statusModel = filesList[position]

        if(statusModel.uri.toString().endsWith(".mp4")){
            val bmThumbnail = ThumbnailUtils.createVideoThumbnail(statusModel.path, MediaStore.Video.Thumbnails.MICRO_KIND);
            holder.mediaImage.setImageBitmap(bmThumbnail)
            holder.mediaName.text = "Video"+"\n"
        }else{
            holder.playImage.visibility = View.GONE
            Picasso.get().load(statusModel.uri).into(holder.mediaImage)
            holder.mediaName.text = "Image"+"\n"
        }


        setFileDateAndTime(statusModel.path,holder)



        holder.downloadImage.setOnClickListener {

            checkFolder()

            val path:String = statusModel.path
            val file = File(path)
            val time = file.lastModified()


            val destPath  =  context.getExternalFilesDir(null)!!.absolutePath + Constants.SAVE_FOLDER_NAME
            val destFile = File(destPath,file.name)
            if(destFile.exists())
                Toast.makeText(context,"Already Downloaded",Toast.LENGTH_SHORT).show()
            else {
                copyFile(file, destFile)
                Toast.makeText(context,"Status saved"+destPath+ statusModel.fileName,Toast.LENGTH_SHORT).show()
            }

            MediaScannerConnection.scanFile(
                    context,
                    arrayOf(destPath+statusModel.name),
                    arrayOf("*/*"),
                    MediaClient()
            )




        }

        holder.shareImage.setOnClickListener {
            shareMedia(statusModel)
        }

        holder.mediaImage.setOnClickListener {
            openMediaPreviewActivity(statusModel)
        }



    }

    private fun openMediaPreviewActivity(statusModel: StatusModel) {
        val intent = Intent(context,MediaPreviewActivity::class.java)
        if(statusModel.uri.toString().endsWith(".mp4")){
            intent.putExtra(MediaTypeConstant.TYPE,MediaTypeConstant.VIDEO_TYPE)
        }else{
            intent.putExtra(MediaTypeConstant.TYPE,MediaTypeConstant.IMAGE_TYPE)
        }
        intent.putExtra(MediaTypeConstant.PATH,statusModel.path)
        intent.putExtra(MediaTypeConstant.URI,statusModel.uri.toString())
        intent.putExtra(MediaTypeConstant.FILE_NAME,statusModel.name)
      context.startActivity(intent)


    }

    private fun shareMedia(file: StatusModel) {
        val uriToImage = file.uri
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.apply {
            type = "*/*"
            putExtra(Intent.EXTRA_STREAM, uriToImage)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Status"))



    }

    private fun setFileDateAndTime(
        path: String,
        holder: StatusViewHolder
    ) {

        val file = File(path)
        val lastModified = file.lastModified()

        val lastModifiedDate = DateFormat.format("yyyy-MM-dd hh:mm:ss a",Date(lastModified))

        Log.d("date",lastModifiedDate.toString())

        val currentDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val date = DateFormat.format("yyyy-MM-dd",Date(lastModified)).toString()

        if(currentDate.compareTo(date) == 0){
            holder.mediaDateTime.text = "Today"+" "+lastModifiedDate.substring(11)
        }else{
            holder.mediaDateTime.text = lastModifiedDate.toString()
        }


    }

    private fun checkFolder() {
        val path = context.getExternalFilesDir(null)!!.absolutePath + Constants.SAVE_FOLDER_NAME


        val file = File(path)

        var isDirCreated = file.isDirectory

        if(isDirCreated){
            Log.d("folder",path)
        }else{
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




    inner class StatusViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val mediaImage:ImageView = itemView.findViewById(R.id.mediaImage)
        val mediaName:TextView = itemView.findViewById(R.id.imageTextTv)
        val shareImage:ImageView = itemView.findViewById(R.id.shareImage)
        val downloadImage:ImageView = itemView.findViewById(R.id.downloadImage)
        val playImage:ImageView = itemView.findViewById(R.id.playImage)
        val mediaDateTime:TextView = itemView.findViewById(R.id.dateTimeTv)
    }

    inner class MediaClient:MediaScannerConnection.MediaScannerConnectionClient{
        override fun onMediaScannerConnected() {

        }

        override fun onScanCompleted(p0: String?, p1: Uri?) {
            Log.d("path",p0!!)
            Log.d("uri",p1!!.toString())

        }

    }



}