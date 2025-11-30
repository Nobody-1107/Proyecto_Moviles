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

// Modelo para la UI de los detalles de la vacante
data class VacancyDetailUi(
    val id: Int,
    val title: String,
    val department: String,
    val skills: List<Pair<String, Int>> // Pares de (Nombre de Skill, Grado)
)

// Modelo para la UI de los candidatos recomendados
data class RecommendedCandidateUi(
    val profile: Profile,
    val compatibility: Int // Porcentaje de 0 a 100
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

    fun loadVacancyDetails(vacancyId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val api = RetrofitClient.instance.create(ApiService::class.java)

                // 1. Obtener todos los datos maestros
                val allSkillsMap = try { api.getSkills().associateBy { it.id } } catch (e: Exception) { emptyMap() }
                val allDepartmentsMap = try { api.getDepartments().associateBy { it.id } } catch (e: Exception) { emptyMap() }
                val allProfiles = try { api.getProfiles() } catch (e: Exception) { emptyList() }

                // 2. Obtener los detalles de la vacante específica
                val vacancyRaw = api.getVacancyById(vacancyId)
                val vacancySkills = api.getVacancySkills(vacancyId) // Lista de VacancySkill

                // 3. Mapear los datos de la vacante al modelo de UI
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

                // 4. Lógica de recomendación con cálculo de compatibilidad dinámico
                _candidates.value = allProfiles.map { profile ->
                    val profileSkillsMap = profile.profileSkills?.associateBy({ it.skillId }, { it.grado }) ?: emptyMap()

                    if (vacancySkills.isEmpty()) {
                        // Si la vacante no requiere skills, la compatibilidad es del 100%
                        return@map RecommendedCandidateUi(profile = profile, compatibility = 100)
                    }

                    val skillGaps = vacancySkills.map { requiredSkill ->
                        val profileGrade = profileSkillsMap[requiredSkill.skillId] ?: 0
                        val requiredGrade = requiredSkill.grado

                        // La diferencia nunca puede ser negativa (el perfil no puede tener "menos" que 0)
                        val gradeDifference = maxOf(0, requiredGrade - profileGrade)

                        // Convertir la diferencia de grado (0-3) a un porcentaje de brecha (0-100)
                        // Cada punto de diferencia equivale a ~33.3% de brecha
                        val gapPercentage = (gradeDifference / 3.0) * 100.0
                        gapPercentage
                    }

                    val averageGap = skillGaps.average()
                    val compatibility = (100.0 - averageGap).roundToInt()

                    RecommendedCandidateUi(
                        profile = profile,
                        compatibility = maxOf(0, compatibility) // Asegurarse de que no sea negativo
                    )

                }.sortedByDescending { it.compatibility } // Ordenar por mejor compatibilidad

            } catch (e: Exception) {
                _error.value = "Error al cargar los detalles: ${e.localizedMessage}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}