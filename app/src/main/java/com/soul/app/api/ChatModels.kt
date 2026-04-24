package com.soul.app.api

import com.google.gson.annotations.SerializedName

data class ChatRequest(
    val model: String = "glm-4-flash",
    val messages: List<Map<String, String>>
)

data class ChatResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: Message
)

data class Message(
    val role: String,
    val content: String
)
