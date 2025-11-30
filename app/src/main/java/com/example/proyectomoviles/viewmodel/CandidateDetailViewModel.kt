package com.example.proyectomoviles.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectomoviles.model.Profile
import com.example.proyectomoviles.model.Skill
import com.example.proyectomoviles.model.VacancySkill
import com.example.proyectomoviles.network.ApiService
import com.example.proyectomoviles.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// UI State for the Candidate Detail Screen
data class CandidateDetailUiState(
    val candidateProfile: Profile? = null,
    val candidateDepartment: String = "",
    val skillsComparison: List<SkillComparisonItem> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

// Represents a single skill's comparison for the chart
data class SkillComparisonItem(
    val skillName: String,
    val requiredGrade: Int,
    val profileGrade: Int
)

class CandidateDetailViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CandidateDetailUiState())
    val uiState: StateFlow<CandidateDetailUiState> = _uiState

    fun loadCandidateDetails(vacancyId: Int, candidateId: String) {
        viewModelScope.launch {
            _uiState.value = CandidateDetailUiState(isLoading = true) // Reset and show loading

            try {
                val api = RetrofitClient.instance.create(ApiService::class.java)

                // 1. Fetch all necessary data in parallel
                val allSkillsMap = try { api.getSkills().associateBy { it.id } } catch (e: Exception) { emptyMap() }
                val allDepartmentsMap = try { api.getDepartments().associateBy { it.id } } catch (e: Exception) { emptyMap() }
                
                // 2. Fetch specific data for the vacancy and candidate
                val vacancySkills = api.getVacancySkills(vacancyId)
                val candidateProfile = api.getProfileById(candidateId) // Assuming this endpoint exists

                val candidateSkillsMap = candidateProfile.profileSkills?.associateBy({ it.skillId }, { it.grado }) ?: emptyMap()

                // 3. Perform the comparison logic
                val skillsComparison = vacancySkills.mapNotNull { requiredSkill ->
                    allSkillsMap[requiredSkill.skillId]?.name?.let { skillName ->
                        SkillComparisonItem(
                            skillName = skillName,
                            requiredGrade = requiredSkill.grado,
                            profileGrade = candidateSkillsMap[requiredSkill.skillId] ?: 0
                        )
                    }
                }
                
                val departmentName = allDepartmentsMap[candidateProfile.departmentId]?.name ?: "Desconocido"

                // 4. Update the UI State
                _uiState.value = CandidateDetailUiState(
                    candidateProfile = candidateProfile,
                    candidateDepartment = departmentName,
                    skillsComparison = skillsComparison,
                    isLoading = false
                )

            } catch (e: Exception) {
                _uiState.value = CandidateDetailUiState(
                    error = "Error al cargar los detalles: ${e.localizedMessage}",
                    isLoading = false
                )
                e.printStackTrace()
            }
        }
    }
}