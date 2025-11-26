package com.example.proyectomoviles.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectomoviles.model.*
import com.example.proyectomoviles.network.ApiService
import com.example.proyectomoviles.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GestionSeguridadViewModel : ViewModel() {

    private val _stats = MutableStateFlow<SecurityStats?>(null)
    val stats: StateFlow<SecurityStats?> = _stats

    private val _alerts = MutableStateFlow<List<SecurityAlert>>(emptyList())
    val alerts: StateFlow<List<SecurityAlert>> = _alerts

    private val _accessLogs = MutableStateFlow<List<AccessLog>>(emptyList())
    val accessLogs: StateFlow<List<AccessLog>> = _accessLogs

    private val _owaspConfig = MutableStateFlow<List<OwaspConfig>>(emptyList())
    val owaspConfig: StateFlow<List<OwaspConfig>> = _owaspConfig

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun cargarDatosSeguridad() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val api = RetrofitClient.instance.create(ApiService::class.java)

                // Intentamos cargar cada sección de forma independiente para que si una falla, las otras se muestren
                // (Aunque idealmente esto se haría con async/await para paralelo)
                
                try {
                    _stats.value = api.getSecurityStats()
                } catch (e: Exception) {
                    // Si falla, dejamos null o un valor por defecto
                }
                
                try {
                    _alerts.value = api.getSecurityAlerts()
                } catch (e: Exception) {
                    _alerts.value = emptyList()
                }
                
                try {
                    _accessLogs.value = api.getAccessLogs()
                } catch (e: Exception) {
                    _accessLogs.value = emptyList()
                }
                
                try {
                    _owaspConfig.value = api.getOwaspConfig()
                } catch (e: Exception) {
                    _owaspConfig.value = emptyList()
                }

            } catch (e: Exception) {
                _error.value = "Error general de conexión: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun actualizarConfiguracionOwasp(configId: Int, isEnabled: Boolean) {
        viewModelScope.launch {
            try {
                val api = RetrofitClient.instance.create(ApiService::class.java)
                
                // Buscamos la config actual en la lista local
                val currentConfig = _owaspConfig.value.find { it.id == configId } ?: return@launch
                
                // Creamos objeto actualizado
                val updatedConfig = currentConfig.copy(isEnabled = isEnabled)
                
                // Enviamos al backend
                api.updateOwaspConfig(configId, updatedConfig)
                
                // Actualizamos lista local
                _owaspConfig.value = _owaspConfig.value.map { 
                    if (it.id == configId) updatedConfig else it 
                }
                
            } catch (e: Exception) {
                _error.value = "Error al actualizar configuración: ${e.localizedMessage}"
            }
        }
    }
}