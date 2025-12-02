package com.example.proyectomoviles.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectomoviles.model.Department
import com.example.proyectomoviles.network.ApiService
import com.example.proyectomoviles.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Data class to hold our calculated metric
data class CalculatedMetric(
    val name: String,
    val value: Float, // The calculated percentage
    val totalVacancies: Int,
    val closedVacancies: Int
)

class ReportesViewModel : ViewModel() {

    private val _vacancyCoverageMetric = MutableStateFlow<CalculatedMetric?>(null)
    val vacancyCoverageMetric: StateFlow<CalculatedMetric?> = _vacancyCoverageMetric

    private val _departments = MutableStateFlow<List<Department>>(emptyList())
    val departments: StateFlow<List<Department>> = _departments

    private val _selectedDepartment = MutableStateFlow<Department?>(null) // null means "All"
    val selectedDepartment: StateFlow<Department?> = _selectedDepartment

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val apiService = RetrofitClient.instance.create(ApiService::class.java)

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _departments.value = apiService.getDepartments()
                cargarMetricas()
            } catch (e: Exception) {
                _error.value = "Error al cargar datos iniciales: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onDepartmentSelected(department: Department?) {
        _selectedDepartment.value = department
    }

    fun cargarMetricas() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val allVacancies = apiService.getVacancies()
                val departmentId = _selectedDepartment.value?.id

                val filteredVacancies = if (departmentId == null) {
                    allVacancies
                } else {
                    allVacancies.filter { it.departmentId == departmentId }
                }

                val totalVacancies = filteredVacancies.size
                val closedVacancies = filteredVacancies.count { 
                    it.status.equals("Cerrada", ignoreCase = true) || it.status.equals("closed", ignoreCase = true)
                }

                val coveragePercentage = if (totalVacancies > 0) {
                    (closedVacancies.toFloat() / totalVacancies.toFloat()) * 100
                } else {
                    0f
                }

                _vacancyCoverageMetric.value = CalculatedMetric(
                    name = "Tasa de Cobertura de Vacantes",
                    value = coveragePercentage,
                    totalVacancies = totalVacancies,
                    closedVacancies = closedVacancies
                )

            } catch (e: Exception) {
                _error.value = "Error al calcular métricas: ${e.localizedMessage}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}