package com.soul.app.api

import android.content.Context
import okhttp3.*

object SoulApiClientWrapper {
    private const val API_URL = "https://open.bigmodel.cn/api/paas/v4/chat/completions"
    private var cachedKey: String? = null

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    fun setApiKey(key: String) { cachedKey = key }

    fun send(context: Context, messages: List<Map<String, String>>, callback: (String) -> Unit) {
        val apiKey = cachedKey ?: ApiKeyManager.get(context)
        if (apiKey.isBlank()) {
            callback("⚠️ 请先在设置中配置智谱 API Key")
            return
        }

        val request = ChatRequest(model = "glm-4-flash", messages = messages)
        val json = com.google.gson.Gson().toJson(request)
        val body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"))

        val req = Request.Builder()
            .url(API_URL)
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .post(body)
            .build()

        client.newCall(req).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback("网络错误: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    val respBody = it.body?.string()
                    val chatResp = com.google.gson.Gson().fromJson(respBody, ChatResponse::class.java)
                    val reply = chatResp?.choices?.firstOrNull()?.message?.content
                    callback(reply ?: "服务器返回为空")
                }
            }
        })
    }
}
