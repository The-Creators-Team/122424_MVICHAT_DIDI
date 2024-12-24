package com.example.mvichatapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.SemanticsProperties.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.unit.dp
import com.example.mvichatapplication.intent.ChatIntent
import com.example.mvichatapplication.model.Message
import com.example.mvichatapplication.ui.theme.MVIChatApplicationTheme
import com.example.mvichatapplication.ui.viewmodel.ChatViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MVIChatApplicationTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    ChatScreen()
                }
            }
        }
    }
}

@Composable
fun ChatScreen(viewModel: ChatViewModel = viewModel()) {

    val state = viewModel.state.collectAsState().value


    var text by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(state.messages) { message ->
                MessageView(message)
            }
        }


        MessageInputField(
            text = text,
            onTextChange = { newText -> text = newText },
            onSendClick = { handleSendClick(viewModel, text) }
        )


        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
        }

        state.error?.let { errorMessage ->
            Text(text = "Error: $errorMessage", color = Color.Red, modifier = Modifier.padding(top = 16.dp))
        }
    }
}

@Composable
fun MessageView(message: Message) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = "${message.user}: ${message.content}")
        Text(text = "Timestamp: ${message.timestamp}")
    }
}

@Composable
fun MessageInputField(
    text: String,
    onTextChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        TextField(
            value = text,
            onValueChange = { value -> onTextChange(value) },
            label = { Text("Type a message") },
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        )

        Button(onClick = onSendClick) {
            Text("Send")
        }
    }
}

fun handleSendClick(viewModel: ChatViewModel, message: String) {
    if (message.isNotBlank()) {

        viewModel.handleIntent(ChatIntent.SendMessage("User", message))

    }
}

@Preview
@Composable
fun PreviewChatScreen() {

    val viewModel = ChatViewModel()
    ChatScreen(viewModel)
}


