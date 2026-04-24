package com.soul.app.api

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

object SoulApiClient {
    private const val API_URL = "https://open.bigmodel.cn/api/paas/v4/chat/completions"
    private var cachedKey: String? = null

    fun setApiKey(key: String) { cachedKey = key }

    fun send(context: Context, messages: List<Map<String, String>>, callback: (String) -> Unit) {
        Thread {
            try {
                val apiKey = cachedKey ?: ApiKeyManager.get(context)
                if (apiKey.isBlank()) {
                    callback("⚠️ 请先在设置中配置智谱 API Key")
                    return@Thread
                }

                val model = ApiKeyManager.getModel(context)
                val request = ChatRequest(model = model, messages = messages)
                val json = com.google.gson.Gson().toJson(request)

                val url = URL(API_URL)
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Authorization", "Bearer $apiKey")
                conn.setRequestProperty("Content-Type", "application/json")
                conn.doOutput = true
                conn.connectTimeout = 30000
                conn.readTimeout = 60000

                conn.outputStream.use { os ->
                    OutputStreamWriter(os, StandardCharsets.UTF_8).use { writer ->
                        writer.write(json)
                        writer.flush()
                    }
                }

                val responseCode = conn.responseCode
                val reader = BufferedReader(InputStreamReader(
                    if (responseCode in 200..299) conn.inputStream else conn.errorStream,
                    StandardCharsets.UTF_8
                ))
                val response = reader.readText()
                reader.close()

                if (responseCode in 200..299) {
                    val chatResp = com.google.gson.Gson().fromJson(response, ChatResponse::class.java)
                    val reply = chatResp?.choices?.firstOrNull()?.message?.content
                    callback(reply ?: "服务器返回为空")
                } else {
                    callback("API错误 ($responseCode): $response")
                }
                conn.disconnect()
            } catch (e: Exception) {
                e.printStackTrace()
                callback("网络错误: ${e.message}")
            }
        }.start()
    }
}
