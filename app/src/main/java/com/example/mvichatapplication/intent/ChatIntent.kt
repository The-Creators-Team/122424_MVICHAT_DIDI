package com.example.mvichatapplication.intent

import com.example.mvichatapplication.model.Message

sealed class ChatIntent {
    data class SendMessage(val user: String, val content: String) : ChatIntent()
    data class ReceiveMessage(val message: Message) : ChatIntent()
}

