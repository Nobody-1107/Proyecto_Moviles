package com.example.proyectomoviles.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectomoviles.model.Profile
import com.example.proyectomoviles.network.ApiService
import com.example.proyectomoviles.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class CrearColaboradorViewModel : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _createSuccess = MutableStateFlow(false)
    val createSuccess: StateFlow<Boolean> = _createSuccess

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun crearColaborador(fullName: String, position: String, departmentId: Int?, role: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _createSuccess.value = false
            _error.value = null
            try {
                val api = RetrofitClient.instance.create(ApiService::class.java)

                val newProfile = Profile(
                    id = UUID.randomUUID().toString(), // Generamos un ID temporal o real
                    fullName = fullName,
                    position = position,
                    departmentId = departmentId,
                    role = role,
                    isAvailableForChange = false, // Por defecto no disponible al crearse
                    createdAt = null,
                    updatedAt = null,
                    profileSkills = null
                )

                api.createProfile(newProfile)
                _createSuccess.value = true

            } catch (e: Exception) {
                _error.value = "Error al crear colaborador: ${e.localizedMessage}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetSuccess() {
        _createSuccess.value = false
    }
}