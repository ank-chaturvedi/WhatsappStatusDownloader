package com.basics.whatsappstatusdownloader.fragments

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.basics.whatsappstatusdownloader.Constants
import com.basics.whatsappstatusdownloader.R
import com.basics.whatsappstatusdownloader.adapter.VideoAdapter
import com.basics.whatsappstatusdownloader.model.StatusModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

import kotlin.collections.ArrayList


class VideoFragment : Fragment() {
    lateinit var swipeLayout: SwipeRefreshLayout
    lateinit var videoRv: RecyclerView
    lateinit var rlLayout: RelativeLayout
    lateinit var progress: ProgressBar
    lateinit var layoutManager:LinearLayoutManager
    lateinit var emptyTv:TextView
    lateinit var videoAdapter: VideoAdapter
    var filesList = ArrayList<StatusModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_video, container, false)
        init(view)

        return view
    }

    private fun init(view:View) {
        swipeLayout = view.findViewById(R.id.swipeContainer)
        videoRv = view.findViewById(R.id.videoRv)
        progress = view.findViewById(R.id.progress)
        rlLayout = view.findViewById(R.id.rlLayout)
        emptyTv = view.findViewById(R.id.emptyTv)
        emptyTv.visibility = View.VISIBLE
        initRecylerView()

        swipeLayout.setOnRefreshListener{
            swipeLayout.isRefreshing = true
            setUpRefreshLayout()
            Handler().postDelayed({
                swipeLayout.isRefreshing = false
            },500)

        }
    }

    private fun initRecylerView() {
        layoutManager = LinearLayoutManager(context)
        videoRv.layoutManager = layoutManager
        videoAdapter = VideoAdapter(activity!!,filesList)
        videoRv.adapter = videoAdapter

        videoAdapter.setOnClickListener(object:VideoAdapter.OnClickListener{
            override fun onClick(position: Int) {
                filesList.removeAt(position)
                videoAdapter.notifyDataSetChanged()
            }

        })






        CoroutineScope(IO).launch {
            getResult()
        }
    }



    private fun setUpRefreshLayout() {

        filesList.clear()
        videoRv.hasFixedSize()
        CoroutineScope(IO).launch {
            getResult()
        }
    }

    private suspend fun getResult() {

        var f: StatusModel
        var targetPath:String
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q){
            targetPath = "/storage/emulated/0"+ Constants.FOLDER_NAME+"Media/WhatsApp Video"
        }else{
            targetPath = Environment.getExternalStorageDirectory().absolutePath+ Constants.FOLDER_NAME+"Media/WhatsApp Video"
        }


        val targetDir = File(targetPath)

        if(!targetDir.exists()){
            targetDir.mkdir()
        }

        val file = targetDir.listFiles()

        Log.d("file size","${file.size}")
        file!!.forEach {
            val fileName = it.name
            val filePath = it.path
            val uri = Uri.fromFile(it)
            f = StatusModel(fileName,filePath,fileName,uri)

            if(!f.uri.toString().endsWith(".nomedia") && !it.isDirectory){
                filesList.add(f)
            }
        }

        setResult(filesList)

    }

    private suspend fun setResult(filesList: ArrayList<StatusModel>) {
            withContext(Main){
                result(filesList)
            }
    }

    private fun result(data: ArrayList<StatusModel>){
        rlLayout.visibility = View.GONE
        progress.visibility = View.GONE
        filesList= data
        if(filesList.isEmpty() && filesList.size == 0){
            emptyTv.text = "Please Add Some Audio"
            emptyTv.visibility = View.VISIBLE
        }else{
            emptyTv.visibility = View.GONE
        }
        videoAdapter.notifyDataSetChanged()
    }

}


