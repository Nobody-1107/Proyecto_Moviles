package com.example.proyectomoviles.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectomoviles.model.Profile
import com.example.proyectomoviles.model.Vacancy
import com.example.proyectomoviles.network.ApiService
import com.example.proyectomoviles.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

// Modelo UI simplificado para la vista de demanda
data class VacancyUi(
    val id: Int,
    val title: String,
    val department: String,
    val skills: List<String>,
    val status: String,
    val coverage: Float 
)

class DemandaLiderViewModel : ViewModel() {
    
    private val _vacancies = MutableStateFlow<List<VacancyUi>>(emptyList())
    val vacancies: StateFlow<List<VacancyUi>> = _vacancies

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _createSuccess = MutableStateFlow(false)
    val createSuccess: StateFlow<Boolean> = _createSuccess

    fun cargarVacantes() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val api = RetrofitClient.instance.create(ApiService::class.java)
                
                val vacanciesRaw = api.getVacancies()
                val departments = try { api.getDepartments().associateBy { it.id } } catch (e: Exception) { emptyMap() }
                val skills = try { api.getSkills().associateBy { it.id } } catch (e: Exception) { emptyMap() }

                val uiModels = vacanciesRaw.map { vac ->
                    val deptName = departments[vac.departmentId]?.name ?: "Desconocido"
                    val skillNames = vac.vacancySkills?.mapNotNull { vs -> skills[vs.skillId]?.name } ?: emptyList()

                    VacancyUi(
                        id = vac.id,
                        title = vac.title,
                        department = deptName,
                        skills = skillNames,
                        status = vac.status ?: "Abierta",
                        coverage = 0.5f 
                    )
                }
                _vacancies.value = uiModels

            } catch (e: Exception) {
                _error.value = "Error al cargar vacantes: ${e.localizedMessage}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun crearVacante(title: String, departmentId: Int?, description: String, status: String = "Abierta") {
        viewModelScope.launch {
            _isLoading.value = true
            _createSuccess.value = false
            _error.value = null
            try {
                val api = RetrofitClient.instance.create(ApiService::class.java)
                
                // --- SOLUCIÓN AUTOMÁTICA ERROR 500 ---
                // 1. Buscamos un ID de usuario válido en la BD para usar como 'creator'
                var validCreatorId = obtenerIdUsuarioValido(api)
                
                // 2. Si no hay usuarios, creamos uno de emergencia
                if (validCreatorId == null) {
                    validCreatorId = crearUsuarioAdminPorDefecto(api)
                }

                // 3. Crear la vacante usando ese ID real
                val newVacancy = Vacancy(
                    id = 0, 
                    title = title,
                    description = description,
                    departmentId = departmentId,
                    createdBy = validCreatorId, // ¡Ahora usamos un ID que SI existe!
                    status = status,
                    createdAt = null,
                    vacancySkills = null
                )

                api.createVacancy(newVacancy)
                _createSuccess.value = true
                
                cargarVacantes()

            } catch (e: Exception) {
                // Si falla, mostramos el mensaje técnico para debug
                val mensaje = if (e.message?.contains("500") == true) {
                    "Error del Servidor (500). Verifica que el ID de Departamento ($departmentId) exista en la tabla Departments."
                } else {
                    "Error: ${e.localizedMessage}"
                }
                _error.value = mensaje
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // Función auxiliar para buscar cualquier usuario existente
    private suspend fun obtenerIdUsuarioValido(api: ApiService): String? {
        return try {
            val profiles = api.getProfiles()
            if (profiles.isNotEmpty()) profiles.first().id else null
        } catch (e: Exception) {
            null
        }
    }

    // Función auxiliar para crear un usuario si la BD está vacía
    private suspend fun crearUsuarioAdminPorDefecto(api: ApiService): String {
        val newId = UUID.randomUUID().toString()
        val adminProfile = Profile(
            id = newId,
            fullName = "Admin Autogenerado",
            position = "System Admin",
            departmentId = null,
            role = "Admin",
            isAvailableForChange = false,
            createdAt = null,
            updatedAt = null,
            profileSkills = null
        )
        api.createProfile(adminProfile)
        return newId
    }
    
    fun resetCreateSuccess() {
        _createSuccess.value = false
    }
}