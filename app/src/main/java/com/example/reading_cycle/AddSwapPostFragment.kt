package com.example.reading_cycle

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.reading_cycle.databinding.FragmentAddSalePostBinding
import com.example.reading_cycle.databinding.FragmentAddSwapPostBinding
import com.example.reading_cycle.databinding.FragmentPostMainBinding

class AddSwapPostFragment : Fragment() {

    private lateinit var fragmentAddSwapPostBinding: FragmentAddSwapPostBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentAddSwapPostBinding = FragmentAddSwapPostBinding.inflate(inflater)

        return fragmentAddSwapPostBinding.root
    }
}