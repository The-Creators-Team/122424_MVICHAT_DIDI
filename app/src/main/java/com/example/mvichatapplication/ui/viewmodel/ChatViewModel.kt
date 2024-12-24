package com.example.mvichatapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvichatapplication.intent.ChatIntent
import com.example.mvichatapplication.model.ChatState
import com.example.mvichatapplication.model.Message
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val _state = MutableStateFlow(ChatState())
    val state: StateFlow<ChatState> get() = _state

    init {
        listenForMessages()
    }


    private fun listenForMessages() {
        firestore.collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    _state.value = _state.value.copy(error = exception.localizedMessage)
                    return@addSnapshotListener
                }

                val messages = snapshot?.documents?.map { document ->
                    val user = document.getString("user") ?: "Unknown"
                    val content = document.getString("content") ?: ""
                    val timestamp = document.getLong("timestamp") ?: 0L
                    Message(user, content, timestamp)
                } ?: emptyList()

                _state.value = _state.value.copy(messages = messages)
            }
    }


    fun handleIntent(intent: ChatIntent) {
        when (intent) {
            is ChatIntent.SendMessage -> sendMessage(intent.user, intent.content)
            is ChatIntent.ReceiveMessage -> {}
        }
    }

    private fun sendMessage(user: String, content: String) {
        val message = Message(user, content, System.currentTimeMillis())
        val messageData = hashMapOf(
            "user" to user,
            "content" to content,
            "timestamp" to message.timestamp
        )

        firestore.collection("messages")
            .add(messageData)
            .addOnSuccessListener {
                _state.value = _state.value.copy(isLoading = false)
            }
            .addOnFailureListener { exception ->
                _state.value = _state.value.copy(
                    error = exception.localizedMessage,
                    isLoading = false
                )
            }

        _state.value = _state.value.copy(isLoading = true)
    }
}
