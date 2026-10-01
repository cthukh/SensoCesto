package com.example.myappl.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myappl.data.DataState
import com.example.myappl.data.EventLog
import com.example.myappl.data.EventType
import com.example.myappl.ui.theme.StatusRed
import com.example.myappl.ui.theme.StatusYellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryStateScreens(
    dataState: DataState,
    eventLogs: List<EventLog>,
    onSetDataState: (DataState) -> Unit,
    onAddSampleEvent: () -> Unit,
    onClearEvents: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial & Estados UI", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Interactive State Switcher for Testing/Demonstrating
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "🧪 Probar Pantallas de Estado:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        FilterChip(
                            selected = dataState == DataState.SUCCESS,
                            onClick = { onSetDataState(DataState.SUCCESS) },
                            label = { Text("Éxito", fontSize = 10.sp) },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = dataState == DataState.LOADING,
                            onClick = { onSetDataState(DataState.LOADING) },
                            label = { Text("Cargando", fontSize = 10.sp) },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = dataState == DataState.EMPTY,
                            onClick = {
                                onClearEvents()
                                onSetDataState(DataState.EMPTY)
                            },
                            label = { Text("Vacío", fontSize = 10.sp) },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = dataState == DataState.ERROR,
                            onClick = { onSetDataState(DataState.ERROR) },
                            label = { Text("Error", fontSize = 10.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Render State Screen based on dataState
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when (dataState) {
                    DataState.LOADING -> LoadingStateView()
                    DataState.EMPTY -> EmptyStateView(onAddSampleEvent = onAddSampleEvent)
                    DataState.ERROR -> ErrorStateView(onRetry = { onSetDataState(DataState.SUCCESS) })
                    DataState.SUCCESS -> {
                        if (eventLogs.isEmpty()) {
                            EmptyStateView(onAddSampleEvent = onAddSampleEvent)
                        } else {
                            SuccessLogListView(eventLogs = eventLogs, onAddSampleEvent = onAddSampleEvent)
                        }
                    }
                }
            }
        }
    }
}

// 1. Loading State (Skeleton Screens + Spinner)
@Composable
fun LoadingStateView() {
    val infiniteTransition = rememberInfiniteTransition(label = "skeletonPulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.5.dp,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Cargando registros del dispositivo...",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Skeleton Items
        repeat(4) {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(alpha),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
                                shape = CircleShape
                            )
                    )
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.6f)
                                .height(16.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(4.dp)
                                )
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .height(12.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(4.dp)
                                )
                        )
                    }
                }
            }
        }
    }
}

// 2. Empty State View
@Composable
fun EmptyStateView(
    onAddSampleEvent: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(100.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Inbox,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(50.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Sin Registros de Eventos",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Aún no hay actividad registrada en tu basurero autónomo. Cuando se abra la tapa o se alcance una alerta, aparecerán aquí.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onAddSampleEvent,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(48.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Crear / Simular Primer Evento", fontWeight = FontWeight.Bold)
        }
    }
}

// 3. Error State View
@Composable
fun ErrorStateView(
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.errorContainer,
            modifier = Modifier.size(100.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.CloudOff,
                    contentDescription = null,
                    tint = StatusRed,
                    modifier = Modifier.size(50.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        SuggestionChip(
            onClick = {},
            label = { Text("Error 503 • Conexión Perdida", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
            colors = SuggestionChipDefaults.suggestionChipColors(
                containerColor = StatusRed,
                labelColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Falló la Comunicación con el Servidor",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "No pudimos obtener el historial del módulo ESP32. Comprueba tu conexión a Internet o el estado Wi-Fi del basurero.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(48.dp)
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Reintentar Conexión", fontWeight = FontWeight.Bold)
        }
    }
}

// 4. Success State View (Events List)
@Composable
fun SuccessLogListView(
    eventLogs: List<EventLog>,
    onAddSampleEvent: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Registros Recientes (${eventLogs.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            IconButton(onClick = onAddSampleEvent) {
                Icon(Icons.Default.Add, contentDescription = "Añadir evento")
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(eventLogs) { event ->
                EventLogItem(event = event)
            }
        }
    }
}

@Composable
fun EventLogItem(event: EventLog) {
    val (icon, color) = when (event.type) {
        EventType.INFO -> Icons.Default.Info to MaterialTheme.colorScheme.primary
        EventType.WARNING -> Icons.Default.Warning to StatusYellow
        EventType.ALERT -> Icons.Default.Error to StatusRed
    }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = color.copy(alpha = 0.15f),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = event.title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                    Text(text = event.time, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = event.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
