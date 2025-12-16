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
import com.example.calendar.Constants
import com.example.calendar.api.*
import com.example.calendar.models.Event
import com.example.calendar.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

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

    // Gemini API 서비스 인스턴스 생성
    val geminiService = remember { GeminiService.create() }

    val suggestedQuestions = listOf(
        "오늘 일정이 뭐야?",
        "내일 뭐 해야 해?",
        "이번 주 회의 일정 알려줘",
        "다음 주에 약속 있어?"
    )

    // 일정 정보를 문자열로 변환하는 함수
    fun formatEventsForAI(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd (E)", Locale.KOREAN)
        val timeFormat = SimpleDateFormat("HH:mm", Locale.KOREAN)

        return events.joinToString("\n") { event ->
            val dateStr = dateFormat.format(event.date)
            "- ${event.title} | 날짜: $dateStr | 시간: ${event.startTime} - ${event.endTime}" +
                    (event.location?.let { " | 장소: $it" } ?: "") +
                    (event.description?.let { " | 내용: $it" } ?: "")
        }
    }

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
                            // 사용자 메시지 추가
                            val userMessage = Message(
                                id = System.currentTimeMillis().toString(),
                                type = MessageType.USER,
                                content = inputValue
                            )
                            messages = messages + userMessage
                            val query = inputValue
                            inputValue = ""
                            isLoading = true

                            // Gemini API 호출
                            coroutineScope.launch {
                                try {
                                    // 프롬프트 생성 (일정 정보 포함)
                                    val fullPrompt = """
                                        당신은 일정 관리 AI 어시스턴트입니다.
                                        사용자의 일정 정보를 기반으로 질문에 답변해주세요.

                                        현재 등록된 일정 목록:
                                        ${formatEventsForAI()}

                                        답변 규칙:
                                        - 친절하고 자연스러운 한국어로 답변하세요
                                        - 일정 정보가 없으면 "등록된 일정이 없습니다"라고 알려주세요
                                        - 날짜와 시간을 명확하게 알려주세요
                                        - 간결하게 답변하세요 (3-4문장 이내)

                                        사용자 질문: $query
                                    """.trimIndent()

                                    // Gemini API 요청 생성
                                    val request = GeminiRequest(
                                        contents = listOf(
                                            Content(
                                                parts = listOf(Part(text = fullPrompt))
                                            )
                                        ),
                                        generationConfig = GenerationConfig(
                                            temperature = Constants.TEMPERATURE,
                                            topK = Constants.TOP_K,
                                            topP = Constants.TOP_P,
                                            maxOutputTokens = Constants.MAX_TOKENS
                                        )
                                    )

                                    // API 호출
                                    val response = geminiService.generateContent(
                                        model = Constants.GEMINI_MODEL,
                                        apiKey = Constants.GEMINI_API_KEY,
                                        request = request
                                    )

                                    val aiResponse = if (response.isSuccessful) {
                                        // 성공: AI 답변 추출
                                        response.body()?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                                            ?: "응답을 받지 못했습니다."
                                    } else {
                                        // 실패: 에러 메시지
                                        when (response.code()) {
                                            400 -> "잘못된 요청입니다. API 키 또는 요청 형식을 확인해주세요."
                                            401, 403 -> "API 키가 유효하지 않습니다. Constants.kt 파일에서 API 키를 확인해주세요."
                                            429 -> "API 사용량 한도를 초과했습니다. 잠시 후 다시 시도해주세요."
                                            500 -> "Gemini 서버 오류입니다. 잠시 후 다시 시도해주세요."
                                            else -> "오류가 발생했습니다 (코드: ${response.code()}). 다시 시도해주세요."
                                        }
                                    }

                                    // AI 메시지 추가
                                    val aiMessage = Message(
                                        id = (System.currentTimeMillis() + 1).toString(),
                                        type = MessageType.AI,
                                        content = aiResponse
                                    )
                                    messages = messages + aiMessage

                                } catch (e: Exception) {
                                    // 네트워크 오류 등 예외 처리
                                    val errorMessage = Message(
                                        id = (System.currentTimeMillis() + 1).toString(),
                                        type = MessageType.AI,
                                        content = "연결 오류가 발생했습니다.\n" +
                                                "인터넷 연결을 확인하고 Constants.kt 파일의 API 키가 올바른지 확인해주세요.\n\n" +
                                                "오류: ${e.message}"
                                    )
                                    messages = messages + errorMessage
                                } finally {
                                    isLoading = false
                                }
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