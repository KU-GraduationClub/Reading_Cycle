package com.example.reading_cycle.location.model

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.reading_cycle.R
import com.example.reading_cycle.databinding.FragmentLocSetBinding


class LocSetFragment : Fragment() {

    private lateinit var fragmentLocSetBinding : FragmentLocSetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_loc_set, container, false)
        fragmentLocSetBinding = FragmentLocSetBinding.inflate(inflater)

        return fragmentLocSetBinding.root
    }
}
