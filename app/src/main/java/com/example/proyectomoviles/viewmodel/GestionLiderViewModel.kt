package com.example.proyectomoviles.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectomoviles.model.Profile
import com.example.proyectomoviles.network.ApiService
import com.example.proyectomoviles.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GestionLiderViewModel : ViewModel() {

    private val _collaborators = MutableStateFlow<List<Profile>>(emptyList())
    val collaborators: StateFlow<List<Profile>> = _collaborators.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadCollaborators() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val api = RetrofitClient.instance.create(ApiService::class.java)
                _collaborators.value = api.getProfiles()
            } catch (e: Exception) {
                _error.value = "No se pudo cargar la lista de colaboradores: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}