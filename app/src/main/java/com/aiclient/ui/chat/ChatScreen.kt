package com.aiclient.ui.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aiclient.R
import com.aiclient.data.local.AppDatabase
import com.aiclient.data.remote.dto.ApiMessage
import com.aiclient.data.repository.AiRepository
import com.aiclient.domain.model.Chat
import com.aiclient.domain.model.Message
import com.aiclient.domain.model.MessageRole
import com.aiclient.ui.chat.components.MessageComposer
import com.aiclient.ui.chat.components.MessageItem
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    chatId: Long?,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val database = remember { AppDatabase.getInstance(context) }
    val repo = remember { AiRepository(context) }
    val scope = rememberCoroutineScope()

    var currentChatId by remember { mutableStateOf(chatId) }
    var chat by remember { mutableStateOf<Chat?>(null) }
    var isSending by remember { mutableStateOf(false) }

    val messages by database.messageDao()
        .getMessagesByChatId(currentChatId ?: 0L)
        .collectAsState(initial = emptyList())

    val listState = rememberLazyListState()

    LaunchedEffect(currentChatId) {
        if (currentChatId != null && currentChatId != 0L) {
            chat = database.chatDao().getChatById(currentChatId!!)
        } else {
            val newChat = Chat(
                title = "New Chat",
                timestamp = System.currentTimeMillis()
            )
            val newChatId = database.chatDao().insertChat(newChat)
            currentChatId = newChatId
            chat = newChat.copy(id = newChatId)
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(chat?.title ?: stringResource(R.string.new_chat)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(messages, key = { it.id }) { message ->
                    MessageItem(message = message)
                }
                if (isSending) {
                    item {
                        Text(
                            "…",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            MessageComposer(
                onSendMessage = { text ->
                    val cid = currentChatId ?: return@MessageComposer
                    scope.launch {
                        val userMessage = Message(
                            chatId = cid,
                            role = MessageRole.USER,
                            content = text
                        )
                        database.messageDao().insertMessage(userMessage)

                        chat?.let {
                            database.chatDao().updateChat(
                                it.copy(
                                    preview = text.take(100),
                                    timestamp = System.currentTimeMillis()
                                )
                            )
                        }

                        isSending = true
                        val errMsg: String? = try {
                            val history = database.messageDao()
                                .getMessagesByChatId(cid)
                                .first()
                                .map { ApiMessage(role = it.role.name.lowercase(), content = it.content) }
                            val resp = repo.complete(history)
                            val reply = resp.choices.firstOrNull()?.message?.content ?: "(empty response)"
                            database.messageDao().insertMessage(
                                Message(chatId = cid, role = MessageRole.ASSISTANT, content = reply)
                            )
                            null
                        } catch (e: Exception) {
                            e.message ?: e.toString()
                        } finally {
                            isSending = false
                        }
                        if (errMsg != null) {
                            database.messageDao().insertMessage(
                                Message(
                                    chatId = cid,
                                    role = MessageRole.ASSISTANT,
                                    content = "Error: $errMsg",
                                    error = errMsg
                                )
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
