package com.vk.zenspend.data.repository

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences("zenspend_user_prefs", Context.MODE_PRIVATE)

    private val _userName = MutableStateFlow(prefs.getString(KEY_USER_NAME, "") ?: "")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _monthlySalary = MutableStateFlow(prefs.getFloat(KEY_MONTHLY_SALARY, 0f).toDouble())
    val monthlySalary: StateFlow<Double> = _monthlySalary.asStateFlow()

    private val _hasCompletedOnboarding = MutableStateFlow(
        prefs.getBoolean(KEY_HAS_COMPLETED_ONBOARDING, false)
    )
    val hasCompletedOnboarding: StateFlow<Boolean> = _hasCompletedOnboarding.asStateFlow()

    private val _isDarkMode = MutableStateFlow<Boolean?>(
        if (prefs.contains(KEY_DARK_MODE)) prefs.getBoolean(KEY_DARK_MODE, false) else null
    )
    val isDarkMode: StateFlow<Boolean?> = _isDarkMode.asStateFlow()

    fun setDarkMode(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DARK_MODE, enabled).apply()
        _isDarkMode.value = enabled
    }

    fun toggleDarkMode(currentIsDark: Boolean) {
        setDarkMode(!currentIsDark)
    }

    fun saveUserProfile(name: String, salary: Double) {
        prefs.edit()
            .putString(KEY_USER_NAME, name.trim())
            .putFloat(KEY_MONTHLY_SALARY, salary.toFloat())
            .apply()
        _userName.value = name.trim()
        _monthlySalary.value = salary
    }

    fun setOnboardingCompleted(completed: Boolean) {
        prefs.edit()
            .putBoolean(KEY_HAS_COMPLETED_ONBOARDING, completed)
            .apply()
        _hasCompletedOnboarding.value = completed
    }

    fun resetOnboarding() {
        prefs.edit()
            .putBoolean(KEY_HAS_COMPLETED_ONBOARDING, false)
            .apply()
        _hasCompletedOnboarding.value = false
    }

    companion object {
        private const val KEY_USER_NAME = "key_user_name"
        private const val KEY_MONTHLY_SALARY = "key_monthly_salary"
        private const val KEY_HAS_COMPLETED_ONBOARDING = "key_has_completed_onboarding"
        private const val KEY_DARK_MODE = "key_dark_mode"
    }
}
