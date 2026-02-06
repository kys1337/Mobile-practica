package com.example.collegeschedule.ui.schedule

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.RowScope.weight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.collegeschedule.data.dto.ScheduleByDateDto
import com.example.collegeschedule.data.network.RetrofitInstance
import com.example.collegeschedule.data.repository.FavoriteRepository
import com.example.collegeschedule.ui.components.GroupSelector
import com.example.collegeschedule.ui.components.LessonCard
import com.example.collegeschedule.utils.getWeekDateRange
import kotlinx.coroutines.launch
import kotlin.collections.forEach

@Composable
fun ScheduleScreen(
    initialGroup: String = "ИС-12",
    onGroupSelected: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val favoriteRepository = remember { FavoriteRepository(context) }
    val coroutineScope = rememberCoroutineScope()

    var selectedGroup by remember { mutableStateOf(initialGroup) }
    var schedule by remember { mutableStateOf<List<ScheduleByDateDto>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    // Получаем состояние избранного для текущей группы
    val isFavorite by produceState<Boolean>(
        initialValue = false,
        key1 = selectedGroup
    ) {
        MutableState.value = favoriteRepository.isFavorite(selectedGroup)
    }

    // Загружаем расписание при изменении группы
    LaunchedEffect(selectedGroup) {
        loading = true
        error = null

        try {
            val (start, end) = getWeekDateRange()
            schedule = RetrofitInstance.api.getSchedule(selectedGroup, start, end)
        } catch (e: Exception) {
            error = e.message ?: "Неизвестная ошибка"
        } finally {
            loading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {
        // Шапка с выбором группы и кнопкой избранного
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GroupSelector(
                selectedGroup = selectedGroup,
                onGroupSelected = { group ->
                    selectedGroup = group
                    onGroupSelected(group)
                },
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = {
                    coroutineScope.launch {
                        favoriteRepository.toggleFavorite(selectedGroup)
                    }
                }
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite
                    else Icons.Default.FavoriteBorder,
                    contentDescription = if (isFavorite) "Удалить из избранного"
                    else "Добавить в избранное",
                    tint = if (isFavorite) Color(0xFFE91E63) // Красный цвет для избранного
                    else MaterialTheme.colorScheme.onSurfaceVariant // Серый цвет для не избранного
                )
            }
        }

        // Контент
        when {
            loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Ошибка: $error",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            schedule.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Нет занятий на выбранную неделю")
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(schedule) { day ->
                        Column {
                            // Заголовок дня
                            Text(
                                text = "${day.lessonDate} (${day.weekday})",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(
                                    horizontal = 16.dp,
                                    vertical = 8.dp
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )

                            // Карточки занятий
                            day.lessons.forEach { lesson ->
                                LessonCard(lesson = lesson)
                            }
                        }
                    }
                }
            }
        }
    }
}