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
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.basics.whatsappstatusdownloader.Constants
import com.basics.whatsappstatusdownloader.R
import com.basics.whatsappstatusdownloader.adapter.DownloadAdapter
import com.basics.whatsappstatusdownloader.adapter.StatusAdapter
import com.basics.whatsappstatusdownloader.model.StatusModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class DownloadsFragment : Fragment() {
    lateinit var swipeLayout: SwipeRefreshLayout
    lateinit var downloadsRv: RecyclerView
    lateinit var rlLayout: RelativeLayout
    lateinit var progress: ProgressBar
    lateinit var emptyTv:TextView

    lateinit var downloadAdapter: DownloadAdapter
    var filesList =  ArrayList<StatusModel>()
    var files: Array<File>? = null




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_downloads, container, false)
        init(view)
        return view
    }
    private fun init(view:View) {
        swipeLayout = view.findViewById(R.id.swipeContainer)
        downloadsRv = view.findViewById(R.id.downloadsRv)
        progress = view.findViewById(R.id.progress)
        rlLayout = view.findViewById(R.id.rlLayout)
        progress.visibility = View.VISIBLE
        rlLayout.visibility = View.VISIBLE
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
        downloadsRv.layoutManager = LinearLayoutManager(context)
        downloadAdapter = DownloadAdapter(activity!!,filesList)
        downloadsRv.adapter = downloadAdapter


        downloadAdapter.setOnClickListener(object:DownloadAdapter.OnClickListener{
            override fun onClick(position: Int) {
                filesList.removeAt(position)
                downloadAdapter.notifyDataSetChanged()
            }

        })



        CoroutineScope(Dispatchers.IO).launch {
            getResult()
        }
    }

    private fun setUpRefreshLayout() {
        filesList.clear()
        downloadsRv.hasFixedSize()
        CoroutineScope(Dispatchers.IO).launch {
            getResult()
        }
    }


    private suspend fun getResult(){
        var f:StatusModel
        val targetPath = context!!.getExternalFilesDir(null)!!.absolutePath+ Constants.SAVE_FOLDER_NAME
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

            if(!f.uri.toString().endsWith(".nomedia")){
                filesList.add(f)
            }
        }

        setResult(filesList)
    }

    private suspend fun setResult(filesList:ArrayList<StatusModel>){
        withContext(Dispatchers.Main){
            result(filesList)
        }
    }

    private fun result(data: ArrayList<StatusModel>){
        rlLayout.visibility = View.GONE
        progress.visibility = View.GONE
        if(data.size != 0 && !data.isEmpty()){ emptyTv.visibility = View.GONE
        filesList = data
        downloadAdapter.notifyDataSetChanged()
        }else{
            emptyTv.visibility = View.VISIBLE
            emptyTv.text = "Downloaded Status Show here"

        }
    }

}

