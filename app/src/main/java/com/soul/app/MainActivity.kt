package com.soul.app

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.soul.app.api.ApiKeyManager
import com.soul.app.api.SoulApiClient
import com.soul.app.chat.ChatManager
import com.soul.app.chat.Message
import com.soul.app.chat.MessageAdapter
import com.soul.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: MessageAdapter
    private lateinit var chatManager: ChatManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = MessageAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        val apiKey = ApiKeyManager.getKey(this)
        chatManager = ChatManager(apiKey)

        chatManager.setListener(object : ChatManager.ChatListener {
            override fun onResponse(message: Message) {
                runOnUiThread {
                    adapter.addMessage(message)
                    binding.recyclerView.scrollToPosition(adapter.itemCount - 1)
                }
            }

            override fun onError(error: String) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, error, Toast.LENGTH_SHORT).show()
                }
            }
        })

        binding.sendButton.setOnClickListener {
            val input = binding.inputText.text.toString().trim()
            if (input.isEmpty()) return@setOnClickListener

            adapter.addMessage(Message("user", input))
            binding.inputText.text?.clear()
            binding.recyclerView.scrollToPosition(adapter.itemCount - 1)
            binding.loadingView.visibility = View.VISIBLE
            chatManager.send(input)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        chatManager.cancel()
    }
}
