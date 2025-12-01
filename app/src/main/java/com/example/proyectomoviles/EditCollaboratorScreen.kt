package com.example.proyectomoviles

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectomoviles.model.Skill
import com.example.proyectomoviles.viewmodel.EditCollaboratorViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCollaboratorScreen(
    profileId: String,
    onNavigateBack: () -> Unit,
    viewModel: EditCollaboratorViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(profileId) {
        viewModel.loadInitialData(profileId)
    }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            snackbarHostState.showSnackbar("¡Perfil guardado correctamente!")
            onNavigateBack()
        }
    }

    uiState.error?.let {
        val context = androidx.compose.ui.platform.LocalContext.current
        LaunchedEffect(it) {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Editar Colaborador") },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") } }
            )
        }
    ) {
        if (uiState.isLoading && uiState.userSkills.isEmpty()) { // Show loading only on initial load
             Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Información Básica", style = MaterialTheme.typography.titleLarge)
                OutlinedTextField(value = uiState.fullName, onValueChange = viewModel::onFullNameChange, label = { Text("Nombre completo") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = uiState.position, onValueChange = viewModel::onPositionChange, label = { Text("Posición") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = uiState.role, onValueChange = viewModel::onRoleChange, label = { Text("Rol") }, modifier = Modifier.fillMaxWidth())
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Disponible para cambio")
                    Spacer(modifier = Modifier.weight(1f))
                    Switch(checked = uiState.isAvailable, onCheckedChange = viewModel::onAvailabilityChange)
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                SkillsManagementSection(viewModel = viewModel, profileId = profileId)

                Spacer(modifier = Modifier.weight(1f))
                
                Button(
                    onClick = { viewModel.updateProfileDetails(profileId) },
                    enabled = !uiState.isLoading, 
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                     if (uiState.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    } else {
                        Text("Guardar Cambios")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SkillsManagementSection(
    viewModel: EditCollaboratorViewModel,
    profileId: String
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddSkillDialog by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Habilidades", style = MaterialTheme.typography.titleLarge)
            Button(onClick = { showAddSkillDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Añadir")
            }
        }

        if (uiState.userSkills.isNotEmpty()) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                uiState.userSkills.forEach { profileSkill ->
                    val skillName = uiState.allSkills.find { it.id == profileSkill.skillId }?.name ?: "Skill desconocido"
                    InputChip(
                        selected = false,
                        onClick = {},
                        label = { Text("$skillName (Nivel ${profileSkill.grado})") },
                        trailingIcon = {
                            IconButton(onClick = { viewModel.removeSkill(profileId, profileSkill.skillId) }) {
                                Icon(Icons.Default.Close, contentDescription = "Eliminar Skill")
                            }
                        }
                    )
                }
            }
        } else {
            Text("No hay habilidades asignadas.", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        }
    }

    if (showAddSkillDialog) {
        AddSkillDialog(
            allSkills = uiState.allSkills.filterNot { a -> uiState.userSkills.any { u -> u.skillId == a.id } },
            onDismiss = { showAddSkillDialog = false },
            onAddSkill = { skillId, grade ->
                viewModel.addSkill(profileId, skillId, grade)
                showAddSkillDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSkillDialog(
    allSkills: List<Skill>,
    onDismiss: () -> Unit,
    onAddSkill: (Int, Int) -> Unit
) {
    var selectedSkill by remember { mutableStateOf<Skill?>(null) }
    var grade by remember { mutableStateOf(1f) }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    val gradeLabels = listOf("Básico", "Intermedio", "Avanzado")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Añadir Nueva Habilidad") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                ExposedDropdownMenuBox(
                    expanded = isDropdownExpanded,
                    onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedSkill?.name ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Selecciona una habilidad") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = { isDropdownExpanded = false }
                    ) {
                        allSkills.forEach { skill ->
                            DropdownMenuItem(
                                text = { Text(skill.name) },
                                onClick = {
                                    selectedSkill = skill
                                    isDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Column {
                    Text("Nivel: ${gradeLabels.getOrNull(grade.roundToInt() - 1) ?: ""}")
                    Slider(
                        value = grade,
                        onValueChange = { grade = it },
                        valueRange = 1f..3f,
                        steps = 1
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    selectedSkill?.let { onAddSkill(it.id, grade.roundToInt()) }
                },
                enabled = selectedSkill != null
            ) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
