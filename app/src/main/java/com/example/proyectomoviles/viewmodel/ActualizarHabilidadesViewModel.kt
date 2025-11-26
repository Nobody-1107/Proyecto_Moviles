package com.example.proyectomoviles.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectomoviles.model.Profile
import com.example.proyectomoviles.network.ApiService
import com.example.proyectomoviles.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ActualizarHabilidadesViewModel : ViewModel() {
    private val _profile = MutableStateFlow<Profile?>(null)
    val profile: StateFlow<Profile?> = _profile

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _updateSuccess = MutableStateFlow(false)
    val updateSuccess: StateFlow<Boolean> = _updateSuccess

    // ID temporal hardcodeado para la demo
    private val currentUserId = "11111111-1111-1111-1111-111111111111"

    fun cargarDatosProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val apiService = RetrofitClient.instance.create(ApiService::class.java)
                val response = apiService.getProfileById(currentUserId)
                _profile.value = response
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.localizedMessage}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun guardarDisponibilidad(isAvailable: Boolean) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _updateSuccess.value = false
            
            val currentProfile = _profile.value ?: return@launch
            
            try {
                val apiService = RetrofitClient.instance.create(ApiService::class.java)
                
                // Creamos un objeto perfil actualizado (copia del actual con el nuevo estado)
                // Nota: Idealmente la API debería tener un endpoint PATCH para esto, 
                // pero usaremos PUT enviando todo el objeto como suele requerirse.
                val updatedProfile = currentProfile.copy(isAvailableForChange = isAvailable)
                
                apiService.updateProfile(currentUserId, updatedProfile)
                
                // Actualizamos el estado local si la API respondió OK
                _profile.value = updatedProfile
                _updateSuccess.value = true
                
            } catch (e: Exception) {
                _error.value = "Error al guardar: ${e.localizedMessage}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}