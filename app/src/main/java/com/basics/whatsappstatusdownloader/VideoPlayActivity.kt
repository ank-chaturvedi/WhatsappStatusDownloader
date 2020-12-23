package com.basics.whatsappstatusdownloader

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.MediaController
import android.widget.VideoView

class VideoPlayActivity : AppCompatActivity() {
    var file_name:String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_play)
        file_name = intent.getStringExtra(MediaTypeConstant.URI)
        val  videoView:VideoView =findViewById(R.id.videoView1);

        //Creating MediaController
        val  mediaController= MediaController(this);
        mediaController.setAnchorView(videoView);

        val uri = Uri.parse(file_name)
        Log.d("uri",uri.toString())
        //Setting MediaController and URI, then starting the videoView
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.start();

    }



}