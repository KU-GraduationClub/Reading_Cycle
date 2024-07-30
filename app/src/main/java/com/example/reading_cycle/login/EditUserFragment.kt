package com.example.reading_cycle.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.reading_cycle.MainActivity
import com.example.reading_cycle.databinding.FragmentEditUserBinding

class EditUserFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    private lateinit var fragmentEditUserBinding: FragmentEditUserBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        fragmentEditUserBinding = FragmentEditUserBinding.inflate(inflater)
        mainActivity.showBottomNavigation()

        //setupUiListeners()

        return fragmentEditUserBinding.root
    }
}

    //private fun setupUiListeners() {}


