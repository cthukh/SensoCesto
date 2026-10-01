package com.example.myappl

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import com.example.myappl.data.AppScreen
import com.example.myappl.ui.ForgotPasswordScreen
import com.example.myappl.ui.LoginScreen
import com.example.myappl.ui.MainContainerScreen
import com.example.myappl.ui.OnboardingScreen
import com.example.myappl.ui.RegisterScreen
import com.example.myappl.ui.SplashScreen
import com.example.myappl.ui.theme.SensoCestoTheme
import com.example.myappl.viewmodel.TrashViewModel

class MainActivity : ComponentActivity() {

    private val trashViewModel: TrashViewModel by viewModels()

    private var onNotificationPermissionResult: ((Boolean) -> Unit)? = null
    private var onLocationPermissionResult: ((Boolean) -> Unit)? = null

    private val requestNotificationLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        onNotificationPermissionResult?.invoke(isGranted)
    }

    private val requestLocationLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        onLocationPermissionResult?.invoke(granted)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SensoCestoTheme {
                val currentScreen by trashViewModel.currentScreen.collectAsState()

                when (currentScreen) {
                    AppScreen.SPLASH -> {
                        SplashScreen(
                            onSplashFinished = {
                                trashViewModel.navigateToScreen(AppScreen.ONBOARDING)
                            }
                        )
                    }

                    AppScreen.ONBOARDING -> {
                        OnboardingScreen(
                            onRequestNotificationPermission = { onResult ->
                                requestNotificationPermission(onResult)
                            },
                            onRequestLocationPermission = { onResult ->
                                requestLocationPermission(onResult)
                            },
                            onFinishOnboarding = {
                                trashViewModel.navigateToScreen(AppScreen.LOGIN)
                            }
                        )
                    }

                    AppScreen.LOGIN -> {
                        LoginScreen(
                            onLoginSuccess = { email ->
                                trashViewModel.loginUser(email)
                            },
                            onNavigateToRegister = {
                                trashViewModel.navigateToScreen(AppScreen.REGISTER)
                            },
                            onNavigateToForgotPassword = {
                                trashViewModel.navigateToScreen(AppScreen.FORGOT_PASSWORD)
                            }
                        )
                    }

                    AppScreen.REGISTER -> {
                        RegisterScreen(
                            onRegisterSuccess = { name, email ->
                                trashViewModel.registerUser(name, email)
                            },
                            onNavigateToLogin = {
                                trashViewModel.navigateToScreen(AppScreen.LOGIN)
                            }
                        )
                    }

                    AppScreen.FORGOT_PASSWORD -> {
                        ForgotPasswordScreen(
                            onNavigateToLogin = {
                                trashViewModel.navigateToScreen(AppScreen.LOGIN)
                            }
                        )
                    }

                    AppScreen.MAIN -> {
                        MainContainerScreen(
                            viewModel = trashViewModel,
                            onRequestNotificationPermission = {
                                requestNotificationPermission {}
                            }
                        )
                    }
                }
            }
        }
    }

    private fun requestNotificationPermission(onResult: (Boolean) -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    onResult(true)
                }
                else -> {
                    onNotificationPermissionResult = onResult
                    requestNotificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            onResult(true)
        }
    }

    private fun requestLocationPermission(onResult: (Boolean) -> Unit) {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                onResult(true)
            }
            else -> {
                onLocationPermissionResult = onResult
                requestLocationLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }
}
