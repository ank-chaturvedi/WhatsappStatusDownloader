package com.basics.whatsappstatusdownloader.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.basics.whatsappstatusdownloader.fragments.*


class FixedTabPagerAdapter(fm:FragmentActivity):FragmentStateAdapter(fm) {
    override fun getItemCount(): Int {
        return 5
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> StatusFragment()
            1 -> VideoFragment()
            2 -> ImagesFragment()
            3-> AudioFragment()
            else ->DownloadsFragment()
        }
    }
}