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
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.basics.whatsappstatusdownloader.Constants
import com.basics.whatsappstatusdownloader.R
import com.basics.whatsappstatusdownloader.adapter.AudioAdapter
import com.basics.whatsappstatusdownloader.adapter.ImagesAdapter
import com.basics.whatsappstatusdownloader.model.StatusModel
import java.io.File
import java.util.*
import kotlin.collections.ArrayList




class AudioFragment : Fragment() {
    lateinit var swipeLayout: SwipeRefreshLayout
    lateinit var audioRv: RecyclerView
    lateinit var emptyTv:TextView


    lateinit var audioAdapter: AudioAdapter
    var filesList =ArrayList<StatusModel>()
    var files: Array<File>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_audio, container, false)
        init(view)
        return view
    }

    private fun init(view:View) {
        swipeLayout = view.findViewById(R.id.swipeContainer)
        audioRv = view.findViewById(R.id.audioRv)
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
        audioRv.layoutManager = LinearLayoutManager(context)
        filesList = getData()
        audioAdapter = AudioAdapter(activity!!,filesList)
        audioRv.adapter = audioAdapter

        audioAdapter.setOnClickListener(object:AudioAdapter.OnClickListener{
            override fun onClick(position: Int) {
                filesList.removeAt(position)
                audioAdapter.notifyDataSetChanged()
            }

        })

    }

    private fun setUpRefreshLayout() {
        filesList.clear()
        audioRv.hasFixedSize()
        filesList = getData()
        if(filesList.isEmpty() && filesList.size <= 0){
            emptyTv.text = "Please Add Some Audio"
            emptyTv.visibility = View.VISIBLE
        }else{
            emptyTv.visibility = View.GONE
        }
        audioAdapter.notifyDataSetChanged()
    }

    private fun getData(): ArrayList<StatusModel> {
        var f: StatusModel
        var targetPath:String

        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q){
            targetPath = "/storage/emulated/0"+ Constants.FOLDER_NAME+"Media/WhatsApp Audio"
        }else{
            targetPath  = Environment.getExternalStorageDirectory().absolutePath+ Constants.FOLDER_NAME+"Media/WhatsApp Audio"
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

            if(!f.uri.toString().endsWith(".nomedia") && !f.uri.toString().endsWith(".opus") && !it.isDirectory){
                filesList.add(f)
            }
        }
        return filesList
    }



}