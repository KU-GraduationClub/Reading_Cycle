package com.example.reading_cycle

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.reading_cycle.databinding.FragmentLibraryMainBinding

class LibraryMainFragment : Fragment() {

    private lateinit var fragmentLibraryMainBinding : FragmentLibraryMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentLibraryMainBinding = FragmentLibraryMainBinding.inflate(inflater)
        // 타이틀 아이콘 작업
        val iconDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.baseline_sync_40)
        fragmentLibraryMainBinding.toolbarTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)


        fragmentLibraryMainBinding.toolbarTitle.compoundDrawablePadding = resources.getDimensionPixelSize(R.dimen.icon_text_padding)
        // 텍스트 설정
        fragmentLibraryMainBinding.toolbarTitle.text = "라이브러리"

        return fragmentLibraryMainBinding.root
    }
}

























