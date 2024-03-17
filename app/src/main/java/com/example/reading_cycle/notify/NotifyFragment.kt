package com.example.reading_cycle.notify

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reading_cycle.MainActivity
import com.example.reading_cycle.R
import com.example.reading_cycle.databinding.FragmentNotifyBinding
import com.example.reading_cycle.notify.model.NotifyAdapter
import com.example.reading_cycle.notify.model.NotifyDataClass
import com.example.reading_cycle.post.model.PostMainAdapter

class NotifyFragment : Fragment() {

    private lateinit var mainActivity : MainActivity
    private lateinit var fragmentNotifyBinding: FragmentNotifyBinding
    private lateinit var notifyAdapter: NotifyAdapter

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

        // 리사이클러뷰 초기화 및 어댑터 설정 (더미)
        val notifyList = mutableListOf(
            NotifyDataClass("제목 1", "알림 내용 1", System.currentTimeMillis()),
            NotifyDataClass("제목 2", "알림 내용 2", System.currentTimeMillis() + 1000),
            NotifyDataClass("제목 3", "알림 내용 3", System.currentTimeMillis() + 2000)
        )

        notifyAdapter = NotifyAdapter(notifyList)
        fragmentNotifyBinding.recyclerNotify.layoutManager = LinearLayoutManager(requireContext())
        fragmentNotifyBinding.recyclerNotify.adapter = notifyAdapter

        return fragmentNotifyBinding.root
    }
}