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
import com.example.proyectomoviles.network.ApiService
import com.example.proyectomoviles.network.RetrofitClient

// Modelo de UI para la vacante, desacoplado del modelo de red
data class DemandaVacancyUi(
    val id: Int,
    val title: String,
    val department: String,
    val status: String,
    val skills: List<String>
)

@Composable
fun DemandaLiderScreen(
    onNavigateToCreateVacante: () -> Unit,
    onNavigateToDetail: (Int) -> Unit
) {
    var vacancies by remember { mutableStateOf<List<DemandaVacancyUi>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    // Lógica de carga de datos directamente en el Composable
    LaunchedEffect(Unit) {
        isLoading = true
        error = null
        try {
            val apiService = RetrofitClient.instance.create(ApiService::class.java)
            
            val allSkills = apiService.getSkills().associateBy { it.id }
            val allDepartments = apiService.getDepartments().associateBy { it.id }
            val vacanciesRaw = apiService.getVacancies()

            // Procesar vacantes para la UI
            vacancies = vacanciesRaw.map { vacancy ->
                val departmentName = allDepartments[vacancy.departmentId]?.name ?: "Desconocido"
                val skillNames = vacancy.vacancySkills?.mapNotNull { allSkills[it.skillId]?.name } ?: emptyList()
                
                DemandaVacancyUi(
                    id = vacancy.id,
                    title = vacancy.title,
                    department = departmentName,
                    status = vacancy.status ?: "N/A",
                    skills = skillNames
                )
            }

        } catch (e: Exception) {
            error = "Error al cargar datos: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item {
                Button(onClick = onNavigateToCreateVacante, modifier = Modifier.fillMaxWidth()) {
                    Text("Registrar Nueva Vacante")
                }
            }
            
            item {
                Text("Demanda de Talento", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }

            if (error != null) {
                item { Text(text = error!!, color = Color.Red) }
            } else if (vacancies.isEmpty()) {
                item { Text("No hay vacantes abiertas.", color = Color.Gray) }
            } else {
                items(vacancies) { vacancy ->
                    VacancyCard(vacante = vacancy, onClick = { onNavigateToDetail(vacancy.id) })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VacancyCard(vacante: DemandaVacancyUi, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(vacante.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text(vacante.department, color = Color.Gray, style = MaterialTheme.typography.bodyMedium)

            if (vacante.skills.isNotEmpty()) {
                Text("Skills Requeridos:", fontWeight = FontWeight.Medium, style = MaterialTheme.typography.bodySmall)
                Row {
                    vacante.skills.take(3).forEach { skill ->
                        Chip(label = { Text(skill) }, modifier = Modifier.padding(end = 4.dp))
                    }
                    if (vacante.skills.size > 3) {
                        Chip(label = { Text("+${vacante.skills.size - 3}") })
                    }
                }
            } 

            Text("Estado: ${vacante.status}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun Chip(label: @Composable () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
    ) {
        Box(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
            label()
        }
    }
}