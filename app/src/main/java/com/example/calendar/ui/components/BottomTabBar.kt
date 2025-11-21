package com.example.calendar.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.calendar.ui.theme.Blue600
import com.example.calendar.ui.theme.Gray500
import com.example.calendar.viewmodels.TabScreen

data class TabItem(
    val screen: TabScreen,
    val icon: ImageVector,
    val label: String
)

@Composable
fun BottomTabBar(
    activeTab: TabScreen,
    onTabChange: (TabScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = listOf(
        TabItem(TabScreen.CALENDAR, Icons.Default.CalendarMonth, "캘린더"),
        TabItem(TabScreen.LIST, Icons.Default.List, "일정"),
        TabItem(TabScreen.INSIGHT, Icons.Default.TrendingUp, "인사이트"),
        TabItem(TabScreen.AI, Icons.Default.AutoAwesome, "AI 검색")
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            tabs.forEach { tab ->
                BottomTabItem(
                    icon = tab.icon,
                    label = tab.label,
                    isActive = activeTab == tab.screen,
                    onClick = { onTabChange(tab.screen) }
                )
            }
        }
    }
}

@Composable
private fun BottomTabItem(
    icon: ImageVector,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val color = if (isActive) Blue600 else Gray500

    Column(
        modifier = Modifier
            .width(80.dp)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color
            )
        }

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}
