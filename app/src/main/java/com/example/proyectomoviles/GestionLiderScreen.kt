package com.example.proyectomoviles

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectomoviles.viewmodel.GestionLiderViewModel

@Composable
fun GestionLiderScreen(
    onNavigateToUpdateSkills: () -> Unit,
    viewModel: GestionLiderViewModel = viewModel()
) {
    val candidates by viewModel.availableCandidates.collectAsState()
    val vacancies by viewModel.vacancies.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val assignmentSuccess by viewModel.assignmentSuccess.collectAsState()
    
    // Estado para el modal de asignación
    var showAssignModal by remember { mutableStateOf(false) }
    var selectedCandidateId by remember { mutableStateOf<String?>(null) }
    var selectedCandidateName by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.cargarDatos()
    }
    
    if (assignmentSuccess) {
        // Podríamos mostrar un Toast aquí, pero lo manejaremos con un reset simple
        LaunchedEffect(Unit) {
            viewModel.resetAssignmentSuccess()
            showAssignModal = false
        }
    }

    if (isLoading && candidates.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    
    // Modal para asignar candidato a vacante
    if (showAssignModal && selectedCandidateId != null) {
        AlertDialog(
            onDismissRequest = { showAssignModal = false },
            title = { Text("Asignar $selectedCandidateName") },
            text = {
                Column {
                    Text("Selecciona una vacante para este colaborador:")
                    Spacer(modifier = Modifier.height(16.dp))
                    if (vacancies.isEmpty()) {
                        Text("No hay vacantes disponibles.", color = Color.Red)
                    } else {
                        vacancies.forEach { vacante ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable { 
                                        viewModel.asignarCandidato(selectedCandidateId!!, vacante.id) 
                                    },
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Text(
                                    text = vacante.title,
                                    modifier = Modifier.padding(12.dp),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAssignModal = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Talento Disponible para Vacantes", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text(
                        "Colaboradores que han activado su disponibilidad.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
        
        if (error != null) {
            item {
                Text("Error: $error", color = Color.Red)
            }
        }
        
        if (candidates.isEmpty() && !isLoading) {
             item {
                Text("No hay colaboradores disponibles en este momento.", modifier = Modifier.padding(8.dp))
            }
        }

        items(candidates) { candidate ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(candidate.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text(candidate.title, color = Color.Gray)
                        }
                        IconButton(
                            onClick = { 
                                selectedCandidateId = candidate.id
                                selectedCandidateName = candidate.name
                                showAssignModal = true 
                            },
                            colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Icon(Icons.Default.PersonAdd, contentDescription = "Asignar a vacante")
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text("Habilidades:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Row(modifier = Modifier.padding(top = 4.dp)) {
                        candidate.skills.take(3).forEach { skill ->
                             Surface(
                                shape = MaterialTheme.shapes.small,
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.padding(end = 4.dp)
                            ) {
                                Text(
                                    text = skill,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
