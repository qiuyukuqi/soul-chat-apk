package com.soul.app.chat

import com.soul.app.api.ChatRequest
import com.soul.app.api.ChatResponse
import com.soul.app.api.SoulApiClient
import com.google.gson.Gson

class ChatManager {
    val messages = mutableListOf<Message>()
    private val gson = Gson()

    fun addMessage(msg: Message) {
        messages.add(msg)
    }

    fun getBotReply(userText: String, callback: (String) -> Unit) {
        val history = messages.takeLast(10).map {
            mapOf("role" to it.role, "content" to it.content)
        }
        val request = ChatRequest(
            messages = history + mapOf("role" to "user", "content" to userText)
        )
        SoulApiClient.send(request) { response ->
            callback(response?.choices?.firstOrNull()?.message?.content ?: "嗯...我好像走神了，重新说一遍？")
        }
    }
}
