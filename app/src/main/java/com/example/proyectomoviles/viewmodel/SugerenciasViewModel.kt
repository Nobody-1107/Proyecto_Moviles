package com.example.proyectomoviles.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectomoviles.model.Sugerencia
import com.example.proyectomoviles.network.ApiService
import com.example.proyectomoviles.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Este es el modelo de datos que la UI usará
data class SugerenciaUi(
    val id: Long,
    val descripcion: String,
    val collaboratorName: String
)

data class SugerenciasUiState(
    val sugerencias: List<SugerenciaUi> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class SugerenciasViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SugerenciasUiState())
    val uiState: StateFlow<SugerenciasUiState> = _uiState

    private val apiService: ApiService = RetrofitClient.instance.create(ApiService::class.java)

    init {
        loadSugerencias()
    }

    private fun loadSugerencias() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val sugerenciasRaw = apiService.getSugerencias()
                val profiles = apiService.getProfiles().associateBy { it.id } // Necesitamos los perfiles

                val sugerenciasUi = sugerenciasRaw.mapNotNull { sugerencia ->
                    val profile = sugerencia.profile ?: sugerencia.userId?.let { profiles[it] }
                    profile?.let {
                        SugerenciaUi(
                            id = sugerencia.id,
                            descripcion = sugerencia.descripcion,
                            collaboratorName = it.fullName
                        )
                    }
                }
                _uiState.value = SugerenciasUiState(sugerencias = sugerenciasUi)
            } catch (e: Exception) {
                _uiState.value = SugerenciasUiState(error = "Error al cargar sugerencias: ${e.message}")
            }
        }
    }

    fun deleteSugerencia(id: Long) {
        viewModelScope.launch {
            val originalState = _uiState.value
            // Optimistic update
            val newList = originalState.sugerencias.filter { it.id != id }
            _uiState.value = originalState.copy(sugerencias = newList)
            
            try {
                apiService.deleteSugerencia(id)
            } catch (e: Exception) {
                // Revert on error
                _uiState.value = originalState.copy(error = "Error al procesar la sugerencia: ${e.message}")
            }
        }
    }
}