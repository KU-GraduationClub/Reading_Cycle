package com.example.reading_cycle.friend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.reading_cycle.MainActivity
import com.example.reading_cycle.R
import com.example.reading_cycle.databinding.FragmentFriendMainBinding
import com.example.reading_cycle.friend.model.Friend
import com.example.reading_cycle.friend.repository.FriendRepository
import com.example.reading_cycle.friend.vm.FriendViewModel

class FriendMainFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    private lateinit var fragmentFriendMainBinding: FragmentFriendMainBinding
    private lateinit var friendMainAdapter: FriendMainAdapter
    private lateinit var friendViewModel: FriendViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // ViewModel 및 LiveData 초기화
        friendViewModel = ViewModelProvider(this).get(FriendViewModel::class.java)

        // FragmentFriendMainBinding 초기화
        fragmentFriendMainBinding = FragmentFriendMainBinding.inflate(inflater, container, false)
        val binding = fragmentFriendMainBinding.root

        // MainActivity 초기화
        mainActivity = activity as MainActivity
        mainActivity.showBottomNavigation()

        // RecyclerView 설정
        val recyclerView: RecyclerView = fragmentFriendMainBinding.recyclerViewFriendMain
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager

        // 어댑터 설정
        friendMainAdapter = FriendMainAdapter(ArrayList()) // 빈 목록으로 초기화
        recyclerView.adapter = friendMainAdapter

        // 데이터 관찰 및 업데이트 처리
        friendViewModel.userData.observe(viewLifecycleOwner) { userData ->
            userData?.let {
                updateFriendList(it)
            } ?: run {
                // userData가 null인 경우 처리할 코드 작성
            }
        }

        // 데이터 가져오기
        friendViewModel.fetchUserData()

        // 툴바 알림 메뉴 클릭 이벤트 처리
        fragmentFriendMainBinding.toolbarLayoutFriendMain.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.friendMenuItemNotify -> {
                    mainActivity.navigateToNotifyFragment()
                    true
                }
                else -> false
            }
        }

        return binding
    }

    private fun updateFriendList(userData: List<FriendRepository.UserData>) {
        // userData Friend 객체로 변환하여 RecyclerView 표시하는 로직 작성
        val friendList = getFriendListFromDatabase(userData)
        friendMainAdapter.updateFriendList(friendList)
    }

    private fun getFriendListFromDatabase(userData: List<FriendRepository.UserData>): List<Friend> {
        // userData 사용하여 Firebase Realtime Database 친구 목록을 가져오는 로직 작성
        val friendList = mutableListOf<Friend>()

        // userData 리스트를 순회하면서 각 UserData Friend 객체로 변환하여 friendList 추가
        for (user in userData) {
            val friend = Friend(user.nickname, user.memo, user.imageUrl)
            friendList.add(friend)
        }

        return friendList
    }

}

class FriendMainAdapter(private var friendList: MutableList<Friend>) : RecyclerView.Adapter<FriendMainAdapter.FriendViewHolder>() {

    inner class FriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nicknameTextView: TextView = itemView.findViewById(R.id.textLibraryMyUser)
        private val memoTextView: TextView = itemView.findViewById(R.id.textLibraryMyUserMemo)

        fun bind(friend: Friend) {
            nicknameTextView.text = friend.nickname
            memoTextView.text = friend.memo
            // 이미지 설정 등 다른 작업을 여기에 추가할 수 있습니다.
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_friend_item_layout, parent, false)
        return FriendViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val friend = friendList[position]
        holder.bind(friend)
    }

    override fun getItemCount(): Int {
        return friendList.size
    }

    fun updateFriendList(newFriendList: List<Friend>) {
        friendList.clear()
        friendList.addAll(newFriendList)
        notifyDataSetChanged()
    }
}

