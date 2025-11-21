package com.example.calendar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.calendar.models.Event
import com.example.calendar.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AddEventDialog(
    open: Boolean,
    selectedDate: Date,
    onClose: () -> Unit,
    onSave: (Event) -> Unit
) {
    if (!open) return

    var title by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate)) }
    var startTime by remember { mutableStateOf("09:00") }
    var endTime by remember { mutableStateOf("10:00") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f),
            color = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    shadowElevation = 1.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp, 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "새 일정 추가",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Gray900,
                            fontWeight = FontWeight.Bold
                        )

                        IconButton(
                            onClick = onClose,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "닫기",
                                tint = Gray700
                            )
                        }
                    }
                }

                // Form Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp, 24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Title
                    InputField(
                        label = "제목 *",
                        value = title,
                        onValueChange = { title = it },
                        placeholder = "일정 제목을 입력하세요"
                    )

                    // Date
                    InputField(
                        label = "날짜 *",
                        value = date,
                        onValueChange = { date = it },
                        placeholder = "YYYY-MM-DD"
                    )

                    // Time
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        InputField(
                            modifier = Modifier.weight(1f),
                            label = "시작 시간 *",
                            value = startTime,
                            onValueChange = { startTime = it },
                            placeholder = "HH:MM"
                        )

                        InputField(
                            modifier = Modifier.weight(1f),
                            label = "종료 시간 *",
                            value = endTime,
                            onValueChange = { endTime = it },
                            placeholder = "HH:MM"
                        )
                    }

                    // Location
                    InputField(
                        label = "장소",
                        value = location,
                        onValueChange = { location = it },
                        placeholder = "장소를 입력하세요"
                    )

                    // Description
                    InputField(
                        label = "설명",
                        value = description,
                        onValueChange = { description = it },
                        placeholder = "일정에 대한 설명을 입력하세요",
                        singleLine = false,
                        minLines = 3
                    )
                }

                // Footer
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp, 16.dp, 20.dp, 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onClose,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Gray50,
                            contentColor = Gray700
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        Text(
                            text = "취소",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Button(
                        onClick = {
                            if (title.isNotBlank() && date.isNotBlank() && startTime.isNotBlank() && endTime.isNotBlank()) {
                                val event = Event(
                                    id = System.currentTimeMillis().toString(),
                                    title = title,
                                    date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(date) ?: Date(),
                                    startTime = formatTime(startTime),
                                    endTime = formatTime(endTime),
                                    location = location.ifBlank { null },
                                    description = description.ifBlank { null }
                                )
                                onSave(event)
                                onClose()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Blue600,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        Text(
                            text = "저장",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InputField(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    singleLine: Boolean = true,
    minLines: Int = 1
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            color = Gray900,
            fontWeight = FontWeight.SemiBold
        )

        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Gray300
                )
            },
            singleLine = singleLine,
            minLines = minLines,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Gray50,
                unfocusedContainerColor = Gray50,
                disabledContainerColor = Gray50,
                focusedIndicatorColor = Gray200,
                unfocusedIndicatorColor = Gray200,
                focusedTextColor = Gray900,
                unfocusedTextColor = Gray900
            ),
            shape = RoundedCornerShape(12.dp)
        )
    }
}

private fun formatTime(time: String): String {
    val parts = time.split(":")
    if (parts.size != 2) return time

    val hour = parts[0].toIntOrNull() ?: return time
    val minute = parts[1]

    val ampm = if (hour >= 12) "PM" else "AM"
    val displayHour = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }

    return "$displayHour:$minute $ampm"
}
