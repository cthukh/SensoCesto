package com.example.myappl.viewmodel

import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.AndroidViewModel
import com.example.myappl.data.ConnectionStatus
import com.example.myappl.data.TrashState
import com.example.myappl.util.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TrashViewModel(application: Application) : AndroidViewModel(application), SensorEventListener {

    private val _uiState = MutableStateFlow(TrashState())
    val uiState: StateFlow<TrashState> = _uiState.asStateFlow()

    private val sensorManager = application.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    private val proximitySensor: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_PROXIMITY)

    init {
        NotificationHelper.createNotificationChannel(application)
        val available = proximitySensor != null
        _uiState.update { it.copy(isHardwareSensorAvailable = available) }
        if (available) {
            registerProximitySensor()
        }
    }

    private fun registerProximitySensor() {
        if (proximitySensor != null && _uiState.value.isHardwareSensorEnabled) {
            sensorManager?.registerListener(
                this,
                proximitySensor,
                SensorManager.SENSOR_DELAY_UI
            )
        }
    }

    private fun unregisterProximitySensor() {
        sensorManager?.unregisterListener(this)
    }

    fun toggleHardwareSensor() {
        _uiState.update { current ->
            val newEnabled = !current.isHardwareSensorEnabled
            if (newEnabled) {
                registerProximitySensor()
            } else {
                unregisterProximitySensor()
            }
            current.copy(isHardwareSensorEnabled = newEnabled)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_PROXIMITY) {
            val distance = event.values[0]
            val maxRange = proximitySensor?.maximumRange ?: 5f
            // In Android proximity sensors, binary sensors report 0.0 for near and maxRange for far.
            val isNear = distance < maxRange

            handleProximityDetection(isNear = isNear, distance = distance)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used
    }

    fun handleProximityDetection(isNear: Boolean, distance: Float = 0f) {
        val context = getApplication<Application>().applicationContext

        _uiState.update { current ->
            val previousFill = current.fillLevel
            val newFillLevel = if (isNear && current.isProximityModeActive) 100 else current.fillLevel
            val newState = current.copy(
                isObjectNear = isNear,
                proximityDistance = distance,
                fillLevel = newFillLevel,
                isLidOpen = if (isNear && !current.isLocked && current.isProximityModeActive) true else current.isLidOpen
            )

            // Trigger notification when fill level reaches or exceeds 90%
            if (isNear && current.isProximityModeActive && previousFill < 90) {
                NotificationHelper.showTrashFullNotification(context, 100)
            }

            newState
        }
    }

    fun simulateNearProximity() {
        handleProximityDetection(isNear = true, distance = 0f)
    }

    fun simulateFarProximity() {
        handleProximityDetection(isNear = false, distance = 5f)
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
            val newMode = !current.isProximityModeActive
            current.copy(isProximityModeActive = newMode)
        }
    }

    fun simulateProximitySensor() {
        simulateNearProximity()
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

    override fun onCleared() {
        super.onCleared()
        unregisterProximitySensor()
    }
}
