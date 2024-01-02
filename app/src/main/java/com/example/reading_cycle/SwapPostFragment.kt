package com.example.reading_cycle

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.reading_cycle.databinding.FragmentSalePostBinding
import com.example.reading_cycle.databinding.FragmentSwapPostBinding

class SwapPostFragment : Fragment() {

    private lateinit var fragmentSwapPostBinding: FragmentSwapPostBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        return fragmentSwapPostBinding.root
    }
}