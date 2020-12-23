package com.basics.whatsappstatusdownloader

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.basics.whatsappstatusdownloader.adapter.FixedTabPagerAdapter
import com.basics.whatsappstatusdownloader.adapter.StatusAdapter
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    lateinit var toolbar:MaterialToolbar
    lateinit var tabLayout:TabLayout
    lateinit var viewPager:ViewPager2



    val tabTitles = arrayOf("Status","Videos","Images","Audios","Downloads")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        initComponents()

        setSupportActionBar(toolbar)

        setViewPager()



    }

    private fun setViewPager() {
        val adapter = FixedTabPagerAdapter(this)
        viewPager.adapter = adapter
        viewPager.isUserInputEnabled = false

        setTabLayout()
    }

    private fun setTabLayout() {
        TabLayoutMediator(tabLayout,viewPager){tab,position ->
            tab.text = tabTitles[position]
//            viewPager.setCurrentItem(position,true)
        }.attach()
    }

    private fun initComponents() {
        toolbar = findViewById(R.id.toolbar)
        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)
    }
}