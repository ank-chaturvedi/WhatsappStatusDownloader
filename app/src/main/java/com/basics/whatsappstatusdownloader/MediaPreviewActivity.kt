package com.basics.whatsappstatusdownloader

import android.content.Intent
import android.media.Image
import android.media.ThumbnailUtils
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.lifecycle.MediatorLiveData
import com.squareup.picasso.Picasso
import org.w3c.dom.Text

class MediaPreviewActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var mediaPreviewImage:ImageView
    lateinit var shareBtn:ImageView
    lateinit var playBtn:ImageView
    lateinit var finishBtn:ImageView

    var videoType:Boolean = true
    var path:String? = null
    var uri:String? = null
    var file_name:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_preview)
        Log.d("started"," yes")

        val type = intent.getStringExtra(MediaTypeConstant.TYPE)
        videoType = type!!.compareTo(MediaTypeConstant.VIDEO_TYPE) == 0

        path = intent.getStringExtra(MediaTypeConstant.PATH)
        uri  = intent.getStringExtra(MediaTypeConstant.URI)
        file_name = intent.getStringExtra(MediaTypeConstant.FILE_NAME)
        init()

        finishBtn.setOnClickListener(this)
        playBtn.setOnClickListener(this)
        shareBtn.setOnClickListener(this)

    }

    private fun init() {
        mediaPreviewImage = findViewById(R.id.mediaPreviewImage)
        shareBtn = findViewById(R.id.shareBtn)
        playBtn  = findViewById(R.id.playBtn)
        finishBtn = findViewById(R.id.finishBtn)

        if(videoType) {
            playBtn.visibility = View.VISIBLE
            val bmThumbnail = ThumbnailUtils.createVideoThumbnail(path!!, MediaStore.Video.Thumbnails.MICRO_KIND);
            mediaPreviewImage.setImageBitmap(bmThumbnail);

        }
        else {
            playBtn.visibility = View.GONE

            Picasso.get().load(uri!!.toUri()).into(mediaPreviewImage)
        }
    }

    override fun onClick(view: View) {
        when(view){
            finishBtn -> finish()
            shareBtn -> shareContent()
            playBtn -> playContent()
        }
    }

    private fun playContent() {
        val intent = Intent(this,VideoPlayActivity::class.java)
        intent.putExtra(MediaTypeConstant.FILE_NAME,file_name)
        intent.putExtra(MediaTypeConstant.URI,uri)
        startActivity(intent)

    }

    private fun shareContent() {
        val uriToImage = uri!!.toUri()
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.apply {
            type = "*/*"
            putExtra(Intent.EXTRA_STREAM, uriToImage)
        }
        startActivity(Intent.createChooser(shareIntent, "Share Via"))

    }
}