package com.example.calendar.ui.components

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
fun EditEventDialog(
    open: Boolean,
    event: Event?,
    onClose: () -> Unit,
    onSave: (Event) -> Unit
) {
    if (!open || event == null) return

    var title by remember(event) { mutableStateOf(event.title) }
    var date by remember(event) { mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(event.date)) }
    var startTime by remember(event) { mutableStateOf(parseTime(event.startTime)) }
    var endTime by remember(event) { mutableStateOf(parseTime(event.endTime)) }
    var location by remember(event) { mutableStateOf(event.location ?: "") }
    var description by remember(event) { mutableStateOf(event.description ?: "") }

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
                            text = "일정 수정",
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
                    EditInputField(
                        label = "제목 *",
                        value = title,
                        onValueChange = { title = it },
                        placeholder = "일정 제목을 입력하세요"
                    )

                    EditInputField(
                        label = "날짜 *",
                        value = date,
                        onValueChange = { date = it },
                        placeholder = "YYYY-MM-DD"
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        EditInputField(
                            modifier = Modifier.weight(1f),
                            label = "시작 시간 *",
                            value = startTime,
                            onValueChange = { startTime = it },
                            placeholder = "HH:MM"
                        )

                        EditInputField(
                            modifier = Modifier.weight(1f),
                            label = "종료 시간 *",
                            value = endTime,
                            onValueChange = { endTime = it },
                            placeholder = "HH:MM"
                        )
                    }

                    EditInputField(
                        label = "장소",
                        value = location,
                        onValueChange = { location = it },
                        placeholder = "장소를 입력하세요"
                    )

                    EditInputField(
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
                                val updatedEvent = event.copy(
                                    title = title,
                                    date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(date) ?: event.date,
                                    startTime = formatTime(startTime),
                                    endTime = formatTime(endTime),
                                    location = location.ifBlank { null },
                                    description = description.ifBlank { null }
                                )
                                onSave(updatedEvent)
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
private fun EditInputField(
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

private fun parseTime(time: String): String {
    // Parse "9:00 AM" to "09:00"
    val match = Regex("""(\d+):(\d+)\s*(AM|PM)""").find(time) ?: return time
    var hour = match.groupValues[1].toIntOrNull() ?: return time
    val minute = match.groupValues[2]
    val period = match.groupValues[3]

    if (period == "PM" && hour != 12) hour += 12
    if (period == "AM" && hour == 12) hour = 0

    return String.format("%02d:%s", hour, minute)
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
