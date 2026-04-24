package com.soul.app

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.soul.app.api.ApiKeyManager
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

        setupToolbar()
        setupRecyclerView()
        setupSendButton()

        // 检查API Key
        if (!ApiKeyManager.hasKey(this)) {
            showApiKeyDialog()
        } else {
            showWelcomeMessage()
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
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
            if (text.isEmpty()) return@setOnClickListener

            if (!ApiKeyManager.hasKey(this@MainActivity)) {
                showApiKeyDialog()
                return@setOnClickListener
            }

            chatManager.addMessage(Message("user", text))
            binding.inputText.text?.clear()
            updateMessages()

            chatManager.getBotReply(this@MainActivity, text) { reply ->
                runOnUiThread {
                    chatManager.addMessage(Message("assistant", reply))
                    updateMessages()
                }
            }
        }
    }

    private fun showWelcomeMessage() {
        chatManager.addMessage(Message("assistant", "你好呀！我是Soul陪聊机器人~ 有趣的灵魂万里挑一，来和我聊聊吧！"))
        updateMessages()
    }

    private fun updateMessages() {
        chatAdapter.notifyDataSetChanged()
        binding.recyclerView.scrollToPosition(chatManager.messages.size - 1)
    }

    private fun showApiKeyDialog() {
        val editText = EditText(this).apply {
            hint = "请输入智谱 API Key"
            setSingleLine()
        }
        AlertDialog.Builder(this)
            .setTitle("配置 API Key")
            .setMessage("需要智谱 GLM API Key 才能使用。\n获取地址: bigmodel.cn")
            .setView(editText)
            .setCancelable(false)
            .setPositiveButton("确定") { _, _ ->
                val key = editText.text.toString().trim()
                if (key.isNotEmpty()) {
                    ApiKeyManager.save(this, key)
                    Toast.makeText(this, "API Key 已保存", Toast.LENGTH_SHORT).show()
                    showWelcomeMessage()
                } else {
                    Toast.makeText(this, "Key 不能为空", Toast.LENGTH_SHORT).show()
                    showApiKeyDialog()
                }
            }
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add(0, 1, 0, "设置 API Key")
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == 1) {
            showApiKeyDialog()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
