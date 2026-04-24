package com.soul.app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.soul.app.api.ApiKeyManager
import com.soul.app.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
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
        val editText = android.widget.EditText(this).apply {
            hint = "请输入智谱 API Key"
            setSingleLine()
        }
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("配置 API Key")
            .setMessage("需要智谱 GLM API Key 才能使用。\n获取地址: https://open.bigmodel.cn\n注册后点击「API Keys」创建。")
            .setView(editText)
            .setCancelable(false)
            .setPositiveButton("确定") { _, _ ->
                val key = editText.text.toString().trim()
                if (key.isNotEmpty()) {
                    ApiKeyManager.save(this, key)
                    Toast.makeText(this, "API Key 已保存", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    Toast.makeText(this, "Key 不能为空，3秒后重试", Toast.LENGTH_SHORT).show()
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
