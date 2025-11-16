# Jetpack Compose InsightPage Setup Guide

## Files Created

1. **Event.kt** - Data class representing an event
2. **InsightPage.kt** - Main composable UI component with all statistics and visualizations

## Dependencies

Add these dependencies to your `build.gradle.kts` (app level):

```kotlin
dependencies {
    // Jetpack Compose BOM
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))

    // Compose UI
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")

    // Material 3
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    // Activity Compose
    implementation("androidx.activity:activity-compose:1.8.2")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    // Debug
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
```

## Gradle Configuration

In your `build.gradle.kts` (app level), ensure Compose is enabled:

```kotlin
android {
    ...
    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}
```

## Usage Example

### Basic Usage

```kotlin
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.calendar.model.Event
import com.example.calendar.ui.screens.InsightPage

@Composable
fun MyCalendarApp() {
    // Sample events
    val events = remember {
        listOf(
            Event(
                id = "1",
                title = "Team Meeting",
                date = "2025-01-15",
                startTime = "10:00 AM",
                endTime = "11:00 AM",
                location = "Conference Room A",
                description = "Weekly team sync"
            ),
            Event(
                id = "2",
                title = "Lunch with Client",
                date = "2025-01-15",
                startTime = "12:30 PM",
                endTime = "2:00 PM",
                location = "Downtown Cafe",
                description = "Discuss project requirements"
            ),
            Event(
                id = "3",
                title = "Code Review",
                date = "2025-01-16",
                startTime = "3:00 PM",
                endTime = "4:00 PM",
                location = "Office",
                description = "Review PR #123"
            )
        )
    }

    InsightPage(events = events)
}
```

### With ViewModel

```kotlin
// EventViewModel.kt
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calendar.model.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class EventViewModel : ViewModel() {
    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events.asStateFlow()

    fun loadEvents() {
        // Load events from database or API
        _events.value = listOf(
            // your events here
        )
    }
}

// In your Composable
@Composable
fun InsightScreen(viewModel: EventViewModel = viewModel()) {
    val events by viewModel.events.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadEvents()
    }

    InsightPage(events = events)
}
```

### With Navigation

```kotlin
// Navigation setup
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "insights") {
        composable("insights") {
            InsightPage(events = /* your events */)
        }
    }
}
```

## Features Implemented

### ✅ Statistics Cards
- Total events count
- This week events
- Upcoming events
- Average event duration

### ✅ Day of Week Distribution
- Custom animated bar chart
- Color-coded bars (Sunday in red, Saturday in light blue, weekdays in blue)
- Shows busiest day with count

### ✅ Time Slot Distribution
- Morning (6-12), Afternoon (12-18), Evening (18-24)
- Animated progress bars
- Color-coded by time period

### ✅ Top Locations
- Top 3 most frequent locations
- Ranked display with custom styling
- Visit count for each location

### ✅ Empty State
- Friendly message when no events exist
- Icon and helpful text

## Customization

### Changing Colors

The colors are defined inline. You can create a color theme:

```kotlin
object InsightColors {
    val Primary = Color(0xFF3182F6)
    val Secondary = Color(0xFF4DABF7)
    val Tertiary = Color(0xFF74C0FC)
    val Success = Color(0xFF20C997)
    val Warning = Color(0xFFFF8C42)
    val Error = Color(0xFFFF6B6B)
    val Purple = Color(0xFF9775FA)
}
```

### Changing Fonts

If you want to use custom fonts:

```kotlin
val CustomFontFamily = FontFamily(
    Font(R.font.pretendard_regular, FontWeight.Normal),
    Font(R.font.pretendard_semibold, FontWeight.SemiBold),
    Font(R.font.pretendard_bold, FontWeight.Bold)
)
```

## Date Format Requirements

The Event data class expects:
- **date**: ISO format `"yyyy-MM-dd"` (e.g., "2025-01-15")
- **startTime**: `"HH:mm AM/PM"` (e.g., "10:30 AM", "2:15 PM")
- **endTime**: `"HH:mm AM/PM"` (e.g., "11:30 AM", "3:15 PM")

## Performance Notes

- Statistics are calculated using `remember(events)` to avoid recalculation on recompositions
- Animations use `animateFloatAsState` for smooth transitions
- LazyColumn is used for efficient scrolling

## Troubleshooting

### Issue: Charts not animating
**Solution**: Make sure you're using Material 3 and the animations are enabled in your device's developer settings.

### Issue: Wrong time calculations
**Solution**: Verify your time strings match the format `"HH:mm AM/PM"` exactly.

### Issue: Icons not showing
**Solution**: Ensure `androidx.compose.material:material-icons-extended` is in dependencies.

## Preview

To preview in Android Studio:

```kotlin
import androidx.compose.ui.tooling.preview.Preview

@Preview(showBackground = true)
@Composable
fun InsightPagePreview() {
    MaterialTheme {
        InsightPage(events = listOf(
            Event("1", "Meeting", "2025-01-15", "10:00 AM", "11:00 AM", "Office"),
            Event("2", "Lunch", "2025-01-15", "12:00 PM", "1:00 PM", "Cafe")
        ))
    }
}
```

## Additional Resources

- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [Material 3 for Compose](https://developer.android.com/jetpack/compose/designsystems/material3)
- [Compose Animation](https://developer.android.com/jetpack/compose/animation)
