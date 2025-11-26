package com.example.proyectomoviles

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectomoviles.viewmodel.DemandaLiderViewModel

@Composable
fun DemandaLiderScreen(
    onNavigateToCreateVacante: () -> Unit,
    viewModel: DemandaLiderViewModel = viewModel()
) {
    val vacancies by viewModel.vacancies.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarVacantes()
    }

    if (isLoading && vacancies.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        item {
            Button(
                onClick = onNavigateToCreateVacante,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrar Nueva Vacante")
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Text("Demanda de Talento", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        if (error != null) {
            item {
                Text("Error al cargar demandas: $error", color = Color.Red)
            }
        }
        
        if (!isLoading && vacancies.isEmpty() && error == null) {
            item {
                Text("No hay vacantes registradas.", color = Color.Gray)
            }
        }

        items(vacancies) { vacante ->
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(vacante.title, fontWeight = FontWeight.Bold)
                    Text(vacante.department, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    if (vacante.skills.isNotEmpty()) {
                        Text("Skills Requeridos:", fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(4.dp))
                        // Row horizontal con scroll si son muchos skills, o un FlowRow si tuvieramos la dependencia
                        Row {
                            vacante.skills.take(3).forEach { skill -> // Limitamos visualmente
                                Chip(label = { Text(skill) }, modifier = Modifier.padding(end = 4.dp))
                            }
                            if (vacante.skills.size > 3) {
                                Chip(label = { Text("+${vacante.skills.size - 3}") }, modifier = Modifier.padding(end = 4.dp))
                            }
                        }
                    } else {
                        Text("Sin skills requeridos", style = MaterialTheme.typography.bodySmall)
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Estado: ${vacante.status}")
                    
                    // Opcional: cobertura visual simulada
                    // Text("Cobertura: ${(vacante.coverage * 100).toInt()}%")
                    // LinearProgressIndicator(progress = { vacante.coverage }, modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}

@Composable
private fun Chip(label: @Composable () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Box(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
            label()
        }
    }
}
