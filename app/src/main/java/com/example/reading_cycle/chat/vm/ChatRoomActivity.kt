package com.example.reading_cycle.chat.vm

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
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
    private var name: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatRoomBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val chatRoomId = intent.getStringExtra("chatRoomId")
        val oppname = intent.getStringExtra("name")
        // 이전 화면으로 돌아가는 뒤로가기 버튼을 설정
        val backButton: ImageButton = findViewById(R.id.imgBtnQuit)
        backButton.setOnClickListener {
            onBackPressed()
        }

        // 기본 ActionBar 숨깁니다.
        supportActionBar?.hide()

        FirebaseApp.initializeApp(this)
        name = "이도형"

        val firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerViewMessages.layoutManager = layoutManager
        binding.textOpponent.text = oppname.toString()

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val dataList = mutableListOf<DataMessage>()

                val specificPathSnapshot = dataSnapshot.child("chatRooms").child(chatRoomId.toString())

                for (snapshot in specificPathSnapshot.children) {
                    val message = snapshot.child("message").getValue(String::class.java)
                    val timestamp = snapshot.child("timestamp").getValue(String::class.java)
                    val name = snapshot.child("name").getValue(String::class.java)
                    if (message != null && timestamp != null && name != null) {
                        val content = DataMessage(message, timestamp.toString(), name)
                        dataList.add(content)
                    }
                }

                // dataList를 timestamp에 따라 정렬
                dataList.sortBy { it.timestamp }

                name = "이도형"
                messageAdapter = MessageAdapter(dataList, name)
                binding.recyclerViewMessages.adapter = messageAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MessageActivity", "Failed to connect to database: ${error.message}")
            }
        })

        binding.btnSubmit.setOnClickListener {
            val messageContent = binding.edtSend.text.toString().trim()
            if (messageContent.isNotEmpty()) {
                val currentTime = System.currentTimeMillis()
                val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.KOREA)
                val formattedTime = dateFormat.format(Date())

                val content = DataMessage(messageContent, formattedTime, name)
                databaseReference.child("chatRooms").child(chatRoomId.toString()).push().setValue(content)
                    .addOnSuccessListener {
                        Log.d("MessageActivity", "Data write successful: $content")
                        binding.edtSend.setText("")
                    }
                    .addOnFailureListener { exception ->
                        Log.e("MessageActivity", "Data write failed: ${exception.message}")
                    }
            } else {
                Log.e("MessageActivity", "Please enter a message.")
            }
        }



    }
    override fun onBackPressed() {
        super.onBackPressed()
        // 원하는 추가적인 작업을 여기에 추가할 수 있습니다.
    }
}
