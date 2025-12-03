package com.example.proyectomoviles.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectomoviles.model.Profile
import com.example.proyectomoviles.model.ProfileSkill
import com.example.proyectomoviles.model.Skill
import com.example.proyectomoviles.network.ApiService
import com.example.proyectomoviles.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EditCollaboratorUiState(
    // Estado del formulario
    val fullName: String = "",
    val position: String = "",
    val role: String = "",
    val isAvailable: Boolean = false,

    // Estado de la lista de skills
    val userSkills: List<ProfileSkill> = emptyList(),
    val allSkills: List<Skill> = emptyList(),

    // Estado de la pantalla
    val isLoading: Boolean = true,
    val error: String? = null,
    val isSaved: Boolean = false
)

class EditCollaboratorViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(EditCollaboratorUiState())
    val uiState = _uiState.asStateFlow()

    private var originalProfile: Profile? = null
    private val apiService = RetrofitClient.instance.create(ApiService::class.java)

    fun loadInitialData(profileId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // 1. OBTENER PERFIL
                // Gracias a tu corrección en backend, esto ya trae los "profile_skills" llenos.
                val profile = apiService.getProfileById(profileId)
                originalProfile = profile

                // 2. OBTENER LISTA MAESTRA DE SKILLS (Para llenar el Dropdown de agregar)
                val allSkills = apiService.getSkills()

                _uiState.update {
                    it.copy(
                        fullName = profile.fullName,
                        position = profile.position,
                        role = profile.role,
                        isAvailable = profile.isAvailableForChange,
                        // Usamos la lista que viene dentro del perfil. Si es null, lista vacía.
                        userSkills = profile.profileSkills ?: emptyList(),
                        allSkills = allSkills,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error al cargar datos: ${e.message}", isLoading = false) }
            }
        }
    }

    // --- Event Handlers for UI actions ---
    fun onFullNameChange(newName: String) {
        _uiState.update { it.copy(fullName = newName) }
    }

    fun onPositionChange(newPosition: String) {
        _uiState.update { it.copy(position = newPosition) }
    }

    fun onRoleChange(newRole: String) {
        _uiState.update { it.copy(role = newRole) }
    }

    fun onAvailabilityChange(newAvailability: Boolean) {
        _uiState.update { it.copy(isAvailable = newAvailability) }
    }

    fun updateProfileDetails(profileId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val currentSkillsInScreen = _uiState.value.userSkills

            val profileToUpdate = originalProfile?.copy(
                fullName = _uiState.value.fullName,
                position = _uiState.value.position,
                role = _uiState.value.role,
                isAvailableForChange = _uiState.value.isAvailable,
                profileSkills = currentSkillsInScreen
            )

            if (profileToUpdate == null) {
                _uiState.update { it.copy(error = "Error interno: Perfil original perdido", isLoading = false) }
                return@launch
            }

            try {
                // --- CAMBIO AQUÍ: Recibimos Response ---
                val response = apiService.updateProfile(profileId, profileToUpdate)

                if (response.isSuccessful) {
                    _uiState.update { it.copy(isSaved = true, isLoading = false) }
                } else {
                    // Ahora podemos leer el error real del backend
                    val errorMsg = response.errorBody()?.string() ?: "Error desconocido"
                    _uiState.update { it.copy(error = "Error del servidor (${response.code()}): $errorMsg", isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error de conexión: ${e.message}", isLoading = false) }
            }
        }
    }

    fun addSkill(profileId: String, skillId: Int, grade: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(error = null) }
            try {
                // Hacemos la llamada inmediata para tener persistencia rápida
                val newProfileSkill = ProfileSkill(profileId = profileId, skillId = skillId, grado = grade)
                val addedSkill = apiService.createProfileSkill(newProfileSkill)

                // Actualizamos la UI inmediatamente
                _uiState.update {
                    it.copy(userSkills = it.userSkills + addedSkill)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error al añadir skill: ${e.message}") }
            }
        }
    }

    fun removeSkill(profileId: String, skillId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(error = null) }
            try {
                val body = mapOf("profile_id" to profileId, "skill_id" to skillId.toString())
                val response = apiService.deleteProfileSkill(body)

                if (response.isSuccessful) {
                    _uiState.update {
                        it.copy(userSkills = it.userSkills.filterNot { skill -> skill.skillId == skillId })
                    }
                } else {
                    _uiState.update { it.copy(error = "Error: ${response.code()} ${response.message()}") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error al eliminar skill: ${e.message}") }
            }
        }
    }
}