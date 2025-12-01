package com.example.proyectomoviles

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyectomoviles.viewmodel.RecommendedCandidateUi
import com.example.proyectomoviles.viewmodel.VacancyDetailViewModel
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Arrangement // Probablemente lo necesites
import androidx.compose.foundation.layout.ExperimentalLayoutApi // Por si tu versión lo pide

@Composable
fun VacancyDetailScreen(
    vacancyId: Int,
    navController: NavController,
    viewModel: VacancyDetailViewModel = viewModel()
) {
    val vacancy by viewModel.vacancy.collectAsState()
    val candidates by viewModel.candidates.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val vacancyDeleted by viewModel.vacancyDeleted.collectAsState()
    val context = LocalContext.current
    val openDeleteDialog = remember { mutableStateOf(false) }


    LaunchedEffect(vacancyId) {
        viewModel.loadVacancyDetails(vacancyId)
    }

    LaunchedEffect(vacancyDeleted) {
        if (vacancyDeleted) {
            Toast.makeText(context, "Vacante eliminada", Toast.LENGTH_SHORT).show()
            navController.navigate("demanda") {
                popUpTo(navController.graph.startDestinationId) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
    }

    if (openDeleteDialog.value) {
        AlertDialog(
            onDismissRequest = { openDeleteDialog.value = false },
            title = { Text("Confirmar Eliminación") },
            text = { Text("¿Estás seguro de que quieres eliminar esta vacante? Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(
                    onClick = {
                        openDeleteDialog.value = false
                        viewModel.deleteVacancy(vacancyId)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                Button(onClick = { openDeleteDialog.value = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (isLoading && vacancy == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    
    if (error != null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Error: $error", color = Color.Red)
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            Text("Gestión de Talento", style = MaterialTheme.typography.headlineMedium)
            Text("Dashboard de recursos humanos", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            Spacer(modifier = Modifier.height(24.dp))
        }

        vacancy?.let { vac ->
            item {
                VacancyInfoCard(
                    title = vac.title,
                    department = vac.department,
                    skills = vac.skills,
                    onEditClick = {
                        navController.navigate("edit_vacancy/$vacancyId")
                    },
                    onDeleteClick = {
                        openDeleteDialog.value = true
                    }
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        item {
            Text("Candidatos Internos Recomendados", style = MaterialTheme.typography.titleLarge)
            Text(
                "Mostrando candidatos ordenados por mejor compatibilidad.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        items(candidates) { candidate ->
            CandidateCard(
                candidate = candidate,
                vacancyId = vacancyId // Pasar el ID de la vacante
            )
        }
    }
}

private fun getGradeLabel(grade: Int): String {
    return when (grade) {
        1 -> "Básico"
        2 -> "Intermedio"
        3 -> "Avanzado"
        else -> "N/A"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VacancyInfoCard(
    title: String,
    department: String,
    skills: List<Pair<String, Int>>,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Título del Puesto:", style = MaterialTheme.typography.labelMedium)
            Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))

            Text("Departamento:", style = MaterialTheme.typography.labelMedium)
            Text(department, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))

            Text("Skills Requeridos:", style = MaterialTheme.typography.labelMedium)
            FlowRow(modifier = Modifier.fillMaxWidth()) {
                skills.forEach { (skill, grade) ->
                    Chip(
                        label = { Text("$skill (${getGradeLabel(grade)})") },
                        modifier = Modifier.padding(end = 4.dp, bottom = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar")
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CandidateCard(
    candidate: RecommendedCandidateUi,
    vacancyId: Int // Recibir el ID de la vacante
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = {
             val intent = Intent(context, CandidateDetailActivity::class.java).apply {
                candidate.profile.id?.let { profileId ->
                    putExtra("CANDIDATE_ID", profileId)
                    putExtra("VACANCY_ID", vacancyId) // Usar el ID real
                }
            }
            if (candidate.profile.id != null) {
                context.startActivity(intent)
            }
        }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(candidate.profile.fullName, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                Text(candidate.profile.position, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            }
            Text(
                "${candidate.compatibility}%",
                color = if (candidate.compatibility > 70) Color(0xFF2E7D32) else if (candidate.compatibility > 40) Color(0xFFF9A825) else Color(0xFFC62828),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = "Ver perfil",
                modifier = Modifier.padding(start = 8.dp).size(16.dp),
                tint = Color.Gray
            )
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
