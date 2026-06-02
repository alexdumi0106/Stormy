package com.example.weathersimulator.ui.sensors.location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val locationRepo: LocationRepository,
    private val geocodingRepo: GeocodingRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LocationUiState())
    val state: StateFlow<LocationUiState> = _state

    private var timeoutJob: Job? = null
    private var startJob: Job? = null

    fun setPermission(granted: Boolean) {
        _state.update { it.copy(hasPermission = granted, error = null) }
        if (!granted) stop()
    }

    fun start() {
        if (!_state.value.hasPermission) return
        if (startJob?.isActive == true) return

        _state.update { it.copy(error = null) }

        timeoutJob?.cancel()
        timeoutJob = viewModelScope.launch {
            delay(8_000)
            if (_state.value.lat == null || _state.value.lon == null) {
                _state.update {
                    it.copy(
                        error = "Nu pot determina locatia. Activeaza GPS si incearca din nou."
                    )
                }
            }
        }

        startJob = viewModelScope.launch {
            val quickLocation = locationRepo.getSingleLocation(timeoutMs = 2_500L)

            if (quickLocation != null) {
                applyLocation(
                    lat = quickLocation.latitude,
                    lon = quickLocation.longitude,
                    accuracy = quickLocation.accuracy
                )
            }

            locationRepo.start { lat, lon, accuracy ->
                applyLocation(lat = lat, lon = lon, accuracy = accuracy)
            }
        }
    }

    private fun applyLocation(lat: Double, lon: Double, accuracy: Float?) {
        timeoutJob?.cancel()

        _state.update {
            it.copy(
                lat = lat,
                lon = lon,
                accuracyMeters = accuracy,
                lastUpdatedEpochMs = System.currentTimeMillis(),
                error = null
            )
        }

        viewModelScope.launch(Dispatchers.IO) {
            val place = geocodingRepo.reverseGeocode(lat, lon)

            _state.update {
                it.copy(
                    placeName = place ?: "Locatie detectata"
                )
            }
        }
    }

    fun stop() {
        timeoutJob?.cancel()
        startJob?.cancel()
        startJob = null
        locationRepo.stop()
    }

    override fun onCleared() {
        timeoutJob?.cancel()
        startJob?.cancel()
        locationRepo.stop()
        super.onCleared()
    }
}
