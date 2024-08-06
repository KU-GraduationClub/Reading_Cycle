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

        val chatRoomId = intent.getStringExtra("chatRoomId") ?: return
        val oppname = intent.getStringExtra("name") ?: return
        val userProfileImage = intent.getStringExtra("userProfileImage")
        val userNickname = intent.getStringExtra("userNickname")

        binding.textOpponent.text = oppname
        supportActionBar?.hide()

        FirebaseApp.initializeApp(this)
        name = "이도형"

        val firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference

        binding.recyclerViewMessages.layoutManager = LinearLayoutManager(this)

        databaseReference.child("chatRooms").child(chatRoomId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val dataList = mutableListOf<DataMessage>()

                for (snapshot in dataSnapshot.children) {
                    val message = snapshot.child("message").getValue(String::class.java)
                    val timestamp = snapshot.child("timestamp").getValue(String::class.java)
                    val senderName = snapshot.child("name").getValue(String::class.java)

                    if (message != null && timestamp != null && senderName != null) {
                        val content = DataMessage(
                            message,
                            timestamp,
                            senderName,
                            userProfileImage = if (senderName != name) userProfileImage else null,
                            userNickname = if (senderName != name) userNickname else null
                        )
                        dataList.add(content)
                    }
                }

                dataList.sortBy { it.timestamp }

                messageAdapter = MessageAdapter(dataList, name)
                binding.recyclerViewMessages.adapter = messageAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ChatRoomActivity", "Failed to connect to database: ${error.message}")
            }
        })

        binding.btnSubmit.setOnClickListener {
            val messageContent = binding.edtSend.text.toString().trim()
            if (messageContent.isNotEmpty()) {
                val currentTime = System.currentTimeMillis()
                val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.KOREA)
                val formattedTime = dateFormat.format(Date())

                val content = DataMessage(messageContent, formattedTime, name)
                databaseReference.child("chatRooms").child(chatRoomId).push().setValue(content)
                    .addOnSuccessListener {
                        Log.d("ChatRoomActivity", "Data write successful: $content")
                        binding.edtSend.setText("")
                    }
                    .addOnFailureListener { exception ->
                        Log.e("ChatRoomActivity", "Data write failed: ${exception.message}")
                    }
            } else {
                Log.e("ChatRoomActivity", "Please enter a message.")
            }
        }

        val backButton: ImageButton = findViewById(R.id.imgBtnQuit)
        backButton.setOnClickListener {
            onBackPressed()
        }
    }
}
