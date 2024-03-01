package com.example.reading_cycle.location.model

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.reading_cycle.MainActivity
import com.example.reading_cycle.R
import com.example.reading_cycle.databinding.FragmentLocSetBinding


class LocSetFragment : Fragment() {

    private lateinit var fragmentLocSetBinding : FragmentLocSetBinding
    private lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        fragmentLocSetBinding = FragmentLocSetBinding.inflate(inflater)
        mainActivity.hideBottomNavigation()

        return fragmentLocSetBinding.root
    }
}
