package com.example.proyectomoviles

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioVacanteScreen(onNavigateBack: () -> Unit) {
    var jobTitle by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("") }
    var urgency by remember { mutableStateOf("Media") }
    var startDate by remember { mutableStateOf("Selecciona una fecha") }
    var skillName by remember { mutableStateOf("") }
    var skillLevel by remember { mutableStateOf("Intermedio") }
    val skillLevels = listOf("Básico", "Intermedio", "Avanzado", "Experto")
    var isDropdownExpanded by remember { mutableStateOf(false) }

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                }
                Text("Registrar Nueva Vacante", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Información General", fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = jobTitle,
                        onValueChange = { jobTitle = it },
                        label = { Text("Nombre del perfil o puesto") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = department,
                        onValueChange = { department = it },
                        label = { Text("Área o departamento") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Nivel de Urgencia", fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                        ToggleButton(text = "Alta", selected = urgency == "Alta") { urgency = "Alta" }
                        ToggleButton(text = "Media", selected = urgency == "Media") { urgency = "Media" }
                        ToggleButton(text = "Baja", selected = urgency == "Baja") { urgency = "Baja" }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Fecha de Inicio", fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = startDate,
                        onValueChange = { startDate = it },
                        label = { Text("Selecciona una fecha") },
                        leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { /* TODO: Show Date Picker */ }
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Skills Requeridos", fontWeight = FontWeight.Bold)
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = skillName,
                            onValueChange = { skillName = it },
                            label = { Text("Ej: React, Python, Liderazgo...") },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(modifier = Modifier.width(140.dp)) {
                            ExposedDropdownMenuBox(
                                expanded = isDropdownExpanded,
                                onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }
                            ) {
                                OutlinedTextField(
                                    value = skillLevel,
                                    onValueChange = {}, // Read-only
                                    readOnly = true,
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                                    modifier = Modifier.menuAnchor()
                                )
                                ExposedDropdownMenu(
                                    expanded = isDropdownExpanded,
                                    onDismissRequest = { isDropdownExpanded = false }
                                ) {
                                    skillLevels.forEach { level ->
                                        DropdownMenuItem(
                                            text = { Text(level) },
                                            onClick = {
                                                skillLevel = level
                                                isDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = { /* TODO: Add skill */ }) {
                            Icon(Icons.Default.Add, contentDescription = "Añadir Skill")
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        item {
            Column {
                Button(
                    onClick = { /* TODO: Save */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar Vacante")
                }
                OutlinedButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancelar")
                }
            }
        }
    }
}

@Composable
private fun ToggleButton(text: String, selected: Boolean, onClick: () -> Unit) {
    val colors = if (selected) ButtonDefaults.buttonColors() else ButtonDefaults.outlinedButtonColors()
    Button(onClick = onClick, colors = colors) {
        Text(text)
    }
}
