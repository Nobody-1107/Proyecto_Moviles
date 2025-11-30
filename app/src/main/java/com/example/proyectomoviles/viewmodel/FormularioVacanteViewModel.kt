package com.example.proyectomoviles.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectomoviles.model.Department
import com.example.proyectomoviles.model.Skill
import com.example.proyectomoviles.model.Vacancy
import com.example.proyectomoviles.model.VacancySkill
import com.example.proyectomoviles.network.ApiService
import com.example.proyectomoviles.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Modelo de UI para manejar un skill con su grado en el formulario
data class SkillConGrado(
    val skill: Skill,
    var grade: Int = 1
)

class FormularioVacanteViewModel : ViewModel() {

    // --- Estados del Formulario ---
    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()

    private val _selectedDepartment = MutableStateFlow<Department?>(null)
    val selectedDepartment: StateFlow<Department?> = _selectedDepartment.asStateFlow()

    // --- Listas de datos ---
    private val _departments = MutableStateFlow<List<Department>>(emptyList())
    val departments: StateFlow<List<Department>> = _departments.asStateFlow()

    private val _allSkills = MutableStateFlow<List<Skill>>(emptyList())
    val allSkills: StateFlow<List<Skill>> = _allSkills.asStateFlow()

    private val _selectedSkills = MutableStateFlow<List<SkillConGrado>>(emptyList())
    val selectedSkills: StateFlow<List<SkillConGrado>> = _selectedSkills.asStateFlow()

    // --- Estados de la UI ---
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _createSuccess = MutableStateFlow(false)
    val createSuccess: StateFlow<Boolean> = _createSuccess.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val api = RetrofitClient.instance.create(ApiService::class.java)
                _departments.value = api.getDepartments()
                _allSkills.value = api.getSkills()
            } catch (e: Exception) {
                _error.value = "Error al cargar datos iniciales: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // --- Acciones del usuario ---
    fun onTitleChange(newTitle: String) { _title.value = newTitle }
    fun onDescriptionChange(newDescription: String) { _description.value = newDescription }
    fun onDepartmentSelected(department: Department) { _selectedDepartment.value = department }

    fun addSkill(skill: Skill) {
        if (_selectedSkills.value.none { it.skill.id == skill.id }) {
            _selectedSkills.value += SkillConGrado(skill)
        }
    }

    fun removeSkill(skillId: Int) {
        _selectedSkills.value = _selectedSkills.value.filterNot { it.skill.id == skillId }
    }

    fun updateSkillGrade(skillId: Int, newGrade: Int) {
        _selectedSkills.value = _selectedSkills.value.map {
            if (it.skill.id == skillId) it.copy(grade = newGrade) else it
        }
    }

    fun crearVacante() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _createSuccess.value = false

            try {
                val api = RetrofitClient.instance.create(ApiService::class.java)

                // 1. Preparamos la lista de Skills para enviarla JUNTO con la vacante.
                // Convertimos tu modelo de UI (SkillConGrado) al modelo de Datos (VacancySkill)
                val skillsParaEnviar = _selectedSkills.value.map { skillUi ->
                    VacancySkill(
                        vacancyId = 0, // Se manda 0, el backend le asignará el ID correcto automáticamente
                        skillId = skillUi.skill.id,
                        grado = skillUi.grade
                    )
                }

                // 2. Creamos el objeto Vacancy INCLUYENDO la lista de skills
                val vacancyToCreate = Vacancy(
                    id = 0,
                    title = _title.value,
                    description = _description.value,
                    departmentId = _selectedDepartment.value?.id,
                    status = "open", // Ojo: Asegúrate de mandar el status que espera tu BD ("open" o "Abierta")
                    vacancySkills = skillsParaEnviar // <--- AQUÍ ESTÁ EL CAMBIO CLAVE
                )

                // 3. Hacemos UNA SOLA llamada al API
                api.createVacancy(vacancyToCreate)

                // Si no lanza error, es éxito
                _createSuccess.value = true

            } catch (e: Exception) {
                _error.value = "Error al crear la vacante: ${e.localizedMessage}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}