package com.soul.app

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.soul.app.api.ApiKeyManager
import com.soul.app.chat.ChatAdapter
import com.soul.app.chat.ChatManager
import com.soul.app.chat.Message
import com.soul.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var chatManager: ChatManager
    private val messages = mutableListOf<Message>()

    private val models = listOf(
        "glm-4-flash" to "GLM-4-Flash (快速/便宜)",
        "glm-4" to "GLM-4 (高性能)",
        "glm-4-plus" to "GLM-4-Plus (最强)",
        "glm-3-turbo" to "GLM-3-Turbo (便宜)"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        chatAdapter = ChatAdapter(messages)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = chatAdapter

        chatManager = ChatManager()

        binding.sendButton.setOnClickListener {
            val input = binding.inputText.text.toString().trim()
            if (input.isEmpty()) return@setOnClickListener

            messages.add(Message("user", input))
            chatAdapter.notifyItemInserted(messages.size - 1)
            binding.recyclerView.scrollToPosition(messages.size - 1)
            binding.inputText.text?.clear()

            chatManager.getBotReply(this, input) { reply ->
                runOnUiThread {
                    messages.add(Message("bot", reply))
                    chatAdapter.notifyItemInserted(messages.size - 1)
                    binding.recyclerView.scrollToPosition(messages.size - 1)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_settings) {
            showSettingsDialog()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showSettingsDialog() {
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 32, 48, 0)
        }

        val modelLabel = android.widget.TextView(this).apply {
            text = "选择模型"
            setTextColor(0xFF888888.toInt())
            textSize = 12f
        }

        val spinner = Spinner(this)
        val modelNames = models.map { it.second }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, modelNames)
        spinner.adapter = adapter

        val savedModel = ApiKeyManager.getModel(this)
        val savedIndex = models.indexOfFirst { it.first == savedModel }.takeIf { it >= 0 } ?: 0
        spinner.setSelection(savedIndex)

        val keyLabel = android.widget.TextView(this).apply {
            text = "API Key"
            setTextColor(0xFF888888.toInt())
            textSize = 12f
            setPadding(0, 24, 0, 0)
        }

        val editText = android.widget.EditText(this).apply {
            hint = "请输入智谱 API Key"
            setSingleLine()
            setText(ApiKeyManager.get(this@MainActivity))
        }

        container.addView(modelLabel)
        container.addView(spinner)
        container.addView(keyLabel)
        container.addView(editText)

        AlertDialog.Builder(this)
            .setTitle("设置")
            .setView(container)
            .setPositiveButton("保存") { _, _ ->
                val key = editText.text.toString().trim()
                val selectedModel = models[spinner.selectedItemPosition].first
                if (key.isNotEmpty()) {
                    ApiKeyManager.save(this@MainActivity, key)
                    ApiKeyManager.saveModel(this@MainActivity, selectedModel)
                    Toast.makeText(this@MainActivity, "已保存: ${models[spinner.selectedItemPosition].second}", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MainActivity, "Key 不能为空", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }
}
