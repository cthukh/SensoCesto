package com.example.myappl.util

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

object VibrationHelper {
    private var isVibrating = false

    private fun getVibrator(context: Context): Vibrator? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    @SuppressLint("MissingPermission")
    fun startContinuousVibration(context: Context) {
        if (isVibrating) return
        val vibrator = getVibrator(context) ?: return
        if (!vibrator.hasVibrator()) return

        val timings = longArrayOf(0, 500, 500)
        val amplitudes = intArrayOf(0, 255, 0)

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = VibrationEffect.createWaveform(timings, amplitudes, 0)
                vibrator.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(timings, 0)
            }
            isVibrating = true
        } catch (_: Exception) {
            // Handle gracefully
        }
    }

    @SuppressLint("MissingPermission")
    fun stopVibration(context: Context) {
        val vibrator = getVibrator(context) ?: return
        try {
            vibrator.cancel()
        } catch (_: Exception) {
            // Handle gracefully
        }
        isVibrating = false
    }
}
