package com.example.collegeschedule.data.repository

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.collections.toMutableSet

class FavoriteRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("favorites", Context.MODE_PRIVATE)

    private val KEY_FAVORITES = "favorite_groups"

    private val _favoritesFlow = MutableStateFlow(getFavoriteGroups())
    val favoritesFlow: StateFlow<Set<String>> = _favoritesFlow.asStateFlow()

    // Получить все избранные группы
    fun getFavoriteGroups(): Set<String> {
        return prefs.getStringSet(KEY_FAVORITES, emptySet()) ?: emptySet()
    }

    // Добавить группу в избранное
    fun addFavoriteGroup(groupName: String) {
        val current = getFavoriteGroups().toMutableSet()
        current.add(groupName)
        prefs.edit().putStringSet(KEY_FAVORITES, current).apply()
        _favoritesFlow.value = current // Обновляем Flow
    }

    // Удалить группу из избранного
    fun removeFavoriteGroup(groupName: String) {
        val current = getFavoriteGroups().toMutableSet()
        current.remove(groupName)
        prefs.edit().putStringSet(KEY_FAVORITES, current).apply()
        _favoritesFlow.value = current // Обновляем Flow
    }

    // Проверить, есть ли группа в избранном
    fun isFavorite(groupName: String): Boolean {
        return getFavoriteGroups().contains(groupName)
    }

    // Переключить состояние избранного
    fun toggleFavorite(groupName: String) {
        if (isFavorite(groupName)) {
            removeFavoriteGroup(groupName)
        } else {
            addFavoriteGroup(groupName)
        }
    }
}