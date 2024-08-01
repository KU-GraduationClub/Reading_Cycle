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
import com.example.reading_cycle.R
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
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val userViewModel: UserViewModel by activityViewModels()

    // 사용자 닉네임을 위한 프로퍼티 추가
    private var userNickname: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mainActivity = activity as MainActivity
        fragmentChatListBinding = FragmentChatListBinding.inflate(inflater, container, false)
        mainActivity.showBottomNavigation()
        database = Firebase.database.reference.child("chatRooms") // Firebase 초기화

        // 초기 빈 어댑터 설정
        chatListAdapter = ChatListAdapter(emptyList(), this)
        fragmentChatListBinding.recyclerChatList.layoutManager = LinearLayoutManager(requireContext())
        fragmentChatListBinding.recyclerChatList.adapter = chatListAdapter

        return fragmentChatListBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 사용자 닉네임을 Firestore에서 가져오기
        getUserNickname(userViewModel.userIdx ?: "")

        fragmentChatListBinding.roomaddbtn.setOnClickListener {
            showAddRoomDialog()
        }

        // Firebase 데이터베이스 리스너 설정
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatRoomList.clear()
                val chatItems = mutableListOf<ChatItem>()

                for (childSnapshot in snapshot.children) {
                    val chatRoom = childSnapshot.getValue(ChatRoom::class.java)
                    if (chatRoom != null) {
                        val lastReadTimestamp = chatRoom.lastReadTimestamp
                        var unreadMessageCount = 0

                        // 읽지 않은 메시지 수 계산
                        if (lastReadTimestamp != null) {
                            for (messageSnapshot in childSnapshot.child("messages").children) {
                                val messageTimestamp = messageSnapshot.child("timestamp").getValue(String::class.java)
                                if (messageTimestamp != null && messageTimestamp > lastReadTimestamp) {
                                    unreadMessageCount++
                                }
                            }
                        }

                        // 채팅 아이템 추가
                        val chatItem = ChatItem(
                            profileImage = R.drawable.ic_launcher_foreground,
                            name = chatRoom.name ?: "Unknown",
                            lastMessage = chatRoom.lastMessage ?: "No message",
                            lastMessageTime = chatRoom.lastMessageTime ?: "Unknown Time",
                            chatRoomId = chatRoom.chatRoomId ?: "",
                            unreadMessageCount = unreadMessageCount
                        )
                        chatItems.add(chatItem)
                    } else {
                        Log.w("ChatListFragment", "Invalid ChatRoom object: $childSnapshot")
                    }
                }

                Log.d("ChatListFragment", "Loaded chat items: $chatItems")
                requireActivity().runOnUiThread {
                    chatListAdapter = ChatListAdapter(chatItems, this@ChatListFragment)
                    fragmentChatListBinding.recyclerChatList.adapter = chatListAdapter
                    chatListAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ChatListFragment", "Firebase Database error: ${error.message}")
            }
        })
    }

    // Firestore에서 사용자 닉네임 가져오는 메소드
    private fun getUserNickname(userIdx: String) {
        firestore.collection("users").document(userIdx).get()
            .addOnSuccessListener { document ->
                userNickname = document?.getString("userNickname") ?: "Unknown"
                Log.d("ChatListFragment", "Fetched userNickname: $userNickname")
            }
            .addOnFailureListener { exception ->
                Log.w("ChatListFragment", "Get failed with ", exception)
                userNickname = "Unknown"
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
            if (addName.isNotEmpty()) {
                createChatRoom(addName)
            } else {
                Toast.makeText(requireContext(), "상대방 이름을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("취소") { dialog, which -> dialog.cancel() }

        builder.show()
    }

    private fun createChatRoom(addName: String) {
        val chatRoomRef = database.push() // 새로운 채팅방 ID 생성
        val chatRoomId = chatRoomRef.key

        if (chatRoomId != null) {
            val chatRoom = ChatRoom(
                chatRoomId = chatRoomId,
                name = addName,
                lastMessage = null,
                lastMessageTime = null,
                lastReadTimestamp = null // 새로운 채팅방 생성 시 초기값 설정
            )

            chatRoomRef.setValue(chatRoom)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "채팅방이 생성되었습니다: $addName", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { exception ->
                    Log.e("ChatListFragment", "Failed to create chat room", exception)
                    Toast.makeText(requireContext(), "채팅방 생성에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(requireContext(), "채팅방 ID를 생성할 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onChatItemClicked(chatItem: ChatItem) {
        val chatRoomId = chatItem.chatRoomId
        val name = chatItem.name

        // Intent 생성 및 ChatRoomActivity로 전환
        val intent = Intent(requireContext(), ChatRoomActivity::class.java).apply {
            putExtra("chatRoomId", chatRoomId)
            putExtra("name", name)
            putExtra("userIdx", userViewModel.userIdx)
            putExtra("userNickname", userNickname.toString()) // 사용자 닉네임 추가
        }

        Log.d("IntentDebug", "Sending chatRoomId: $chatRoomId")
        Log.d("IntentDebug", "Sending name: $name")
        Log.d("IntentDebug", "Sending userNickname: $userNickname") // 로그 추가

        startActivity(intent)
    }
}
