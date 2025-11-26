package com.example.proyectomoviles.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectomoviles.model.Profile // Changed to Profile
import com.example.proyectomoviles.network.ApiService
import com.example.proyectomoviles.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ActualizarHabilidadesViewModel : ViewModel() {
    // Renamed to profile
    private val _profile = MutableStateFlow<Profile?>(null)
    val profile: StateFlow<Profile?> = _profile

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // Renamed function
    fun cargarDatosProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null // Limpiar errores previos
            try {
                val apiService = RetrofitClient.instance.create(ApiService::class.java)
                
                // Usamos el GUID válido encontrado
                val testId = "11111111-1111-1111-1111-111111111111"
                // Use the new API method
                val response = apiService.getProfileById(testId) 
                
                _profile.value = response
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.localizedMessage}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}