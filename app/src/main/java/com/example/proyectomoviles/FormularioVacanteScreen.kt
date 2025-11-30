package com.example.proyectomoviles

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectomoviles.model.Skill
import com.example.proyectomoviles.viewmodel.FormularioVacanteViewModel
import com.example.proyectomoviles.viewmodel.SkillConGrado
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioVacanteScreen(
    onNavigateBack: () -> Unit,
    viewModel: FormularioVacanteViewModel = viewModel()
) {
    val title by viewModel.title.collectAsState()
    val description by viewModel.description.collectAsState()
    val departments by viewModel.departments.collectAsState()
    val selectedDepartment by viewModel.selectedDepartment.collectAsState()
    val allSkills by viewModel.allSkills.collectAsState()
    val selectedSkills by viewModel.selectedSkills.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val createSuccess by viewModel.createSuccess.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(createSuccess) {
        if (createSuccess) {
            onNavigateBack()
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text("Crear Nueva Vacante", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            OutlinedTextField(
                value = title,
                onValueChange = viewModel::onTitleChange,
                label = { Text("Título del Puesto") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            OutlinedTextField(
                value = description,
                onValueChange = viewModel::onDescriptionChange,
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            DepartmentDropdown(
                departments = departments,
                selectedDepartment = selectedDepartment,
                onDepartmentSelected = viewModel::onDepartmentSelected
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            SkillSelector(
                allSkills = allSkills,
                selectedSkills = selectedSkills,
                onSkillAdded = viewModel::addSkill
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        items(selectedSkills) { skillConGrado ->
            SelectedSkillItem(
                skillConGrado = skillConGrado,
                onGradeChange = { newGrade ->
                    viewModel.updateSkillGrade(skillConGrado.skill.id, newGrade)
                },
                onRemove = {
                    viewModel.removeSkill(skillConGrado.skill.id)
                }
            )
        }
        
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = viewModel::crearVacante,
                enabled = !isLoading && title.isNotBlank() && selectedDepartment != null,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text("Guardar Vacante")
                }
            }
        }
        
        error?.let {
            item {
                Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
            }
        }
    }
}

@Composable
private fun DepartmentDropdown(
    departments: List<com.example.proyectomoviles.model.Department>,
    selectedDepartment: com.example.proyectomoviles.model.Department?,
    onDepartmentSelected: (com.example.proyectomoviles.model.Department) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collectLatest {
            if (it is PressInteraction.Release) {
                expanded = !expanded
            }
        }
    }

    Box {
        OutlinedTextField(
            value = selectedDepartment?.name ?: "Seleccionar Departamento",
            onValueChange = {},
            readOnly = true,
            label = { Text("Departamento") },
            trailingIcon = { Icon(Icons.Default.ArrowDropDown, "Dropdown") },
            modifier = Modifier.fillMaxWidth(),
            interactionSource = interactionSource
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            departments.forEach { department ->
                DropdownMenuItem(
                    text = { Text(department.name) },
                    onClick = {
                        onDepartmentSelected(department)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun SkillSelector(
    allSkills: List<Skill>,
    selectedSkills: List<SkillConGrado>,
    onSkillAdded: (Skill) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val availableSkills = allSkills.filter { skill -> selectedSkills.none { it.skill.id == skill.id } }
    val interactionSource = remember { MutableInteractionSource() }
    
    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collectLatest {
            if (it is PressInteraction.Release) {
                expanded = !expanded
            }
        }
    }

    Box {
        OutlinedTextField(
            value = if (availableSkills.isNotEmpty()) "Añadir Skill Requerido" else "No hay más skills",
            onValueChange = {},
            readOnly = true,
            enabled = availableSkills.isNotEmpty(),
            label = { Text("Skills") },
            trailingIcon = { Icon(Icons.Default.ArrowDropDown, "Dropdown") },
            modifier = Modifier.fillMaxWidth(),
            interactionSource = interactionSource
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            availableSkills.forEach { skill ->
                DropdownMenuItem(
                    text = { Text(skill.name) },
                    onClick = {
                        onSkillAdded(skill)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun SelectedSkillItem(
    skillConGrado: SkillConGrado,
    onGradeChange: (Int) -> Unit,
    onRemove: () -> Unit
) {
    var gradeMenuExpanded by remember { mutableStateOf(false) }
    val gradeLabels = listOf("Básico", "Intermedio", "Avanzado")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(skillConGrado.skill.name, modifier = Modifier.weight(1f))

        Box {
            OutlinedButton(onClick = { gradeMenuExpanded = true }) {
                Text(gradeLabels[skillConGrado.grade - 1])
                Icon(Icons.Default.ArrowDropDown, "Dropdown")
            }
            DropdownMenu(
                expanded = gradeMenuExpanded,
                onDismissRequest = { gradeMenuExpanded = false }
            ) {
                gradeLabels.forEachIndexed { index, label ->
                    DropdownMenuItem(
                        text = { Text(label) },
                        onClick = {
                            onGradeChange(index + 1)
                            gradeMenuExpanded = false
                        }
                    )
                }
            }
        }

        IconButton(onClick = onRemove) {
            Icon(Icons.Default.Clear, "Remover Skill")
        }
    }
}