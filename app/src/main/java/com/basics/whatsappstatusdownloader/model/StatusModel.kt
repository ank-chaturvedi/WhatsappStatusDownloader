package com.basics.whatsappstatusdownloader.model

import android.net.Uri


data class StatusModel(
    val name:String,
    val path:String,
    val fileName:String,
    val uri: Uri
)