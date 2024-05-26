package com.example.reading_cycle.friend

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.reading_cycle.MainActivity
import com.example.reading_cycle.R
import com.example.reading_cycle.databinding.FragmentFriendMainBinding
import com.example.reading_cycle.databinding.RowFriendItemLayoutBinding
import com.example.reading_cycle.friend.model.Friend
import com.example.reading_cycle.friend.repository.FriendRepository
import com.example.reading_cycle.friend.vm.FriendViewModel
import com.google.firebase.database.FirebaseDatabase

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
        friendViewModel = ViewModelProvider(this)[FriendViewModel::class.java]

        // FragmentFriendMainBinding 초기화
        fragmentFriendMainBinding = FragmentFriendMainBinding.inflate(inflater, container, false)
        val binding = fragmentFriendMainBinding.root

        // MainActivity 초기화
        mainActivity = activity as? MainActivity ?: throw IllegalStateException("Activity must be MainActivity")
        mainActivity.showBottomNavigation()

        // RecyclerView 설정
        val recyclerView: RecyclerView = fragmentFriendMainBinding.recyclerViewFriendMain
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // 어댑터 설정
        friendMainAdapter = FriendMainAdapter(ArrayList()) // 빈 목록으로 초기화
        recyclerView.adapter = friendMainAdapter

        // 데이터 관찰 및 업데이트 처리
        friendViewModel.userData.observe(viewLifecycleOwner) { userData ->
            if (userData != null) {
                updateFriendList(userData)
            } else {
                // userData null 경우 처리할 코드 작성
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
        val friendList = mutableListOf<Friend>()
        for (user in userData) {
            val friend = Friend(user.nickname, user.memo, user.imageUrl) // 올바른 순서로 Friend 객체 생성
            friendList.add(friend)
        }
        return friendList
    }
}


class FriendMainAdapter(private var friendList: MutableList<Friend>) : RecyclerView.Adapter<FriendMainAdapter.FriendViewHolder>() {

    inner class FriendViewHolder(private val binding: RowFriendItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.imgBtnFriendMain.setOnClickListener { view ->
                showPopupMenu(view, friendList[adapterPosition], adapterPosition)
            }
        }

        fun bind(friend: Friend) {
            binding.textLibraryMyUser.text = friend.nickname
            binding.textLibraryMyUserMemo.text = friend.memo
            binding.imgFriendStar.visibility = if (friend.isBookmarked) View.VISIBLE else View.GONE

            Glide.with(binding.root)
                .load(friend.imageUrl)
                .placeholder(R.drawable.baseline_person_30) // 로딩 중에 표시할 이미지
                .error(R.drawable.baseline_person_40) // 로드 실패 시 표시할 이미지
                .into(binding.imgFriendProfile)

            binding.BtnSetProfile.setOnClickListener {
                // MainActivity replaceFragment 메서드를 호출하여 프래그먼트를 교체합니다.
                val activity = binding.root.context as? MainActivity
                activity?.replaceFragment(MainActivity.LIBRARY_MAIN_FRAGMENT, true, null)
            }
        }
    }

    private fun showPopupMenu(view: View, friend: Friend, position: Int) {
        val popup = PopupMenu(view.context, view)
        popup.menuInflater.inflate(R.menu.popup_menu_friend_main, popup.menu)
        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menuItemSortBybookmark -> {
                    // 즐겨찾기 토글
                    friend.isBookmarked = !friend.isBookmarked
                    if (friend.isBookmarked) {
                        // 즐겨찾기 추가: 목록 맨 위로 이동
                        friendList.removeAt(position)
                        friendList.add(0, friend)
                        notifyItemMoved(position, 0)
                        // 이미지 보이기
                        notifyItemChanged(0)
                    } else {
                        // 즐겨찾기 해제: 목록 재정렬
                        friendList.sortByDescending { it.isBookmarked }
                        notifyDataSetChanged()
                    }
                    true
                }
                R.id.menuItemSortByelimination -> {
                    showDeleteConfirmationDialog(view, friend, position)
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun showDeleteConfirmationDialog(view: View, friend: Friend, position: Int) {
        val builder = AlertDialog.Builder(view.context)
        builder.setTitle("삭제 확인")
        builder.setMessage("정말 삭제하시겠습니까?")

        builder.setPositiveButton("예") { _, _ ->
            // "예"를 선택한 경우에만 항목 삭제
            friendList.removeAt(position)
            notifyItemRemoved(position)
            // 저장된 데이터도 삭제
            deleteFriendFromDatabase(friend)
        }

        builder.setNegativeButton("아니오") { _, _ ->
            // "아니오"를 선택한 경우 아무 작업도 수행하지 않음
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun deleteFriendFromDatabase(friend: Friend) {
        // Firebase 데이터베이스의 루트 참조 가져오기
        val database = FirebaseDatabase.getInstance()
        val reference = database.reference

        // 친구의 경로 생성 (여기서는 "users" 경로를 사용합니다)
        val friendPath = "users/${friend.nickname}" // userId에 맞게 경로를 설정해야 합니다.

        // 해당 경로의 데이터 삭제
        reference.child(friendPath).removeValue()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RowFriendItemLayoutBinding.inflate(inflater, parent, false)
        return FriendViewHolder(binding)
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



