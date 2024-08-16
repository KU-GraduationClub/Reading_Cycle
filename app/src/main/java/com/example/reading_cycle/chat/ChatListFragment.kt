package com.example.reading_cycle.chat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reading_cycle.MainActivity
import com.example.reading_cycle.UserViewModel
import com.example.reading_cycle.chat.adapter.ChatListAdapter
import com.example.reading_cycle.chat.model.ChatItem
import com.example.reading_cycle.chat.model.ChatRoom
import com.example.reading_cycle.chat.vm.ChatRoomActivity
import com.example.reading_cycle.databinding.FragmentChatListBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class ChatListFragment : Fragment(), ChatListAdapter.OnChatItemClickListener {

    private lateinit var mainActivity: MainActivity
    private lateinit var fragmentChatListBinding: FragmentChatListBinding
    private lateinit var chatListAdapter: ChatListAdapter
    private val chatRoomList = mutableListOf<ChatRoom>()
    private lateinit var database: DatabaseReference
    private lateinit var firestore: FirebaseFirestore
    private val userViewModel: UserViewModel by activityViewModels()

    private var userIdx: String? = null // userIdx를 저장할 변수
    private var myName: String? = null // userNickname을 저장할 변수

    // Firebase 리스너 변수 추가
    private var chatRoomListener: ValueEventListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mainActivity = activity as MainActivity
        fragmentChatListBinding = FragmentChatListBinding.inflate(inflater, container, false)
        mainActivity.showBottomNavigation()

        userIdx = userViewModel.userIdx
        Log.d("ChatListFragment", "User Index: $userIdx")
        database = Firebase.database.reference.child("chatRooms")
        firestore = FirebaseFirestore.getInstance()

        // RecyclerView 설정
        chatListAdapter = ChatListAdapter(emptyList(), this)
        fragmentChatListBinding.recyclerChatList.layoutManager = LinearLayoutManager(requireContext())
        fragmentChatListBinding.recyclerChatList.adapter = chatListAdapter

        // Firestore에서 userNickname 검색
        userIdx?.let {
            fetchUserNickname(it)
        }

        return fragmentChatListBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentChatListBinding.roomaddbtn.setOnClickListener {
            showAddRoomDialog()
        }

        // 채팅방 리스트 로드
        loadChatRooms() // 채팅방 데이터를 초기 로드
    }

    // Firebase Database에서 채팅방 리스트를 로드하는 메서드
    private fun loadChatRooms() {
        chatRoomListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatRoomList.clear() // 기존 데이터 초기화
                var processedRooms = 0 // 처리된 방의 수

                for (childSnapshot in snapshot.children) {
                    val chatRoom = childSnapshot.getValue(ChatRoom::class.java)

                    // 채팅방에 사용자 정보가 있는지 확인
                    val usersSnapshot = childSnapshot.child("users")
                    if (chatRoom != null && usersSnapshot.hasChild(myName ?: "unknown")) {
                        // 상대방 이름 찾기
                        var otherUserName: String? = null
                        for (user in usersSnapshot.children) {
                            if (user.key != myName) {
                                otherUserName = user.key
                                break
                            }
                        }

                        // 상대방의 프로필 이미지 URL을 Firestore에서 가져오기
                        fetchUserProfileImage(otherUserName ?: "") { profileImageUrl ->
                            // 상대방의 lastMessage 및 lastMessageTime을 가져오기
                            val lastMessage = usersSnapshot.child(otherUserName ?: "").child("lastMessage").getValue(String::class.java) ?: "메시지가 존재하지 않습니다."
                            val lastMessageTime = usersSnapshot.child(otherUserName ?: "").child("lastMessageTime").getValue(String::class.java) ?: ""

                            // 채팅방 정보 업데이트
                            chatRoom.lastMessage = lastMessage
                            chatRoom.lastMessageTime = lastMessageTime

                            chatRoomList.add(chatRoom.copy(profileImage = profileImageUrl, roomName = otherUserName)) // 프로필 이미지 추가
                            processedRooms++

                            // 모든 방이 처리되면 리스트를 업데이트
                            if (processedRooms == snapshot.children.count()) {
                                updateChatList()
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ChatListFragment", "Firebase Database error: ${error.message}")
            }
        }

        database.addValueEventListener(chatRoomListener!!) // 리스너 추가
    }

    // Fragment 종료 시 리스너 해제
    override fun onDestroyView() {
        super.onDestroyView()
        chatRoomListener?.let {
            database.removeEventListener(it)
        }
    }

    private fun showAddRoomDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("상대방 이름 입력")

        val input = EditText(requireContext())
        input.inputType = android.text.InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("확인") { dialog, which ->
            val addName = input.text.toString()
            if (addName.isNotEmpty() && addName != myName) { // 내 이름을 입력하지 못하도록
                checkUserNicknameExists(addName) { exists ->
                    if (exists) {
                        createChatRoom(addName)
                    } else {
                        Toast.makeText(requireContext(), "존재하지 않는 사용자입니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "상대방 이름을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("취소") { dialog, which -> dialog.cancel() }
        builder.show()
    }

    private fun checkUserNicknameExists(nickname: String, callback: (Boolean) -> Unit) {
        firestore.collection("Users")
            .whereEqualTo("userNickname", nickname)
            .get()
            .addOnSuccessListener { result ->
                callback(result.documents.isNotEmpty())
            }
            .addOnFailureListener { exception ->
                Log.e("ChatListFragment", "Firestore error: ${exception.message}")
                Toast.makeText(requireContext(), "사용자 확인 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                callback(false)
            }
    }

    private fun fetchUserNickname(userIdx: String) {
        firestore.collection("Users")
            .whereEqualTo("userIdx", userIdx) // userIdx로 필터링
            .get()
            .addOnSuccessListener { result ->
                if (result.documents.isNotEmpty()) {
                    myName = result.documents[0].getString("userNickname") // userNickname 가져오기
                    Log.d("ChatListFragment", "Fetched myName: $myName")
                } else {
                    Log.e("ChatListFragment", "No matching user found.")
                    myName = null
                }
            }
            .addOnFailureListener { exception ->
                Log.e("ChatListFragment", "Firestore error: ${exception.message}")
            }
    }

    private fun fetchUserProfileImage(userNickname: String, callback: (String?) -> Unit) {
        firestore.collection("Users")
            .whereEqualTo("userNickname", userNickname)
            .get()
            .addOnSuccessListener { result ->
                if (result.documents.isNotEmpty()) {
                    val profileImageUrl = result.documents[0].getString("userProfileImage") // 사용자 데이터에서 프로필 이미지 URL 가져오기
                    callback(profileImageUrl)
                } else {
                    callback(null) // 값이 없을 경우 null 반환
                }
            }
            .addOnFailureListener { exception ->
                Log.e("ChatListFragment", "Firestore error: ${exception.message}")
                callback(null) // 실패할 경우 null 반환
            }
    }

    private fun createChatRoom(addName: String) {
        val chatRoomRef = database.push() // 채팅방 참조 생성
        val chatRoomId = chatRoomRef.key

        if (chatRoomId != null) {
            val chatRoom = ChatRoom(
                chatRoomId = chatRoomId,
                lastMessage = null,
                lastMessageTime = null,
                roomName = null
            )

            // 채팅방 데이터 저장
            chatRoomRef.setValue(chatRoom)
                .addOnSuccessListener {
                    // 사용자의 정보를 users로 저장
                    val usersRef = chatRoomRef.child("users")
                    val currentTime = System.currentTimeMillis()

                    // 사용자를 users에 추가
                    usersRef.child(myName ?: "unknown").setValue(mapOf(
                        "lastReadTime" to currentTime,
                        "lastMessage" to null,
                        "lastMessageTime" to null
                    ))
                    usersRef.child(addName).setValue(mapOf(
                        "lastReadTime" to currentTime,
                        "lastMessage" to null,
                        "lastMessageTime" to null
                    ))

                    Toast.makeText(requireContext(), "채팅방이 생성되었습니다: $addName", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { exception ->
                    Log.e("ChatListFragment", "채팅방 생성에 실패했습니다.", exception)
                    Toast.makeText(requireContext(), "채팅방 생성에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(requireContext(), "채팅방 ID를 생성할 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateChatList() {
        val chatItems = chatRoomList.map {
            ChatItem(
                profileImage = it.profileImage ?: "",
                name = it.roomName ?: "Unknown",
                lastMessage = it.lastMessage ?: "메시지가 존재하지 않습니다.",
                lastMessageTime = it.lastMessageTime ?: "",
                chatRoomId = it.chatRoomId ?: " "
            )
        }

        requireActivity().runOnUiThread {
            chatListAdapter.updateChatItems(chatItems) // 어댑터 데이터 업데이트
            chatListAdapter.notifyDataSetChanged() // 어댑터에 변경 사항 알리기
        }
    }

    override fun onChatItemClicked(chatItem: ChatItem) {
        val chatRoomId = chatItem.chatRoomId
        val name = chatItem.name
        val profileImage = chatItem.profileImage // 프로필 이미지 URL 가져오기

        if (myName == null) {
            Toast.makeText(requireContext(), "사용자 닉네임을 불러오는 중입니다. 잠시 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(requireContext(), ChatRoomActivity::class.java).apply {
            putExtra("chatRoomId", chatRoomId)
            putExtra("name", name)
            putExtra("profileImage", profileImage) // 프로필 이미지 URL 추가
            putExtra("myName", myName) // myName을 인텐트에 추가
        }

        Log.d("IntentDebug", "Sending chatRoomId: $chatRoomId")
        Log.d("IntentDebug", "Sending name: $name")
        Log.d("IntentDebug", "Sending myName: $myName")

        startActivity(intent)
    }

    override fun onChatItemLongClicked(chatItem: ChatItem) {
        showExitChatRoomDialog(chatItem) // 나가기 다이얼로그 표시
    }

    private fun showExitChatRoomDialog(chatItem: ChatItem) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("채팅방 나가기")
        builder.setMessage("${chatItem.name} 채팅방에서 나가시겠습니까?")

        builder.setPositiveButton("확인") { dialog, which ->
            removeUserFromChatRoom(chatItem) // 채팅방에서 사용자 제거
        }

        builder.setNegativeButton("취소") { dialog, which -> dialog.dismiss() }
        builder.show()
    }

    private fun removeUserFromChatRoom(chatItem: ChatItem) {
        val chatRoomId = chatItem.chatRoomId
        val myName = this.myName ?: return // 현재 사용자 이름 확인

        val userRef = database.child(chatRoomId).child("users").child(myName)
        userRef.removeValue()
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "채팅방에서 나갔습니다.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Log.e("ChatListFragment", "채팅방에서 사용자 제거 실패: ${exception.message}")
                Toast.makeText(requireContext(), "채팅방에서 나가는데 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
    }
}
