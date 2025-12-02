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

data class DemandaLiderUiState(
    val vacancies: List<VacancyUi> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

data class VacancyUi(
    val id: Int,
    val title: String,
    val department: String,
    val status: String,
    val skills: List<String>
)

class DemandaLiderViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DemandaLiderUiState())
    val uiState = _uiState.asStateFlow()

    private val apiService = RetrofitClient.instance.create(ApiService::class.java)

    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val allSkills = apiService.getSkills().associateBy { it.id }
                val allDepartments = apiService.getDepartments().associateBy { it.id }
                val vacanciesRaw = apiService.getVacancies()

                val vacanciesUi = vacanciesRaw.map { vacancy ->
                    val departmentName = allDepartments[vacancy.departmentId]?.name ?: "Desconocido"
                    val skillNames = vacancy.vacancySkills?.mapNotNull { allSkills[it.skillId]?.name } ?: emptyList()
                    
                    VacancyUi(
                        id = vacancy.id,
                        title = vacancy.title,
                        department = departmentName,
                        status = vacancy.status ?: "N/A",
                        skills = skillNames
                    )
                }

                _uiState.update {
                    it.copy(
                        vacancies = vacanciesUi,
                        isLoading = false
                    )
                }

            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error al cargar el dashboard: ${e.message}", isLoading = false) }
            }
        }
    }
}