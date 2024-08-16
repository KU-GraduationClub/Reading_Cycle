package com.example.reading_cycle.chat.vm

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.reading_cycle.R
import com.example.reading_cycle.chat.adapter.MessageAdapter
import com.example.reading_cycle.chat.model.DataMessage
import com.example.reading_cycle.databinding.ActivityChatRoomBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatRoomActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatRoomBinding
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val chatRoomId = intent.getStringExtra("chatRoomId") ?: return
        val oppname = intent.getStringExtra("name") ?: "Unknown"
        val myName = intent.getStringExtra("myName") ?: "error"
        val userProfileImage = intent.getStringExtra("profileImage").toString() // 프로필 이미지 URL 추가

        // 뒤로 가기 버튼 설정
        val backButton: ImageButton = findViewById(R.id.imgBtnQuit)
        backButton.setOnClickListener {
            onBackPressed()
        }

        // 기본 ActionBar 숨김
        supportActionBar?.hide()

        // Firebase 초기화
        FirebaseApp.initializeApp(this)

        // Firebase Database 인스턴스 초기화
        val firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference

        // RecyclerView 설정
        val layoutManager = LinearLayoutManager(this)
        val recyclerViewMessages: RecyclerView = findViewById(R.id.recyclerViewMessages)
        recyclerViewMessages.layoutManager = layoutManager

        // 상대방 이름 설정
        val textOpponent: TextView = findViewById(R.id.textOpponent)
        textOpponent.text = oppname

        // 데이터베이스에서 메시지 로드
        databaseReference.child("chatRooms").child(chatRoomId).child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val dataList = mutableListOf<DataMessage>()
                    for (snapshot in dataSnapshot.children) {
                        val message = snapshot.child("message").getValue(String::class.java)
                        val timestamp = snapshot.child("timestamp").getValue(String::class.java)
                        val name = snapshot.child("name").getValue(String::class.java)

                        if (message != null && timestamp != null && name != null) {
                            val content = DataMessage(message, timestamp, name)
                            dataList.add(content)
                        }
                    }

                    // 데이터 정렬
                    dataList.sortBy {
                        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA).parse(it.timestamp)
                    }

                    // 어댑터 설정
                    messageAdapter = MessageAdapter(dataList, myName, userProfileImage)
                    recyclerViewMessages.adapter = messageAdapter
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("ChatRoomActivity", "Failed to connect to database: ${error.message}")
                }
            })

        binding.btnSubmit.setOnClickListener {
            submitMessage(chatRoomId, myName)
        }
    }

    private fun submitMessage(chatRoomId: String, myName: String) {
        val messageContent = binding.edtSend.text.toString().trim()
        if (messageContent.isNotEmpty()) {
            val currentTime = System.currentTimeMillis()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA)
            val formattedTime = dateFormat.format(Date(currentTime))

            // 새로운 메시지 추가
            val newMessageRef = databaseReference.child("chatRooms").child(chatRoomId).child("messages").push()
            val content = DataMessage(messageContent, formattedTime, myName)

            newMessageRef.setValue(content)
                .addOnSuccessListener {
                    Log.d("ChatRoomActivity", "Message sent: $content")
                    binding.edtSend.setText("") // 입력창 초기화

                    // 마지막 메시지 및 시간 업데이트
                    databaseReference.child("chatRooms").child(chatRoomId).child("users").child(myName).child("lastMessage").setValue(messageContent)
                    databaseReference.child("chatRooms").child(chatRoomId).child("users").child(myName).child("lastMessageTime").setValue(formattedTime)
                }
                .addOnFailureListener { exception ->
                    Log.e("ChatRoomActivity", "Failed to send message: ${exception.message}")
                }
        } else {
            Log.e("ChatRoomActivity", "Please enter a message.")
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val myName = intent.getStringExtra("myName") ?: return
        val chatRoomId = intent.getStringExtra("chatRoomId") ?: return
        val currentTime = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA)
        val formattedTime = dateFormat.format(Date(currentTime))

        databaseReference.child("chatRooms").child(chatRoomId).child("users").child(myName).child("lastReadTimestamp").setValue(formattedTime)
        Log.d("ChatRoomActivity", "Last read timestamp updated: $formattedTime")
    }
}
