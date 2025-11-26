package com.example.proyectomoviles.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectomoviles.network.ApiService
import com.example.proyectomoviles.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Modelo UI simplificado para la vista
data class TeamMemberUi(
    val id: String,
    val name: String,
    val title: String,
    val skills: List<String> 
)

class MiEquipoViewModel : ViewModel() {
    private val _teamMembers = MutableStateFlow<List<TeamMemberUi>>(emptyList())
    val teamMembers: StateFlow<List<TeamMemberUi>> = _teamMembers

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun cargarEquipo() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val api = RetrofitClient.instance.create(ApiService::class.java)
                
                // 1. Obtener todos los perfiles
                val profiles = api.getProfiles()
                
                // 2. Obtener catálogo de skills para mapear IDs a Nombres
                // (Idealmente esto se cachearía o se manejaría en un repositorio)
                val skills = try {
                    api.getSkills()
                } catch (e: Exception) {
                    emptyList()
                }
                val skillsMap = skills.associateBy { it.id }

                // 3. Transformar a modelo de UI
                val uiModels = profiles.map { profile ->
                    val profileSkillNames = profile.profileSkills?.mapNotNull { ps ->
                        skillsMap[ps.skillId]?.name
                    } ?: emptyList()

                    TeamMemberUi(
                        id = profile.id,
                        name = profile.fullName,
                        title = profile.position,
                        skills = profileSkillNames
                    )
                }
                _teamMembers.value = uiModels

            } catch (e: Exception) {
                _error.value = "Error al conectar con servidor: ${e.localizedMessage}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}