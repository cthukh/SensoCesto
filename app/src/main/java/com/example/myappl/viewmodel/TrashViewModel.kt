package com.example.myappl.viewmodel

import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.AndroidViewModel
import com.example.myappl.data.AppScreen
import com.example.myappl.data.ConnectionStatus
import com.example.myappl.data.DataState
import com.example.myappl.data.EventLog
import com.example.myappl.data.EventType
import com.example.myappl.data.MainTab
import com.example.myappl.data.TrashState
import com.example.myappl.data.UserProfile
import com.example.myappl.util.NotificationHelper
import com.example.myappl.util.VibrationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TrashViewModel(application: Application) : AndroidViewModel(application), SensorEventListener {

    // App Navigation & Auth Flow State
    private val _currentScreen = MutableStateFlow(AppScreen.SPLASH)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _currentTab = MutableStateFlow(MainTab.DASHBOARD)
    val currentTab: StateFlow<MainTab> = _currentTab.asStateFlow()

    private val _dataState = MutableStateFlow(DataState.SUCCESS)
    val dataState: StateFlow<DataState> = _dataState.asStateFlow()

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    // History logs
    private val _eventLogs = MutableStateFlow(
        listOf(
            EventLog("1", "Apertura por Proximidad", "Presencia detectada por el sensor. Tapa abierta automáticamente.", "Hoy, 12:45 PM", EventType.INFO),
            EventLog("2", "Advertencia de Llenado", "El basurero alcanzó el 90% de su capacidad.", "Hoy, 10:30 AM", EventType.WARNING),
            EventLog("3", "Alerta: Basurero Lleno", "Límite 100% alcanzado. Notificación y vibración continua activadas.", "Ayer, 08:15 PM", EventType.ALERT),
            EventLog("4", "Seguro Activado", "Contenedor bloqueado manualmente desde la App.", "Ayer, 05:00 PM", EventType.INFO)
        )
    )
    val eventLogs: StateFlow<List<EventLog>> = _eventLogs.asStateFlow()

    // Trash IoT Hardware State
    private val _uiState = MutableStateFlow(TrashState())
    val uiState: StateFlow<TrashState> = _uiState.asStateFlow()

    private val sensorManager = application.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    private val proximitySensor: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_PROXIMITY)

    init {
        NotificationHelper.createNotificationChannel(application)
        val available = proximitySensor != null
        _uiState.update { it.copy(isHardwareSensorAvailable = available, isHardwareSensorEnabled = available) }
        if (available) {
            registerProximitySensor()
        }
    }

    // Navigation methods
    fun navigateToScreen(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun navigateToTab(tab: MainTab) {
        _currentTab.value = tab
    }

    fun setDataState(state: DataState) {
        _dataState.value = state
    }

    // User profile & auth
    fun loginUser(email: String) {
        if (email.isNotBlank()) {
            _userProfile.update { it.copy(email = email) }
        }
        _currentScreen.value = AppScreen.MAIN
    }

    fun registerUser(name: String, email: String) {
        _userProfile.update {
            it.copy(
                name = name.ifBlank { "Usuario SensoCesto" },
                email = email.ifBlank { "usuario@sensocesto.app" }
            )
        }
        _currentScreen.value = AppScreen.MAIN
    }

    fun logoutUser() {
        _currentScreen.value = AppScreen.LOGIN
        _currentTab.value = MainTab.DASHBOARD
    }

    fun toggleUserProfileNotification(enabled: Boolean) {
        _userProfile.update { it.copy(notificationsEnabled = enabled) }
    }

    fun addSampleEvent() {
        val newEvent = EventLog(
            id = System.currentTimeMillis().toString(),
            title = "Detección por Sensor de Teléfono",
            description = "Lectura de presencia capturada desde el smartphone.",
            time = "Hace un momento",
            type = EventType.INFO
        )
        _eventLogs.update { listOf(newEvent) + it }
        _dataState.value = DataState.SUCCESS
    }

    fun clearEvents() {
        _eventLogs.value = emptyList()
        _dataState.value = DataState.EMPTY
    }

    // Hardware Proximity Sensor Logic
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

            // Enhanced Detection Range: triggers at a greater distance (up to 12cm or < maxRange)
            val isNear = if (maxRange > 0f) {
                distance < maxRange || distance <= 12f
            } else {
                distance <= 12f
            }

            handleProximityDetection(isNear = isNear, distance = distance)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    fun handleProximityDetection(isNear: Boolean, distance: Float = 0f) {
        _uiState.update { current ->
            val shouldOpenLid = isNear && !current.isLocked && current.isProximityModeActive

            current.copy(
                isObjectNear = isNear,
                proximityDistance = distance,
                isLidOpen = if (shouldOpenLid) true else current.isLidOpen
            )
        }
    }

    fun simulateNearProximity() {
        handleProximityDetection(isNear = true, distance = 3.5f)
    }

    fun simulateFarProximity() {
        handleProximityDetection(isNear = false, distance = 20.0f)
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

            // Trigger continuous vibration when 100% full until emptied (< 100%)
            if (level >= 100) {
                VibrationHelper.startContinuousVibration(context)
            } else {
                VibrationHelper.stopVibration(context)
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
        VibrationHelper.stopVibration(getApplication<Application>().applicationContext)
    }
}
