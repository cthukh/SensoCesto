package com.example.myappl.data

enum class AppScreen {
    SPLASH,
    ONBOARDING,
    LOGIN,
    REGISTER,
    FORGOT_PASSWORD,
    MAIN
}

enum class MainTab {
    DASHBOARD,
    HISTORY,
    PROFILE
}

enum class DataState {
    SUCCESS,
    LOADING,
    EMPTY,
    ERROR
}

data class UserProfile(
    val name: String = "Benjamín Pérez",
    val email: String = "benjamin@sensocesto.app",
    val deviceName: String = "SensoCesto ESP32-01",
    val firmwareVersion: String = "v2.4.1",
    val notificationsEnabled: Boolean = true,
    val proximitySoundEnabled: Boolean = true
)

data class EventLog(
    val id: String,
    val title: String,
    val description: String,
    val time: String,
    val type: EventType
)

enum class EventType {
    INFO,
    WARNING,
    ALERT
}
