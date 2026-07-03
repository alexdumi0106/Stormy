package com.example.weathersimulator.ui.screens.simulator

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.roundToInt
import javax.inject.Inject

data class SimulatorUiState(
    val temperature: Float = 20f,
    val humidity: Float = 50f,
    val pressure: Float = 1013f,
    val wind: Float = 10f,
    val cloudCoverage: Float = 0f
)

@HiltViewModel
class SimulatorViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(SimulatorUiState())
    val uiState: StateFlow<SimulatorUiState> = _uiState.asStateFlow()

    fun onTemperatureChange(value: Float) {
        _uiState.update {
            it.copy(temperature = value.roundToInt().toFloat())
        }
    }

    fun onHumidityChange(value: Float) {
        _uiState.update {
            it.copy(humidity = (value / 10f).roundToInt() * 10f)
        }
    }

    fun onManualPressureChange(value: Float) {
        _uiState.update {
            it.copy(pressure = value.roundToInt().toFloat())
        }
    }

    fun onSensorPressureChange(value: Float) {
        _uiState.update {
            it.copy(pressure = value)
        }
    }

    fun onWindChange(value: Float) {
        _uiState.update {
            it.copy(wind = (value / 10f).roundToInt() * 10f)
        }
    }

    fun onCloudCoverageChange(value: Float) {
        _uiState.update {
            it.copy(cloudCoverage = (value / 20f).roundToInt() * 20f)
        }
    }
}
