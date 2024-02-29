package com.example.reading_cycle.notify

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.reading_cycle.MainActivity
import com.example.reading_cycle.R
import com.example.reading_cycle.databinding.FragmentNotifyBinding
import com.example.reading_cycle.post.model.PostMainAdapter

class NotifyFragment : Fragment() {

    private lateinit var mainActivity : MainActivity
    private lateinit var fragmentNotifyBinding: FragmentNotifyBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        fragmentNotifyBinding = FragmentNotifyBinding.inflate(inflater)
        mainActivity.hideBottomNavigation()

        // 뒤로 가기 버튼 클릭 리스너
        fragmentNotifyBinding.toolbarLayoutNotify.setNavigationOnClickListener {
            mainActivity.removeFragment(MainActivity.NOTIFY_FRAGMENT)
        }

        return fragmentNotifyBinding.root
    }
}