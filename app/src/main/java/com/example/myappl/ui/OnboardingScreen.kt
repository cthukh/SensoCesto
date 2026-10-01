package com.example.myappl.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myappl.ui.theme.StatusGreen
import com.example.myappl.ui.theme.StatusRed

data class OnboardingPage(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val badge: String
)

@Composable
fun OnboardingScreen(
    onRequestNotificationPermission: (onResult: (Boolean) -> Unit) -> Unit,
    onRequestLocationPermission: (onResult: (Boolean) -> Unit) -> Unit,
    onFinishOnboarding: () -> Unit
) {
    var currentPage by remember { mutableIntStateOf(0) }
    var notificationGranted by remember { mutableStateOf(false) }
    var locationGranted by remember { mutableStateOf(false) }
    var showMandatoryError by remember { mutableStateOf(false) }

    val allPermissionsGranted = notificationGranted && locationGranted

    val pages = listOf(
        OnboardingPage(
            title = "Control Autónomo IoT",
            description = "Monitorea la capacidad y el estado de tu basurero inteligente en tiempo real desde la comodidad de tu smartphone.",
            icon = Icons.Default.Delete,
            badge = "Paso 1 de 4"
        ),
        OnboardingPage(
            title = "Apertura por Proximidad",
            description = "Detección de presencia manos libres. La tapa abre automáticamente al acercar tus manos o residuos.",
            icon = Icons.Default.Sensors,
            badge = "Paso 2 de 4"
        ),
        OnboardingPage(
            title = "Alertas de Capacidad",
            description = "Recibe notificaciones instantáneas antes de que el basurero se desborde, asegurando un entorno limpio.",
            icon = Icons.Default.NotificationsActive,
            badge = "Paso 3 de 4"
        ),
        OnboardingPage(
            title = "Permisos Obligatorios",
            description = "Para garantizar el funcionamiento autónomo y la conexión con el basurero, es obligatorio conceder los permisos del sistema.",
            icon = Icons.Default.Security,
            badge = "Paso 4 de 4 (Requerido)"
        )
    )

    val page = pages[currentPage]

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = page.badge,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                if (currentPage < pages.size - 1) {
                    TextButton(onClick = { currentPage = pages.size - 1 }) {
                        Text("Ir a Permisos", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // Main Animated Content
            AnimatedContent(
                targetState = currentPage,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "onboardingSlide"
            ) { targetPageIdx ->
                val currentSlide = pages[targetPageIdx]
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(110.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = currentSlide.icon,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(54.dp)
                            )
                        }
                    }

                    Text(
                        text = currentSlide.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = currentSlide.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    // Mandatory Permissions Card on Last Step
                    if (targetPageIdx == 3) {
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = StatusRed,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "Permisos Requeridos (Toca para Activar):",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = StatusRed
                                    )
                                }

                                // 1. Notification Permission Button
                                Button(
                                    onClick = {
                                        notificationGranted = true
                                        showMandatoryError = false
                                        onRequestNotificationPermission { granted ->
                                            notificationGranted = granted
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (notificationGranted) StatusGreen else MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Icon(
                                        imageVector = if (notificationGranted) Icons.Default.CheckCircle else Icons.Default.Notifications,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (notificationGranted) "✓ Notificaciones Concedidas" else "1. Activar Notificaciones",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                // 2. Location Permission Button
                                Button(
                                    onClick = {
                                        locationGranted = true
                                        showMandatoryError = false
                                        onRequestLocationPermission { granted ->
                                            locationGranted = granted
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (locationGranted) StatusGreen else MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Icon(
                                        imageVector = if (locationGranted) Icons.Default.CheckCircle else Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (locationGranted) "✓ Ubicación Concedida" else "2. Activar Ubicación (ESP32 Wi-Fi)",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Mandatory Warning Alert Banner
                        AnimatedVisibility(visible = showMandatoryError) {
                            Surface(
                                color = StatusRed,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = "⚠️ Toca los botones de arriba para activar ambos permisos antes de continuar.",
                                        color = Color.White,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Navigation Controls
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Page Indicator Dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    pages.indices.forEach { index ->
                        Box(
                            modifier = Modifier
                                .size(if (index == currentPage) 24.dp else 8.dp, 8.dp)
                                .background(
                                    color = if (index == currentPage) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                                    shape = CircleShape
                                )
                        )
                    }
                }

                // Action Button
                val buttonColor = if (currentPage == 3 && allPermissionsGranted) StatusGreen else MaterialTheme.colorScheme.primary

                Button(
                    onClick = {
                        if (currentPage < pages.size - 1) {
                            currentPage++
                        } else {
                            if (allPermissionsGranted) {
                                onFinishOnboarding()
                            } else {
                                showMandatoryError = true
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
                ) {
                    Icon(
                        imageVector = if (currentPage == 3 && allPermissionsGranted) Icons.Default.Check else Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (currentPage == pages.size - 1) {
                            if (allPermissionsGranted) "✓ Permisos Listos • Comenzar" else "Comenzar (Activa Permisos Arriba)"
                        } else "Siguiente",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
