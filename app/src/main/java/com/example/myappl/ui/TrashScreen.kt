package com.example.myappl.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myappl.data.ConnectionStatus
import com.example.myappl.data.TrashState
import com.example.myappl.viewmodel.TrashViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrashScreen(
    viewModel: TrashViewModel,
    onRequestNotificationPermission: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.DeleteOutline,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Text("CensoSesto IoT", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                actions = {
                    IconButton(
                        onClick = onRequestNotificationPermission,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = "Permiso de Notificaciones",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "Estado") },
                    label = { Text("Estado", fontWeight = FontWeight.Medium) },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Tune, contentDescription = "Sensores y Control") },
                    label = { Text("Sensores", fontWeight = FontWeight.Medium) },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Cloud, contentDescription = "Conexión IoT") },
                    label = { Text("Conexión", fontWeight = FontWeight.Medium) },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 }
                )
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            when (selectedTab) {
                0 -> DashboardTab(state = state, onLevelChange = { viewModel.setFillLevel(it) })
                1 -> SensorsControlTab(
                    state = state,
                    onToggleHardwareSensor = { viewModel.toggleHardwareSensor() },
                    onSimulateNear = { viewModel.simulateNearProximity() },
                    onSimulateFar = { viewModel.simulateFarProximity() },
                    onToggleLock = { viewModel.toggleLock() },
                    onToggleLid = { viewModel.toggleLid() },
                    onToggleProximityMode = { viewModel.toggleProximityMode() }
                )
                2 -> IoTConnectionTab(
                    connectionStatus = state.connectionStatus,
                    onStatusChange = { viewModel.setConnectionStatus(it) }
                )
            }
        }
    }
}

@Composable
fun DashboardTab(state: TrashState, onLevelChange: (Int) -> Unit) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Modern Welcome Header Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.AutoDelete,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        "CensoSesto Inteligente",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        "Sistema autónomo de gestión de residuos IoT",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // Main Status Dashboard Card
        MainStatusCard(state = state)

        // Fill Level Slider & Controls
        FillLevelCard(fillLevel = state.fillLevel, onLevelChange = onLevelChange)
    }
}

@Composable
fun SensorsControlTab(
    state: TrashState,
    onToggleHardwareSensor: () -> Unit,
    onSimulateNear: () -> Unit,
    onSimulateFar: () -> Unit,
    onToggleLock: () -> Unit,
    onToggleLid: () -> Unit,
    onToggleProximityMode: () -> Unit
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Proximity Sensor Card
        ProximitySensorCard(
            state = state,
            onToggleHardwareSensor = onToggleHardwareSensor,
            onSimulateNear = onSimulateNear,
            onSimulateFar = onSimulateFar
        )

        // Control Actions Card
        ControlActionsCard(
            state = state,
            onToggleLock = onToggleLock,
            onToggleLid = onToggleLid,
            onToggleProximityMode = onToggleProximityMode
        )
    }
}

