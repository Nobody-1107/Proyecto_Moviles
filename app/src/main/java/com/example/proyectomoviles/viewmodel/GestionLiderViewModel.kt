package com.example.proyectomoviles.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectomoviles.network.ApiService
import com.example.proyectomoviles.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Reutilizamos el modelo de UI de TeamMember para consistencia, 
// pero añadimos lo que necesitemos
data class CandidateUi(
    val id: String,
    val name: String,
    val title: String,
    val skills: List<String>
)

data class VacancySimpleUi(
    val id: Int,
    val title: String
)

class GestionLiderViewModel : ViewModel() {
    
    private val _availableCandidates = MutableStateFlow<List<CandidateUi>>(emptyList())
    val availableCandidates: StateFlow<List<CandidateUi>> = _availableCandidates
    
    private val _vacancies = MutableStateFlow<List<VacancySimpleUi>>(emptyList())
    val vacancies: StateFlow<List<VacancySimpleUi>> = _vacancies

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    
    private val _assignmentSuccess = MutableStateFlow(false)
    val assignmentSuccess: StateFlow<Boolean> = _assignmentSuccess

    fun cargarDatos() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val api = RetrofitClient.instance.create(ApiService::class.java)
                
                // 1. Obtener TODOS los perfiles
                val profiles = api.getProfiles()
                
                // 2. Filtrar solo los que están DISPONIBLES (isAvailableForChange == true)
                val availableProfiles = profiles.filter { it.isAvailableForChange }
                
                // 3. Obtener Skills para mostrar nombres
                val skills = try { api.getSkills().associateBy { it.id } } catch (e: Exception) { emptyMap() }
                
                // 4. Mapear candidatos
                val candidatesUi = availableProfiles.map { profile ->
                    val profileSkillNames = profile.profileSkills?.mapNotNull { ps ->
                        skills[ps.skillId]?.name
                    } ?: emptyList()
                    
                    CandidateUi(
                        id = profile.id,
                        name = profile.fullName,
                        title = profile.position,
                        skills = profileSkillNames
                    )
                }
                _availableCandidates.value = candidatesUi
                
                // 5. Cargar vacantes también para poder asignarlas
                val vacanciesRaw = api.getVacancies()
                // Filtramos solo vacantes abiertas
                val openVacancies = vacanciesRaw.filter { it.status == "Abierta" || it.status == null }
                
                _vacancies.value = openVacancies.map { 
                    VacancySimpleUi(it.id, it.title) 
                }

            } catch (e: Exception) {
                _error.value = "Error al cargar candidatos: ${e.localizedMessage}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun asignarCandidato(candidateId: String, vacancyId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _assignmentSuccess.value = false
            try {
                val api = RetrofitClient.instance.create(ApiService::class.java)
                
                // Llamamos al endpoint de asignación
                api.addCandidateToVacancy(vacancyId, candidateId)
                
                _assignmentSuccess.value = true
                
                // Opcional: Podríamos recargar los datos o quitar al candidato de la lista
                // Por ahora recargamos todo para asegurar consistencia
                cargarDatos()
                
            } catch (e: Exception) {
                _error.value = "Error al asignar: ${e.localizedMessage}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun resetAssignmentSuccess() {
        _assignmentSuccess.value = false
    }
}