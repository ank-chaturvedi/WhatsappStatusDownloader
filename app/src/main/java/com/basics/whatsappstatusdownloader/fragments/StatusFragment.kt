package com.basics.whatsappstatusdownloader.fragments

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.basics.whatsappstatusdownloader.Constants
import com.basics.whatsappstatusdownloader.R
import com.basics.whatsappstatusdownloader.adapter.StatusAdapter
import com.basics.whatsappstatusdownloader.model.StatusModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.collections.ArrayList
import kotlin.math.E


class StatusFragment : Fragment() {
    private lateinit var swipeLayout:SwipeRefreshLayout
    private lateinit var statusRv:RecyclerView
    private lateinit var rlLayout:RelativeLayout
    private lateinit var progress:ProgressBar
lateinit var emptyTv:TextView
    private lateinit var statusAdapter: StatusAdapter
    var filesList =  ArrayList<StatusModel>()
    var files: Array<File>? = null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view= inflater.inflate(R.layout.fragment_status, container, false)

        init(view)
        return view
    }

    private fun init(view:View) {
        swipeLayout = view.findViewById(R.id.swipeContainer)
        statusRv = view.findViewById(R.id.statusRv)
        progress = view.findViewById(R.id.progress)
        rlLayout = view.findViewById(R.id.rlLayout)
        emptyTv = view.findViewById(R.id.emptyTv)
        emptyTv.visibility = View.VISIBLE
        progress.visibility = View.VISIBLE
        rlLayout.visibility = View.VISIBLE

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
        statusRv.layoutManager = LinearLayoutManager(context)
        statusAdapter = StatusAdapter(activity!!,filesList)
        statusRv.adapter = statusAdapter
        CoroutineScope(IO).launch {
            getResult()
        }
    }

    private fun setUpRefreshLayout() {
        filesList.clear()
        statusRv.hasFixedSize()
        CoroutineScope(IO).launch {
            getResult()
        }
    }


    private suspend fun getResult(){
        var f:StatusModel
        var targetPath:String

        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q){
            targetPath =  Environment.getExternalStorageDirectory().absolutePath+Constants.FOLDER_NAME+"Media/.Statuses"


        }else{
            targetPath =  Environment.getExternalStorageDirectory().absolutePath+Constants.FOLDER_NAME+"Media/.Statuses"
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

            if(!f.uri.toString().endsWith(".nomedia")){
                filesList.add(f)
            }
        }
        
        setResult(filesList)
    }

    private suspend fun setResult(filesList:ArrayList<StatusModel>){
        withContext(Main){
            result(filesList)
        }
    }

    private fun result(data: ArrayList<StatusModel>){
        filesList = data
        statusAdapter.notifyDataSetChanged()
        if(filesList.isEmpty() && filesList.size == 0){
            emptyTv.text = "Please Add Some Audio"
            emptyTv.visibility = View.VISIBLE
        }else{
            emptyTv.visibility = View.GONE
        }
        rlLayout.visibility = View.GONE
        progress.visibility = View.GONE
    }

}