@Composable
fun IoTConnectionTab(
    connectionStatus: ConnectionStatus,
    onStatusChange: (ConnectionStatus) -> Unit
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        ConnectionStatusCard(connectionStatus = connectionStatus, onStatusChange = onStatusChange)

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "Información de Red e IoT",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Text(
                    "CensoSesto se comunica en tiempo real mediante protocolos inalambricos con microcontroladores ESP32 y sincroniza métricas de capacidad con bases de datos en la nube (Firebase).",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ConnectionStatusCard(
    connectionStatus: ConnectionStatus,
    onStatusChange: (ConnectionStatus) -> Unit
) {
    val (text, color) = when (connectionStatus) {
        ConnectionStatus.CONNECTED_SIMULATED -> "Modo Simulación Activo" to MaterialTheme.colorScheme.tertiary
        ConnectionStatus.CONNECTING_FIREBASE -> "Conectando con Firebase..." to MaterialTheme.colorScheme.secondary
        ConnectionStatus.CONNECTED_ARDUINO -> "Conectado a Arduino (ESP32/Wi-Fi)" to MaterialTheme.colorScheme.primary
        ConnectionStatus.DISCONNECTED -> "Desconectado" to MaterialTheme.colorScheme.error
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Seleccionar Tipo de Conexión:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { onStatusChange(ConnectionStatus.CONNECTED_SIMULATED) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Simulador", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = { onStatusChange(ConnectionStatus.CONNECTED_ARDUINO) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Arduino", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = { onStatusChange(ConnectionStatus.CONNECTING_FIREBASE) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Firebase", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun MainStatusCard(state: TrashState) {
    val isFull = state.fillLevel >= 90
    val lidStatusText = if (state.isLidOpen) "Abierta 🟢" else "Cerrada 🔴"
    val lockStatusText = if (state.isLocked) "Bloqueado 🔒" else "Desbloqueado 🔓"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isFull) MaterialTheme.colorScheme.errorContainer
            else MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Estado del Basurero",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                if (isFull) {
                    Badge(containerColor = MaterialTheme.colorScheme.error) {
                        Text(
                            "¡LLENO AL LÍMITE!",
                            color = MaterialTheme.colorScheme.onError,
                            modifier = Modifier.padding(6.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatusItem(label = "Tapa", value = lidStatusText)
                StatusItem(label = "Seguro", value = lockStatusText)
                StatusItem(label = "Capacidad", value = "${state.fillLevel}%")
            }
        }
    }
}

@Composable
fun StatusItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun FillLevelCard(fillLevel: Int, onLevelChange: (Int) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Nivel de llenado del Basurero",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Actual: $fillLevel% (Simulación sensor ultrasónico / peso)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Slider(
                value = fillLevel.toFloat(),
                onValueChange = { onLevelChange(it.toInt()) },
                valueRange = 0f..100f,
                steps = 19
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("0% (Vacío)", style = MaterialTheme.typography.labelSmall)
                Text("90% (Límite Alerta)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                Text("100% (Lleno)", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
fun ProximitySensorCard(
    state: TrashState,
    onToggleHardwareSensor: () -> Unit,
    onSimulateNear: () -> Unit,
    onSimulateFar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (state.isObjectNear) MaterialTheme.colorScheme.errorContainer
            else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📡 Sensor de Proximidad",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (state.isObjectNear) {
                    Badge(containerColor = MaterialTheme.colorScheme.error) {
                        Text(
                            "¡OBJETO DETECTADO!",
                            color = MaterialTheme.colorScheme.onError,
                            modifier = Modifier.padding(4.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Text(
                text = "Detecta objetos cerca del teléfono para simular que el basurero está lleno al límite.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            HorizontalDivider()

            // Sensor Físico
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Sensor Físico del Teléfono", fontWeight = FontWeight.Medium)
                    val statusText = if (!state.isHardwareSensorAvailable) {
                        "No disponible (Usar simulación)"
                    } else if (state.isHardwareSensorEnabled) {
                        if (state.proximityDistance >= 0) "Lectura: ${state.proximityDistance} cm" else "Activo y escuchando"
                    } else {
                        "Desactivado"
                    }
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = state.isHardwareSensorEnabled,
                    onCheckedChange = { onToggleHardwareSensor() },
                    enabled = state.isHardwareSensorAvailable
                )
            }

            // Indicador de Estado de Objeto
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (state.isObjectNear) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = if (state.isObjectNear) Icons.Default.Warning else Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (state.isObjectNear) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = if (state.isObjectNear)
                            "Estado: Objeto Cerca 🛑 -> ¡Lleno al 100%!"
                        else
                            "Estado: Sin objetos cerca 🟢",
                        fontWeight = FontWeight.Bold,
                        color = if (state.isObjectNear) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onPrimaryContainer,
                        fontSize = 13.sp
                    )
                }
            }

            Text(
                text = "Simulación y Pruebas:",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onSimulateNear,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Objeto Cerca", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onSimulateFar,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Objeto Lejos", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ControlActionsCard(
    state: TrashState,
    onToggleLock: () -> Unit,
    onToggleLid: () -> Unit,
    onToggleProximityMode: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Controles y Automatización",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // Proximity Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Apertura por Proximidad", fontWeight = FontWeight.Medium)
                    Text("Abre la tapa automáticamente al detectar presencia", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(
                    checked = state.isProximityModeActive,
                    onCheckedChange = { onToggleProximityMode() }
                )
            }

            HorizontalDivider()

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Lid button
                Button(
                    onClick = onToggleLid,
                    enabled = !state.isLocked,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (state.isLidOpen) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = if (state.isLidOpen) Icons.Default.Close else Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (state.isLidOpen) "Cerrar Tapa" else "Abrir Tapa", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                // Lock button
                Button(
                    onClick = onToggleLock,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (state.isLocked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = if (state.isLocked) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Icon(
                        imageVector = if (state.isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (state.isLocked) "Desbloquear" else "Bloquear", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            if (state.isLocked) {
                Text(
                    text = "⚠️ El basurero está bloqueado. La tapa no puede abrirse.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
