package com.example.myappl.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.myappl.data.ConnectionStatus
import com.example.myappl.data.TrashState
import com.example.myappl.util.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TrashViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(TrashState())
    val uiState: StateFlow<TrashState> = _uiState.asStateFlow()

    init {
        NotificationHelper.createNotificationChannel(application)
    }

    fun toggleLock() {
        _uiState.update { current ->
            val newLockedState = !current.isLocked
            current.copy(
                isLocked = newLockedState,
                isLidOpen = if (newLockedState) false else current.isLidOpen
            )
        }
    }

    fun toggleLid() {
        _uiState.update { current ->
            if (current.isLocked) {
                current
            } else {
                current.copy(isLidOpen = !current.isLidOpen)
            }
        }
    }

    fun toggleProximityMode() {
        _uiState.update { current ->
            current.copy(isProximityModeActive = !current.isProximityModeActive)
        }
    }

    fun simulateProximitySensor() {
        val current = _uiState.value
        if (current.isLocked || !current.isProximityModeActive) return

        _uiState.update { it.copy(isLidOpen = true) }
    }

    fun setFillLevel(level: Int) {
        val context = getApplication<Application>().applicationContext
        _uiState.update { current ->
            val newState = current.copy(fillLevel = level)
            if (level >= 90 && current.fillLevel < 90) {
                NotificationHelper.showTrashFullNotification(context, level)
            }
            newState
        }
    }

    fun setConnectionStatus(status: ConnectionStatus) {
        _uiState.update { it.copy(connectionStatus = status) }
    }
}
