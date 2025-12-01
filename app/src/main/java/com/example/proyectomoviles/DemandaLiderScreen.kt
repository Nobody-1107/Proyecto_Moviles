package com.example.proyectomoviles

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.proyectomoviles.model.Suggestion
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
    onNavigateToDetail: (Int) -> Unit,
    onNavigateToEditProfile: (String) -> Unit
) {
    var vacancies by remember { mutableStateOf<List<DemandaVacancyUi>>(emptyList()) }
    var suggestions by remember { mutableStateOf<List<Suggestion>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    // Lógica de carga de datos directamente en el Composable
    LaunchedEffect(Unit) {
        isLoading = true
        error = null
        try {
            val apiService = RetrofitClient.instance.create(ApiService::class.java)
            
            // Cargar datos principales (Vacantes)
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

            // Cargar datos secundarios (Sugerencias) de forma AISLADA
            try {
                val fetchedSuggestions = apiService.getSuggestions()
                if (fetchedSuggestions.isEmpty()) {
                    // AÑADIR EJEMPLO SI LA LISTA ESTÁ VACÍA
                    suggestions = listOf(
                        Suggestion(
                            text = "Este colaborador ha mostrado un gran interés en el desarrollo de nuevas interfaces. Podría ser un buen candidato para el equipo de UI/UX.",
                            collaboratorName = "Aaron Soto",
                            collaboratorId = "a3d9b8f0-1b2c-3d4e-5f6a-7b8c9d0e1f2a" // Reemplaza con un ID real de tu BD para que la navegación funcione
                        )
                    )
                } else {
                    suggestions = fetchedSuggestions
                }
            } catch (e: Exception) {
                println("Fallo al cargar sugerencias (ignorado): ${e.message}")
                suggestions = listOf( // AÑADIR EJEMPLO TAMBIÉN EN CASO DE ERROR
                    Suggestion(
                        text = "Este colaborador ha mostrado un gran interés en el desarrollo de nuevas interfaces. Podría ser un buen candidato para el equipo de UI/UX.",
                        collaboratorName = "Aaron Soto",
                        collaboratorId = "a3d9b8f0-1b2c-3d4e-5f6a-7b8c9d0e1f2a" // Reemplaza con un ID real de tu BD
                    )
                )
            }

        } catch (e: Exception) {
            // Este error solo saltará si falla la carga de vacantes
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
                Button(
                    onClick = onNavigateToCreateVacante, 
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp), // Botón más alto
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp, pressedElevation = 2.dp), // Sombra
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Registrar", modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Registrar Nueva Vacante", style = MaterialTheme.typography.titleLarge) // Texto más grande
                }
            }
            
            item {
                Text("Demanda de Talento", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            }

            if (error != null) {
                item { Text(text = error!!, color = Color.Red) }
            } else if (vacancies.isEmpty()) {
                item { Text("No hay vacantes abiertas.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
            } else {
                items(vacancies) { vacancy ->
                    VacancyCard(vacante = vacancy, onClick = { onNavigateToDetail(vacancy.id) })
                }
            }

            item {
                Text("Sugerencias Recibidas", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp))
            }

            if (suggestions.isEmpty()) {
                item { Text("No hay sugerencias nuevas.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
            } else {
                items(suggestions) { suggestion ->
                    SuggestionCard(
                        suggestion = suggestion,
                        onReviewClick = { onNavigateToEditProfile(suggestion.collaboratorId) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VacancyCard(vacante: DemandaVacancyUi, onClick: () -> Unit) {
    OutlinedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                vacante.title,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                vacante.department,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )

            if (vacante.skills.isNotEmpty()) {
                Text(
                    "Skills Requeridos:",
                    fontWeight = FontWeight.Medium,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Row {
                    vacante.skills.take(3).forEach { skill ->
                        Chip(label = { Text(skill) }, modifier = Modifier.padding(end = 4.dp))
                    }
                    if (vacante.skills.size > 3) {
                        Chip(label = { Text("+${vacante.skills.size - 3}") })
                    }
                }
            }

            Text(
                "Estado: ${vacante.status}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun SuggestionCard(suggestion: Suggestion, onReviewClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = suggestion.text,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Sugerido para: ${suggestion.collaboratorName}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                FilledTonalButton(onClick = onReviewClick) {
                    Text("REVISAR")
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Revisar", modifier = Modifier.size(18.dp))
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
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
    ) {
        Box(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
            label()
        }
    }
}
