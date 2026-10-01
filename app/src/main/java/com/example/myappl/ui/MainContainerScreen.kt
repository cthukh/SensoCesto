package com.example.myappl.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.myappl.data.MainTab
import com.example.myappl.viewmodel.TrashViewModel

@Composable
fun MainContainerScreen(
    viewModel: TrashViewModel,
    onRequestNotificationPermission: () -> Unit
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val dataState by viewModel.dataState.collectAsState()
    val eventLogs by viewModel.eventLogs.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                NavigationBarItem(
                    selected = currentTab == MainTab.DASHBOARD,
                    onClick = { viewModel.navigateToTab(MainTab.DASHBOARD) },
                    icon = { Icon(Icons.Default.Delete, contentDescription = "Inicio") },
                    label = { Text("Inicio") }
                )

                NavigationBarItem(
                    selected = currentTab == MainTab.HISTORY,
                    onClick = { viewModel.navigateToTab(MainTab.HISTORY) },
                    icon = { Icon(Icons.Default.History, contentDescription = "Historial") },
                    label = { Text("Estados/Historial") }
                )

                NavigationBarItem(
                    selected = currentTab == MainTab.PROFILE,
                    onClick = { viewModel.navigateToTab(MainTab.PROFILE) },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
                    label = { Text("Perfil") }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (currentTab) {
                MainTab.DASHBOARD -> {
                    TrashScreen(
                        viewModel = viewModel,
                        onRequestNotificationPermission = onRequestNotificationPermission
                    )
                }

                MainTab.HISTORY -> {
                    HistoryStateScreens(
                        dataState = dataState,
                        eventLogs = eventLogs,
                        onSetDataState = { viewModel.setDataState(it) },
                        onAddSampleEvent = { viewModel.addSampleEvent() },
                        onClearEvents = { viewModel.clearEvents() }
                    )
                }

                MainTab.PROFILE -> {
                    ProfileScreen(
                        userProfile = userProfile,
                        onToggleNotification = { viewModel.toggleUserProfileNotification(it) },
                        onLogout = { viewModel.logoutUser() }
                    )
                }
            }
        }
    }
}
