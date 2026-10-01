package com.example.myappl.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myappl.data.ConnectionStatus
import com.example.myappl.data.TrashState
import com.example.myappl.ui.theme.StatusGreen
import com.example.myappl.ui.theme.StatusRed
import com.example.myappl.ui.theme.StatusYellow
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
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "SensoCesto",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Basurero Autónomo IoT",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                actions = {
                    FilledTonalIconButton(
                        onClick = onRequestNotificationPermission,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Permiso de Notificaciones",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Connection Banner Card
            ConnectionStatusCard(
                connectionStatus = state.connectionStatus,
                onStatusChange = { viewModel.setConnectionStatus(it) }
            )

            // 2. Main Capacity & Hero Dashboard Card
            MainStatusHeroCard(state = state)

            // 3. Quick Action Controls Grid
            ControlActionsCard(
                state = state,
                onToggleLock = { viewModel.toggleLock() },
                onToggleLid = { viewModel.toggleLid() },
                onToggleProximityMode = { viewModel.toggleProximityMode() },
                onSimulateProximity = { viewModel.simulateProximitySensor() }
            )

            // 4. Fill Level Custom Slider & Quick Presets
            FillLevelCard(
                fillLevel = state.fillLevel,
                onLevelChange = { viewModel.setFillLevel(it) }
            )

            // 5. Hardware Proximity Sensor & Monitor
            ProximitySensorCard(
                state = state,
                onToggleHardwareSensor = { viewModel.toggleHardwareSensor() },
                onSimulateNear = { viewModel.simulateNearProximity() },
                onSimulateFar = { viewModel.simulateFarProximity() }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ConnectionStatusCard(
    connectionStatus: ConnectionStatus,
    onStatusChange: (ConnectionStatus) -> Unit
) {
    val (statusText, statusColor, icon) = when (connectionStatus) {
        ConnectionStatus.CONNECTED_SIMULATED -> Triple(
            "Modo Simulación Activo",
            MaterialTheme.colorScheme.tertiary,
            Icons.Default.PhoneAndroid
        )
        ConnectionStatus.CONNECTING_FIREBASE -> Triple(
            "Conectando con Firebase Cloud...",
            StatusYellow,
            Icons.Default.Refresh
        )
        ConnectionStatus.CONNECTED_ARDUINO -> Triple(
            "Conectado a Arduino (ESP32/Wi-Fi)",
            StatusGreen,
            Icons.Default.Wifi
        )
        ConnectionStatus.DISCONNECTED -> Triple(
            "Desconectado",
            StatusRed,
            Icons.Default.Warning
        )
    }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(statusColor)
                    )
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = statusColor
                    )
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Modo de comunicación de la red:",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = connectionStatus == ConnectionStatus.CONNECTED_SIMULATED,
                    onClick = { onStatusChange(ConnectionStatus.CONNECTED_SIMULATED) },
                    label = { Text("Simulador", fontSize = 11.sp) },
                    leadingIcon = if (connectionStatus == ConnectionStatus.CONNECTED_SIMULATED) {
                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                    } else null,
                    modifier = Modifier.weight(1f)
                )

                FilterChip(
                    selected = connectionStatus == ConnectionStatus.CONNECTED_ARDUINO,
                    onClick = { onStatusChange(ConnectionStatus.CONNECTED_ARDUINO) },
                    label = { Text("ESP32 / Wi-Fi", fontSize = 11.sp) },
                    leadingIcon = if (connectionStatus == ConnectionStatus.CONNECTED_ARDUINO) {
                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                    } else null,
                    modifier = Modifier.weight(1f)
                )

                FilterChip(
                    selected = connectionStatus == ConnectionStatus.CONNECTING_FIREBASE,
                    onClick = { onStatusChange(ConnectionStatus.CONNECTING_FIREBASE) },
                    label = { Text("Firebase", fontSize = 11.sp) },
                    leadingIcon = if (connectionStatus == ConnectionStatus.CONNECTING_FIREBASE) {
                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                    } else null,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun MainStatusHeroCard(state: TrashState) {
    val isFull = state.fillLevel >= 90 || state.isObjectNear

    // Animated Gauge Color & Level
    val gaugeColor by animateColorAsState(
        targetValue = when {
            state.fillLevel >= 85 || state.isObjectNear -> StatusRed
            state.fillLevel >= 50 -> StatusYellow
            else -> StatusGreen
        },
        animationSpec = tween(durationMillis = 500)
    )

    val animatedFillProgress by animateFloatAsState(
        targetValue = state.fillLevel / 100f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "fillProgress"
    )

    val containerBgColor by animateColorAsState(
        targetValue = if (isFull) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        animationSpec = tween(durationMillis = 500)
    )

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = containerBgColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Capacidad del Basurero",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                if (isFull) {
                    SuggestionChip(
                        onClick = {},
                        label = {
                            Text(
                                "¡LLENO AL LÍMITE!",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.sp
                            )
                        },
                        icon = {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                        },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = StatusRed,
                            labelColor = Color.White,
                            iconContentColor = Color.White
                        )
                    )
                } else {
                    SuggestionChip(
                        onClick = {},
                        label = {
                            Text(
                                "Estado Normal",
                                fontWeight = FontWeight.Medium,
                                fontSize = 11.sp
                            )
                        },
                        icon = {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                        },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = StatusGreen.copy(alpha = 0.2f),
                            labelColor = StatusGreen,
                            iconContentColor = StatusGreen
                        )
                    )
                }
            }

            // Circular Visual Gauge
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(160.dp)
                    .padding(8.dp)
            ) {
                val trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 16.dp.toPx()
                    // Background track ring
                    drawCircle(
                        color = trackColor,
                        style = Stroke(width = strokeWidth)
                    )
                    // Animated progress arc
                    drawArc(
                        color = gaugeColor,
                        startAngle = -90f,
                        sweepAngle = animatedFillProgress * 360f,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "${state.fillLevel}%",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isFull) "Lleno" else "Capacidad",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Full Warning Banner when >= 90%
            AnimatedVisibility(
                visible = isFull,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Surface(
                    color = StatusRed,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = if (state.isObjectNear)
                                "🛑 ¡Sensor Detecta Objeto Cerca! El basurero requiere vaciado urgente."
                            else
                                "⚠️ Nivel crítico al ${state.fillLevel}%. Se recomienda vaciar el contenedor.",
                            color = Color.White,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

            // Quick Info Pill Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                HeroStatusPill(
                    icon = if (state.isLidOpen) Icons.Default.CheckCircle else Icons.Default.Close,
                    label = "Tapa",
                    value = if (state.isLidOpen) "Abierta" else "Cerrada",
                    active = state.isLidOpen,
                    activeColor = StatusGreen
                )

                HeroStatusPill(
                    icon = if (state.isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                    label = "Seguro",
                    value = if (state.isLocked) "Bloqueado" else "Desbloqueado",
                    active = state.isLocked,
                    activeColor = StatusRed
                )

                HeroStatusPill(
                    icon = Icons.Default.Sensors,
                    label = "Proximidad",
                    value = if (state.isProximityModeActive) "Auto ON" else "Manual",
                    active = state.isProximityModeActive,
                    activeColor = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun HeroStatusPill(
    icon: ImageVector,
    label: String,
    value: String,
    active: Boolean,
    activeColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = if (active) activeColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.size(42.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (active) activeColor else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = if (active) activeColor else MaterialTheme.colorScheme.onSurface
        )
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
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Controles y Automatización",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            // Proximity Switch Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Sensors,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "Apertura Automática por Proximidad",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Abre la tapa al detectar una persona cerca",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Switch(
                        checked = state.isProximityModeActive,
                        onCheckedChange = { onToggleProximityMode() }
                    )
                }
            }

            // Interactive Action Buttons Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Lid Toggle Button
                val lidButtonColor = if (state.isLidOpen) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                Button(
                    onClick = onToggleLid,
                    enabled = !state.isLocked,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = lidButtonColor)
                ) {
                    Icon(
                        imageVector = if (state.isLidOpen) Icons.Default.Close else Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (state.isLidOpen) "Cerrar Tapa" else "Abrir Tapa",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Lock Toggle Button
                Button(
                    onClick = onToggleLock,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (state.isLocked) StatusRed else MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = if (state.isLocked) Color.White else MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Icon(
                        imageVector = if (state.isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (state.isLocked) "Desbloquear" else "Bloquear",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Trigger Proximity Simulation Button
            OutlinedButton(
                onClick = onSimulateProximity,
                enabled = state.isProximityModeActive && !state.isLocked,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Simular Detección de Presencia (Sensor)")
            }

            if (state.isLocked) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(start = 4.dp)
                ) {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        tint = StatusRed,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "El basurero está bloqueado con seguro. Desbloquéalo para abrir la tapa.",
                        style = MaterialTheme.typography.bodySmall,
                        color = StatusRed
                    )
                }
            }
        }
    }
}

@Composable
fun FillLevelCard(
    fillLevel: Int,
    onLevelChange: (Int) -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Nivel de Llenado",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$fillLevel%",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = "Simulación de Sensor Ultrasónico / Peso en tiempo real:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Slider
            Slider(
                value = fillLevel.toFloat(),
                onValueChange = { onLevelChange(it.toInt()) },
                valueRange = 0f..100f,
                steps = 19,
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary
                )
            )

            // Quick Preset Selection Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                PresetChip(label = "0% Vacío", level = 0, currentLevel = fillLevel) { onLevelChange(0) }
                PresetChip(label = "50% Medio", level = 50, currentLevel = fillLevel) { onLevelChange(50) }
                PresetChip(label = "90% Alerta", level = 90, currentLevel = fillLevel) { onLevelChange(90) }
                PresetChip(label = "100% Lleno", level = 100, currentLevel = fillLevel) { onLevelChange(100) }
            }
        }
    }
}

