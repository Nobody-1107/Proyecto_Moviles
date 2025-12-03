package com.example.proyectomoviles.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectomoviles.model.Profile
import com.example.proyectomoviles.network.ApiService
import com.example.proyectomoviles.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

data class VacancyDetailUi(
    val id: Int,
    val title: String,
    val department: String,
    val skills: List<Pair<String, Int>>
)

data class RecommendedCandidateUi(
    val profile: Profile,
    val compatibility: Int
)

class VacancyDetailViewModel : ViewModel() {

    private val _vacancy = MutableStateFlow<VacancyDetailUi?>(null)
    val vacancy: StateFlow<VacancyDetailUi?> = _vacancy

    private val _candidates = MutableStateFlow<List<RecommendedCandidateUi>>(emptyList())
    val candidates: StateFlow<List<RecommendedCandidateUi>> = _candidates

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _vacancyDeleted = MutableStateFlow(false)
    val vacancyDeleted: StateFlow<Boolean> = _vacancyDeleted

    fun loadVacancyDetails(vacancyId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val api = RetrofitClient.instance.create(ApiService::class.java)

                val allSkillsMap = try { api.getSkills().associateBy { it.id } } catch (e: Exception) { emptyMap() }
                val allDepartmentsMap = try { api.getDepartments().associateBy { it.id } } catch (e: Exception) { emptyMap() }
                val allProfiles = try { api.getProfiles() } catch (e: Exception) { emptyList() }

                val vacancyRaw = api.getVacancyById(vacancyId)
                val vacancySkills = api.getVacancySkills(vacancyId)

                val skillsWithGrades = vacancySkills.mapNotNull { vs ->
                    allSkillsMap[vs.skillId]?.name?.let { skillName ->
                        Pair(skillName, vs.grado)
                    }
                }
                val departmentName = allDepartmentsMap[vacancyRaw.departmentId]?.name ?: "Desconocido"

                _vacancy.value = VacancyDetailUi(
                    id = vacancyRaw.id,
                    title = vacancyRaw.title,
                    department = departmentName,
                    skills = skillsWithGrades
                )

                // ... dentro del try, después de _vacancy.value = ...

                _candidates.value = allProfiles.map { profile ->
                    val profileSkillsMap = profile.profileSkills?.associateBy({ it.skillId }, { it.grado }) ?: emptyMap()

                    if (vacancySkills.isEmpty()) {
                        return@map RecommendedCandidateUi(profile = profile, compatibility = 100)
                    }

                    // --- LÓGICA CORREGIDA: CRÉDITO PARCIAL ---

                    var totalScore = 0.0

                    vacancySkills.forEach { requiredSkill ->
                        val candidateGrade = profileSkillsMap[requiredSkill.skillId] ?: 0

                        if (requiredSkill.grado > 0) {
                            // Calculamos cuánto cumple el candidato (Ej: Tiene 1, Pide 3 = 0.33 pts)
                            val skillFulfillment = candidateGrade.toDouble() / requiredSkill.grado.toDouble()

                            // Si tiene más de lo necesario (Ej: Tiene 5, Pide 3), topeamos a 1.0 (100% de esa skill)
                            totalScore += if (skillFulfillment > 1.0) 1.0 else skillFulfillment
                        } else {
                            // Si la skill requerida tiene grado 0, cuenta como cumplida
                            totalScore += 1.0
                        }
                    }

                    // Promedio final: Suma de puntajes parciales / Cantidad de skills
                    val compatibility = (totalScore / vacancySkills.size * 100).roundToInt()

                    RecommendedCandidateUi(
                        profile = profile,
                        compatibility = compatibility
                    )
                }.sortedByDescending { it.compatibility }

            } catch (e: Exception) {
                _error.value = "Error al cargar los detalles: ${e.localizedMessage}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteVacancy(vacancyId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val api = RetrofitClient.instance.create(ApiService::class.java)
                val response = api.deleteVacancy(vacancyId)
                if (response.isSuccessful) {
                    _vacancyDeleted.value = true
                } else {
                    _error.value = "Error al eliminar la vacante: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Error al eliminar la vacante: ${e.localizedMessage}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}