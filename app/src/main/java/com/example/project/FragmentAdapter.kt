package com.example.project

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class FragmentAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    var fragment = ArrayList<Fragment>()
    var tabTitle = ArrayList<String>()

    fun addFragment(f : Fragment, t:String){

        this.fragment.add(f)
        this.tabTitle.add(t)
    }

    override fun getItem(position: Int): Fragment {

        return  this.fragment[position]
    }

    override fun getCount(): Int {

        return this.fragment.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return this.tabTitle[position]
    }
}