@Composable
fun RowScope.PresetChip(
    label: String,
    level: Int,
    currentLevel: Int,
    onClick: () -> Unit
) {
    val selected = currentLevel == level
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.weight(1f),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
        border = if (selected) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
        )
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun ProximitySensorCard(
    state: TrashState,
    onToggleHardwareSensor: () -> Unit,
    onSimulateNear: () -> Unit,
    onSimulateFar: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (state.isObjectNear) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Sensors,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Sensor Físico del Teléfono",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (state.isObjectNear) {
                    Badge(containerColor = StatusRed) {
                        Text(
                            "¡OBJETO DETECTADO!",
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Text(
                text = "Utiliza el sensor de proximidad real de tu smartphone para simular la detección del basurero lleno al 100%.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

            // Hardware Phone Sensor Switch Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Sensor Hardware (Escuchando)",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    val statusText = if (!state.isHardwareSensorAvailable) {
                        "No disponible en este modelo (Usar Botones de Simulación)"
                    } else if (state.isHardwareSensorEnabled) {
                        if (state.proximityDistance >= 0) "Lectura actual: ${state.proximityDistance} cm" else "Activo y detectando"
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

            // Detection Banner
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (state.isObjectNear) StatusRed else MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = if (state.isObjectNear) Icons.Default.Warning else Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (state.isObjectNear) Color.White else MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = if (state.isObjectNear)
                            "Estado: Objeto Detectado Cerca 🛑 -> ¡Basurero Lleno!"
                        else
                            "Estado: Normal (Sin objetos cercanos) 🟢",
                        fontWeight = FontWeight.Bold,
                        color = if (state.isObjectNear) Color.White else MaterialTheme.colorScheme.onPrimaryContainer,
                        fontSize = 13.sp
                    )
                }
            }

            Text(
                text = "Simulación Manual del Sensor:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )

            // Manual Simulation Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onSimulateNear,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StatusRed)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Objeto Cerca (Lleno)", fontSize = 11.sp)
                }

                OutlinedButton(
                    onClick = onSimulateFar,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Objeto Lejos", fontSize = 11.sp)
                }
            }

            // Tip Box
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Consejo: En un smartphone real, acerca la mano al auricular (parte superior) para activar la prueba física.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
