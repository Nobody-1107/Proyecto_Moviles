package com.example.proyectomoviles.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectomoviles.model.Colaborador
import com.example.proyectomoviles.network.ApiService
import com.example.proyectomoviles.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ActualizarHabilidadesViewModel : ViewModel() {
    private val _colaborador = MutableStateFlow<Colaborador?>(null)
    val colaborador: StateFlow<Colaborador?> = _colaborador

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun cargarDatosColaborador() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null // Limpiar errores previos
            try {
                // Llamada real a la API
                val apiService = RetrofitClient.instance.create(ApiService::class.java)
                
                // Usamos el GUID válido encontrado
                val testId = "11111111-1111-1111-1111-111111111111"
                val response = apiService.getColaborador(testId) 
                
                _colaborador.value = response
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.localizedMessage}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}