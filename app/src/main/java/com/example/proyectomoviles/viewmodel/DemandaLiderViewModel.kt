package com.example.proyectomoviles.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectomoviles.model.Vacancy
import com.example.proyectomoviles.model.VacancySkill
import com.example.proyectomoviles.network.ApiService
import com.example.proyectomoviles.network.RetrofitClient
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Asegúrate de tener este modelo en tu proyecto (SkillConGrado)
// Si no, defínelo temporalmente aquí o impórtalo.
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
                // Usamos try-catch en llamadas secundarias para que no rompan todo si fallan
                val departments = try { api.getDepartments().associateBy { it.id } } catch (e: Exception) { emptyMap() }
                val allSkillsMap = try { api.getSkills().associateBy { it.id } } catch (e: Exception) { emptyMap() }

                val uiModels = vacanciesRaw.map { vac ->
                    async {
                        val deptName = departments[vac.departmentId]?.name ?: "Desconocido"

                        val vacancySkills = try {
                            api.getVacancySkills(vac.id)
                        } catch (e: Exception) {
                            emptyList()
                        }

                        val skillNames = vacancySkills.mapNotNull { vs -> allSkillsMap[vs.skillId]?.name }

                        VacancyUi(
                            id = vac.id,
                            title = vac.title,
                            department = deptName,
                            skills = skillNames,
                            status = vac.status ?: "Abierta",
                            coverage = 0.5f
                        )
                    }
                }.awaitAll()

                _vacancies.value = uiModels

            } catch (e: Exception) {
                _error.value = "Error al cargar: ${e.localizedMessage}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // --- FUNCIÓN CORREGIDA ---
    fun crearVacante(
        title: String,
        departmentId: Int?,
        description: String,
        skillsSeleccionados: List<SkillConGrado>, // <--- Recibimos la lista de la UI
        status: String = "Abierta"
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _createSuccess.value = false
            _error.value = null
            try {
                val api = RetrofitClient.instance.create(ApiService::class.java)

                // 1. Validar Departamento
                if (departmentId == null || departmentId == 0) {
                    _error.value = "Selecciona un departamento válido."
                    return@launch
                }

                // 2. Mapear los Skills de la UI (SkillConGrado) al modelo del Backend (VacancySkill)
                // Nota: vacancyId va en 0, el backend lo asignará.
                val skillsParaBackend = skillsSeleccionados.map { uiSkill ->
                    VacancySkill(
                        vacancyId = 0,
                        skillId = uiSkill.skill.id,
                        grado = uiSkill.grade
                    )
                }

                // 3. Crear el objeto Vacancy limpio (Sin createdBy, sin createdAt)
                val newVacancy = Vacancy(
                    id = 0,
                    title = title,
                    description = description,
                    departmentId = departmentId,
                    status = status,
                    vacancySkills = skillsParaBackend // ¡Enviamos la lista aquí!
                )

                // 4. Enviar
                api.createVacancy(newVacancy)

                _createSuccess.value = true
                cargarVacantes() // Recargar lista

            } catch (e: Exception) {
                _error.value = "Error al guardar: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetCreateSuccess() {
        _createSuccess.value = false
    }
}