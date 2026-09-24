package com.example.myappl.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Basurero Autónomo IoT", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(onClick = onRequestNotificationPermission) {
                        Icon(Icons.Default.Notifications, contentDescription = "Permiso de Notificaciones")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Connection Banner Card
            ConnectionStatusCard(connectionStatus = state.connectionStatus, onStatusChange = {
                viewModel.setConnectionStatus(it)
            })

            // Main Status Dashboard Card
            MainStatusCard(state = state)

            // Fill Level Slider & Controls for simulation / Arduino connection
            FillLevelCard(
                fillLevel = state.fillLevel,
                onLevelChange = { viewModel.setFillLevel(it) }
            )

            // Manual Actions & Proximity Trigger
            ControlActionsCard(
                state = state,
                onToggleLock = { viewModel.toggleLock() },
                onToggleLid = { viewModel.toggleLid() },
                onToggleProximityMode = { viewModel.toggleProximityMode() },
                onSimulateProximity = { viewModel.simulateProximitySensor() }
            )
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                    Text(text = text, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(text = "Simular Conexión a Futuro (Firebase & Arduino):", style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { onStatusChange(ConnectionStatus.CONNECTED_SIMULATED) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Simulador", fontSize = 11.sp)
                }
                OutlinedButton(
                    onClick = { onStatusChange(ConnectionStatus.CONNECTED_ARDUINO) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Arduino", fontSize = 11.sp)
                }
                OutlinedButton(
                    onClick = { onStatusChange(ConnectionStatus.CONNECTING_FIREBASE) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Firebase", fontSize = 11.sp)
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
        colors = CardDefaults.cardColors(
            containerColor = if (isFull) MaterialTheme.colorScheme.errorContainer
            else MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
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
                        Text("¡LLENO AL LÍMITE!", color = MaterialTheme.colorScheme.onError, modifier = Modifier.padding(4.dp))
                    }
                }
            }

            HorizontalDivider()

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
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun FillLevelCard(fillLevel: Int, onLevelChange: (Int) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "Nivel de llenado del Basurero", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(text = "Actual: $fillLevel% (Simulación de Sensor ultrasónico / Peso)", style = MaterialTheme.typography.bodySmall)

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
                Text("90% (Límite Alerta)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
                Text("100% (Lleno)", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
fun ControlActionsCard(
    state: TrashState,
    onToggleLock: () -> Unit,
    onToggleLid: () -> Unit,
    onToggleProximityMode: () -> Unit,
    onSimulateProximity: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "Controles y Automatización", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            // Proximity Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Sensor de Proximidad Automático", fontWeight = FontWeight.Medium)
                    Text("Abre la tapa al detectar presencia cercana", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Lid button
                Button(
                    onClick = onToggleLid,
                    enabled = !state.isLocked,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (state.isLidOpen) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = if (state.isLidOpen) Icons.Default.Close else Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (state.isLidOpen) "Cerrar Tapa" else "Abrir Tapa", fontSize = 12.sp)
                }

                // Lock button
                Button(
                    onClick = onToggleLock,
                    modifier = Modifier.weight(1f),
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
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (state.isLocked) "Desbloquear" else "Bloquear", fontSize = 12.sp)
                }
            }

            // Simulate Proximity trigger button
            OutlinedButton(
                onClick = onSimulateProximity,
                enabled = state.isProximityModeActive && !state.isLocked,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Simular Detección de Proximidad (Arduino / Sensor)")
            }

            if (state.isLocked) {
                Text(
                    text = "⚠️ El basurero está bloqueado. La tapa no puede abrirse.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
