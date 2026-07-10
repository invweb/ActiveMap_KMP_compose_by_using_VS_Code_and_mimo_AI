package com.activemap.shared.resources

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AppLanguage(val code: String, val displayName: String) {
    RUSSIAN("ru", "Русский"),
    ENGLISH("en", "English"),
    GERMAN("de", "Deutsch"),
    UKRAINIAN("uk", "Українська");

    companion object {
        fun fromCode(code: String): AppLanguage {
            return entries.find { it.code == code } ?: RUSSIAN
        }
    }
}

object LocaleManager {
    private val _currentLanguage = MutableStateFlow(AppLanguage.RUSSIAN)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    private val _availableLanguages = MutableStateFlow(AppLanguage.entries.toList())
    val availableLanguages: StateFlow<List<AppLanguage>> = _availableLanguages.asStateFlow()

    fun setLanguage(language: AppLanguage) {
        _currentLanguage.value = language
    }

    fun setLanguageByCode(code: String) {
        _currentLanguage.value = AppLanguage.fromCode(code)
    }

    fun getLanguageCode(): String {
        return _currentLanguage.value.code
    }
}
