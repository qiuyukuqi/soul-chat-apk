package com.soul.app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.soul.app.api.ApiKeyManager
import com.soul.app.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private val models = listOf(
        "glm-4-flash" to "GLM-4-Flash (快速/便宜)",
        "glm-4" to "GLM-4 (高性能)",
        "glm-4-plus" to "GLM-4-Plus (最强)",
        "glm-3-turbo" to "GLM-3-Turbo (便宜)"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Handler(Looper.getMainLooper()).postDelayed({
            if (ApiKeyManager.hasKey(this)) {
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                showApiKeyDialog()
            }
            finish()
        }, 1500)
    }

    private fun showApiKeyDialog() {
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

        // Restore previously selected model
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
        }

        val hint = android.widget.TextView(this).apply {
            text = "获取地址: https://open.bigmodel.cn → API Keys"
            setTextColor(0xFF666666.toInt())
            textSize = 11f
            setPadding(0, 8, 0, 0)
        }

        container.addView(modelLabel)
        container.addView(spinner)
        container.addView(keyLabel)
        container.addView(editText)
        container.addView(hint)

        AlertDialog.Builder(this)
            .setTitle("配置 Soul 陪聊")
            .setView(container)
            .setCancelable(false)
            .setPositiveButton("确定") { _, _ ->
                val key = editText.text.toString().trim()
                val selectedModel = models[spinner.selectedItemPosition].first
                if (key.isNotEmpty()) {
                    ApiKeyManager.save(this@SplashActivity, key)
                    ApiKeyManager.saveModel(this@SplashActivity, selectedModel)
                    Toast.makeText(this@SplashActivity, "配置完成，已选择: ${models[spinner.selectedItemPosition].second}", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                } else {
                    Toast.makeText(this@SplashActivity, "Key 不能为空，3秒后重试", Toast.LENGTH_SHORT).show()
                    Handler(Looper.getMainLooper()).postDelayed({ showApiKeyDialog() }, 3000)
                }
            }
            .setNegativeButton("取消") { _, _ ->
                Toast.makeText(this, "需要 API Key 才能使用", Toast.LENGTH_LONG).show()
                Handler(Looper.getMainLooper()).postDelayed({ finish() }, 2000)
            }
            .show()
    }
}
