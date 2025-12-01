package com.example.proyectomoviles.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectomoviles.model.Profile
import com.example.proyectomoviles.network.ApiService
import com.example.proyectomoviles.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class CollaboratorDetailUiState(
    val profile: Profile? = null,
    val skills: List<Pair<String, Int>> = emptyList(), // Skill Name, Grade
    val isLoading: Boolean = false,
    val error: String? = null,
    val profileDeleted: Boolean = false
)

class CollaboratorDetailViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CollaboratorDetailUiState())
    val uiState: StateFlow<CollaboratorDetailUiState> = _uiState

    private val apiService = RetrofitClient.instance.create(ApiService::class.java)

    fun loadCollaboratorDetails(profileId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                // Fetch all data
                val profile = apiService.getProfileById(profileId)
                val profileSkills = apiService.getProfileSkills(profileId)
                val allSkills = apiService.getSkills().associateBy { it.id }

                val skillsWithGrades = profileSkills.mapNotNull { ps ->
                    allSkills[ps.skillId]?.name?.let { skillName ->
                        Pair(skillName, ps.grado)
                    }
                }

                _uiState.value = _uiState.value.copy(
                    profile = profile,
                    skills = skillsWithGrades,
                    isLoading = false
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al cargar el perfil: ${e.message}"
                )
            }
        }
    }

    fun deleteProfile(profileId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                apiService.deleteProfile(profileId)
                _uiState.value = _uiState.value.copy(isLoading = false, profileDeleted = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al eliminar el perfil: ${e.message}"
                )
            }
        }
    }
}