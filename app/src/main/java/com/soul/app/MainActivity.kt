package com.soul.app

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.soul.app.chat.ChatAdapter
import com.soul.app.chat.ChatManager
import com.soul.app.chat.Message
import com.soul.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var chatManager: ChatManager
    private val messages = mutableListOf<Message>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        chatAdapter = ChatAdapter(messages)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = chatAdapter

        chatManager = ChatManager()

        binding.sendButton.setOnClickListener {
            val input = binding.inputText.text.toString().trim()
            if (input.isEmpty()) return@setOnClickListener

            // Add user message
            messages.add(Message("user", input))
            chatAdapter.notifyItemInserted(messages.size - 1)
            binding.recyclerView.scrollToPosition(messages.size - 1)
            binding.inputText.text?.clear()

            // Get bot reply
            chatManager.getBotReply(this, input) { reply ->
                runOnUiThread {
                    messages.add(Message("bot", reply))
                    chatAdapter.notifyItemInserted(messages.size - 1)
                    binding.recyclerView.scrollToPosition(messages.size - 1)
                }
            }
        }
    }
}
