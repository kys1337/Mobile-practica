package com.example.collegeschedule.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.collections.forEach

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupSelector(
    selectedGroup: String,
    onGroupSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    // Временный список групп
    val groups = listOf(
        "ИС-11", "ИС-12", "ИС-13",
        "П-21", "П-22", "П-23",
        "КС-31", "КС-32", "АТ-41"
    )

    Box(modifier = modifier) {
        // Поле выбора
        OutlinedTextField(
            value = selectedGroup,
            onValueChange = { },
            label = { Text("Выберите группу") },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Выбрать группу",
                    modifier = Modifier.clickable { expanded = !expanded }
                )
            },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
        )

        // Выпадающий список - УПРОЩЕННАЯ ВЕРСИЯ
        if (expanded) {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .heightIn(max = 400.dp)
            ) {
                groups.forEach { group ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = group,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        },
                        onClick = {
                            onGroupSelected(group)
                            expanded = false
                        }
                    )
                    Divider(modifier = Modifier.padding(horizontal = 8.dp))
                }
            }
        }
    }
}