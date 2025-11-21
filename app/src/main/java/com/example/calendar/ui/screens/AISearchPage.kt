package com.example.calendar.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.calendar.models.Event
import com.example.calendar.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class Message(
    val id: String,
    val type: MessageType,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

enum class MessageType {
    USER, AI
}

@Composable
fun AISearchPage(
    events: List<Event>,
    onEventClick: (Event) -> Unit,
    modifier: Modifier = Modifier
) {
    var messages by remember { mutableStateOf<List<Message>>(emptyList()) }
    var inputValue by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val suggestedQuestions = listOf(
        "오늘 일정이 뭐야?",
        "내일 뭐 해야 해?",
        "이번 주 회의 일정 알려줘",
        "다음 주에 약속 있어?"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundGray)
    ) {
        // Header Info
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            color = Color.White,
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 1.dp
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    color = Blue100,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Blue600,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Column {
                    Text(
                        text = "AI 일정 어시스턴트",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Gray900,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "자연스러운 대화로 일정을 검색하고 관리하세요",
                        style = MaterialTheme.typography.bodySmall,
                        color = Gray500,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }

        // Messages Area
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (messages.isEmpty()) {
                item {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "이렇게 물어보세요",
                            style = MaterialTheme.typography.titleSmall,
                            color = Gray500,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                        )

                        suggestedQuestions.forEach { question ->
                            SuggestedQuestionCard(
                                question = question,
                                onClick = { inputValue = question }
                            )
                        }
                    }
                }
            } else {
                items(messages) { message ->
                    MessageBubble(message = message)
                }

                if (isLoading) {
                    item {
                        LoadingIndicator()
                    }
                }
            }
        }

        // Input Area
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = BackgroundGray,
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp, 12.dp, 20.dp, 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                TextField(
                    value = inputValue,
                    onValueChange = { inputValue = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("일정에 대해 물어보세요...") },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading
                )

                Button(
                    onClick = {
                        if (inputValue.isNotBlank() && !isLoading) {
                            val userMessage = Message(
                                id = System.currentTimeMillis().toString(),
                                type = MessageType.USER,
                                content = inputValue
                            )
                            messages = messages + userMessage
                            val query = inputValue
                            inputValue = ""
                            isLoading = true

                            // 2. LaunchedEffect 대신 coroutineScope.launch 사용
                            coroutineScope.launch {
                                delay(1500)
                                val aiMessage = Message(
                                    id = (System.currentTimeMillis() + 1).toString(),
                                    type = MessageType.AI,
                                    content = "죄송합니다. AI 검색 기능은 아직 구현되지 않았습니다. " +
                                            "실제 앱에서는 OpenAI API를 사용하여 일정을 검색할 수 있습니다."
                                )
                                messages = messages + aiMessage
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier.size(48.dp),
                    enabled = inputValue.isNotBlank() && !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (inputValue.isNotBlank() && !isLoading) Blue600 else Gray200,
                        contentColor = if (inputValue.isNotBlank() && !isLoading) Color.White else Gray300
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "전송",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SuggestedQuestionCard(
    question: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 1.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, Gray200)
    ) {
        Text(
            text = question,
            style = MaterialTheme.typography.bodyMedium,
            color = Gray900,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(14.dp, 16.dp)
        )
    }
}

@Composable
private fun MessageBubble(message: Message) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.type == MessageType.USER) {
            Arrangement.End
        } else {
            Arrangement.Start
        }
    ) {
        if (message.type == MessageType.AI) {
            Surface(
                modifier = Modifier.size(36.dp),
                color = Blue100,
                shape = CircleShape
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.SmartToy,
                        contentDescription = null,
                        tint = Blue600,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
        }

        Surface(
            modifier = Modifier.widthIn(max = 280.dp),
            color = if (message.type == MessageType.AI) Color.White else Blue600,
            shape = RoundedCornerShape(16.dp),
            shadowElevation = if (message.type == MessageType.AI) 1.dp else 0.dp
        ) {
            Text(
                text = message.content,
                style = MaterialTheme.typography.bodyMedium,
                color = if (message.type == MessageType.AI) Gray900 else Color.White,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(12.dp, 16.dp)
            )
        }

        if (message.type == MessageType.USER) {
            Spacer(modifier = Modifier.width(12.dp))
            Surface(
                modifier = Modifier.size(36.dp),
                color = Blue600,
                shape = CircleShape
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingIndicator() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(36.dp),
            color = Blue100,
            shape = CircleShape
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = null,
                    tint = Blue600,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Surface(
            color = Color.White,
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 1.dp
        ) {
            Row(
                modifier = Modifier.padding(16.dp, 12.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(3) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(8.dp),
                        color = Gray300,
                        strokeWidth = 2.dp
                    )
                }
            }
        }
    }
}
