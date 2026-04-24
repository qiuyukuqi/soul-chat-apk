package com.soul.app.api

import okhttp3.*
import java.io.IOException
import java.util.concurrent.TimeUnit

object SoulApiClient {
    // ⚠️ 请替换为你自己的智谱 API Key
    private const val API_KEY = "YOUR_ZHIPU_API_KEY"
    private const val API_URL = "https://open.bigmodel.cn/api/paas/v4/chat/completions"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    fun send(request: ChatRequest, callback: (ChatResponse?) -> Unit) {
        val json = com.google.gson.GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .create()
            .toJson(request)

        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json)
        val req = Request.Builder()
            .url(API_URL)
            .addHeader("Authorization", "Bearer $API_KEY")
            .addHeader("Content-Type", "application/json")
            .post(body)
            .build()

        client.newCall(req).enqueue(object : Callback() {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val respBody = response.body()?.string()
                    val chatResp = com.google.gson.Gson().fromJson(respBody, ChatResponse::class.java)
                    callback(chatResp)
                } catch (e: Exception) {
                    e.printStackTrace()
                    callback(null)
                }
            }
        })
    }
}
