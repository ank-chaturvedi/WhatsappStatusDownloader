package com.basics.whatsappstatusdownloader

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        if(!checkAllPermission()){
            ActivityCompat.requestPermissions(this, PERMISSION_ARRAY, PERMISSION_CODE)
        }else{
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun checkAllPermission(): Boolean = PERMISSION_ARRAY.all {
        ContextCompat.checkSelfPermission(this,it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == PERMISSION_CODE){
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            finishAffinity()
        }
    }


    companion object{
        private val PERMISSION_CODE = 10
        private val PERMISSION_ARRAY = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE
        ,Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }
}