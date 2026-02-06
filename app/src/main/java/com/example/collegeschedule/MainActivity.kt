package com.example.collegeschedule

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.example.collegeschedule.ui.favorites.FavoritesScreen
import com.example.collegeschedule.ui.schedule.ScheduleScreen
import com.example.collegeschedule.ui.theme.CollegeScheduleTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CollegeScheduleTheme {
                CollegeScheduleApp()
            }
        }
    }
}

// Простой ViewModel для обмена данными между экранами
class AppViewModel {
    private val _selectedGroup = MutableStateFlow("ИС-12")
    val selectedGroup: StateFlow<String> = _selectedGroup.asStateFlow()

    fun setSelectedGroup(group: String) {
        _selectedGroup.value = group
    }
}

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
) {
    HOME("Расписание", Icons.Default.Home),
    FAVORITES("Избранное", Icons.Default.Favorite),
    PROFILE("Профиль", Icons.Default.AccountBox),
}

@Composable
fun CollegeScheduleApp() {
    var currentDestination by remember { mutableStateOf(AppDestinations.HOME) }
    val appViewModel = remember { AppViewModel() }

    Scaffold(
        bottomBar = {
            NavigationBar {
                AppDestinations.entries.forEach { destination ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                destination.icon,
                                contentDescription = destination.label
                            )
                        },
                        label = { Text(destination.label) },
                        selected = currentDestination == destination,
                        onClick = { currentDestination = destination }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentDestination) {
                AppDestinations.HOME -> {
                    ScheduleScreen(
                        onGroupSelected = { group ->
                            appViewModel.setSelectedGroup(group)
                        }
                    )
                }

                AppDestinations.FAVORITES -> {
                    FavoritesScreen(
                        onGroupSelected = { group ->
                            appViewModel.setSelectedGroup(group)
                            currentDestination = AppDestinations.HOME
                        }
                    )
                }

                AppDestinations.PROFILE -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Профиль студента\n\n" +
                                    "Добавьте:\n" +
                                    "• ФИО\n" +
                                    "• Группу\n" +
                                    "• Аватар\n" +
                                    "• Настройки",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}

// Альтернатива без ViewModel (более простая)
@Composable
fun CollegeScheduleAppSimple() {
    var currentDestination by remember { mutableStateOf(AppDestinations.HOME) }
    var selectedGroup by remember { mutableStateOf("ИС-12") }

    Scaffold(
        bottomBar = {
            NavigationBar {
                AppDestinations.entries.forEach { destination ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                destination.icon,
                                contentDescription = destination.label
                            )
                        },
                        label = { Text(destination.label) },
                        selected = currentDestination == destination,
                        onClick = { currentDestination = destination }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentDestination) {
                AppDestinations.HOME -> {
                    // Передаем selectedGroup в ScheduleScreen
                    ScheduleScreen(
                        initialGroup = selectedGroup,
                        onGroupSelected = { group ->
                            selectedGroup = group
                        }
                    )
                }

                AppDestinations.FAVORITES -> {
                    FavoritesScreen(
                        onGroupSelected = { group ->
                            selectedGroup = group
                            currentDestination = AppDestinations.HOME
                        }
                    )
                }

                AppDestinations.PROFILE -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Профиль студента")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CollegeScheduleAppPreview() {
    CollegeScheduleTheme {
        CollegeScheduleAppSimple()
    }
}