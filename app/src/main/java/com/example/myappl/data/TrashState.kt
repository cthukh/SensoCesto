package com.example.myappl.data

data class TrashState(
    val fillLevel: Int = 20, // percentage 0 - 100
    val isLocked: Boolean = false,
    val isLidOpen: Boolean = false,
    val isProximityModeActive: Boolean = true,
    val isHardwareSensorEnabled: Boolean = true,
    val isHardwareSensorAvailable: Boolean = false,
    val isObjectNear: Boolean = false,
    val proximityDistance: Float = -1f,
    val connectionStatus: ConnectionStatus = ConnectionStatus.CONNECTED_SIMULATED,
    val lastUpdate: String = "Hace un momento"
)

enum class ConnectionStatus {
    CONNECTED_SIMULATED,
    CONNECTING_FIREBASE,
    CONNECTED_ARDUINO,
    DISCONNECTED
}
