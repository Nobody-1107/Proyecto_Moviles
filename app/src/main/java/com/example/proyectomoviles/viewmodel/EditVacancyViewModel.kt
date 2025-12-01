package com.example.proyectomoviles.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectomoviles.model.Vacancy
import com.example.proyectomoviles.network.ApiService
import com.example.proyectomoviles.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EditVacancyUiState(
    val vacancy: Vacancy? = null,
    val title: String = "",
    val description: String = "",
    val status: String = "",
    val isLoading: Boolean = true,
    val error: String? = null,
    val isSaved: Boolean = false
)

class EditVacancyViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(EditVacancyUiState())
    val uiState = _uiState.asStateFlow()

    private val apiService = RetrofitClient.instance.create(ApiService::class.java)

    fun loadVacancy(vacancyId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val vacancy = apiService.getVacancyById(vacancyId)
                _uiState.update {
                    it.copy(
                        vacancy = vacancy,
                        title = vacancy.title,
                        description = vacancy.description ?: "",
                        status = vacancy.status ?: "",
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error al cargar la vacante: ${e.message}", isLoading = false) }
            }
        }
    }

    fun onTitleChange(title: String) {
        _uiState.update { it.copy(title = title) }
    }

    fun onDescriptionChange(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun onStatusChange(status: String) {
        _uiState.update { it.copy(status = status) }
    }

    fun saveVacancy() {
        val currentState = _uiState.value
        val vacancyToUpdate = currentState.vacancy?.copy(
            title = currentState.title,
            description = currentState.description,
            status = currentState.status
        )

        if (vacancyToUpdate == null) {
            _uiState.update { it.copy(error = "No se pudo guardar, la vacante no se ha cargado.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val response = apiService.updateVacancy(vacancyToUpdate.id, vacancyToUpdate)
                if (response.isSuccessful) {
                    _uiState.update { it.copy(isSaved = true, isLoading = false) }
                } else {
                     _uiState.update { it.copy(error = "Error al guardar: ${response.message()}", isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error al guardar: ${e.message}", isLoading = false) }
            }
        }
    }
}