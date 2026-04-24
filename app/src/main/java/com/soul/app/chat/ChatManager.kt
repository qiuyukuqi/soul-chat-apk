package com.soul.app.chat

import com.soul.app.api.SoulApiClient

class ChatManager {
    val messages = mutableListOf<Message>()

    fun addMessage(msg: Message) {
        messages.add(msg)
    }

    fun getBotReply(context: android.content.Context, userText: String, callback: (String) -> Unit) {
        val history = messages.takeLast(10).map {
            mapOf("role" to it.role, "content" to it.content)
        }
        val fullMessages = history + mapOf("role" to "user", "content" to userText)
        SoulApiClient.send(context, fullMessages) { reply ->
            callback(reply)
        }
    }
}
