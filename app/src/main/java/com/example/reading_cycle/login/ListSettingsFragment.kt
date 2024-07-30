package com.example.reading_cycle.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.reading_cycle.MainActivity
import com.example.reading_cycle.databinding.FragmentListSettingsBinding

class ListSettingsFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    private lateinit var fragmentListSettingsBinding: FragmentListSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        fragmentListSettingsBinding = FragmentListSettingsBinding.inflate(inflater)
        mainActivity.showBottomNavigation()

        //setupUiListeners()

        return fragmentListSettingsBinding.root
    }
}

    //private fun setupUiListeners() {}


