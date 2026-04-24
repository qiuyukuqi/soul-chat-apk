package com.soul.app

import android.os.Bundle
import android.view.View
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
    private val chatManager = ChatManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupRecyclerView()
        setupSendButton()
        // 默认发一条欢迎消息
        chatManager.addMessage(Message("assistant", "你好呀！我是Soul陪聊机器人~ 有趣的灵魂万里挑一，来和我聊聊吧！"))
        updateMessages()
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter(chatManager.messages)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity).apply {
                stackFromEnd = true
            }
            adapter = chatAdapter
        }
    }

    private fun setupSendButton() {
        binding.sendButton.setOnClickListener {
            val text = binding.inputText.text.toString().trim()
            if (text.isNotEmpty()) {
                chatManager.addMessage(Message("user", text))
                binding.inputText.text?.clear()
                updateMessages()
                chatManager.getBotReply(text) { reply ->
                    runOnUiThread {
                        chatManager.addMessage(Message("assistant", reply))
                        updateMessages()
                    }
                }
            }
        }
    }

    private fun updateMessages() {
        chatAdapter.notifyDataSetChanged()
        binding.recyclerView.scrollToPosition(chatManager.messages.size - 1)
    }
}
