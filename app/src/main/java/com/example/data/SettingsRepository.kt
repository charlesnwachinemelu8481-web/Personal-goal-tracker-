package com.example.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("milestone_settings", Context.MODE_PRIVATE)

    private val _reminderFrequency = MutableStateFlow(
        prefs.getString("reminder_frequency", "Daily Morning (8:30 AM)") ?: "Daily Morning (8:30 AM)"
    )
    val reminderFrequency: StateFlow<String> = _reminderFrequency.asStateFlow()

    private val _themePreference = MutableStateFlow(
        prefs.getString("theme_preference", "Light (#FAFAFA)") ?: "Light (#FAFAFA)"
    )
    val themePreference: StateFlow<String> = _themePreference.asStateFlow()

    private val _showMotivationalQuotes = MutableStateFlow(
        prefs.getBoolean("show_motivational_quotes", true)
    )
    val showMotivationalQuotes: StateFlow<Boolean> = _showMotivationalQuotes.asStateFlow()

    private val _celebrationEffects = MutableStateFlow(
        prefs.getBoolean("celebration_effects", true)
    )
    val celebrationEffects: StateFlow<Boolean> = _celebrationEffects.asStateFlow()

    fun setReminderFrequency(frequency: String) {
        prefs.edit().putString("reminder_frequency", frequency).apply()
        _reminderFrequency.value = frequency
    }

    fun setThemePreference(theme: String) {
        prefs.edit().putString("theme_preference", theme).apply()
        _themePreference.value = theme
    }

    fun setShowMotivationalQuotes(show: Boolean) {
        prefs.edit().putBoolean("show_motivational_quotes", show).apply()
        _showMotivationalQuotes.value = show
    }

    fun setCelebrationEffects(enabled: Boolean) {
        prefs.edit().putBoolean("celebration_effects", enabled).apply()
        _celebrationEffects.value = enabled
    }
}
