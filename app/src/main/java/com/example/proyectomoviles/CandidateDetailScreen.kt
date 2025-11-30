package com.example.proyectomoviles

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectomoviles.viewmodel.CandidateDetailViewModel

// --- CORRECCIÓN AQUÍ ---
// Agregamos esta línea para silenciar el error de Material3 Experimental
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CandidateDetailScreen(
    vacancyId: Int,
    candidateId: String,
    viewModel: CandidateDetailViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    // ... el resto de tu código sigue igual ...
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(vacancyId, candidateId) {
        viewModel.loadCandidateDetails(vacancyId, candidateId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Análisis de Brecha") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.error != null) {
                Text(
                    text = "Error: ${uiState.error}",
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                uiState.candidateProfile?.let { candidate ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text("Gestión de Talento", style = MaterialTheme.typography.headlineMedium)
                        Text("Dashboard de recursos humanos", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                        Spacer(modifier = Modifier.height(24.dp))

                        Text("Análisis de Brecha: ${candidate.fullName}", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(16.dp))

                        // 1. Skills Gap Chart Card
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Análisis de Brecha contra Puesto", style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                uiState.skillsComparison.forEach { (skill, required, profile) ->
                                    SkillBar(skillName = skill, requiredGrade = required, profileGrade = profile)
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // 2. Candidate Info Card
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Información del Candidato", style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("Nombre:", style = MaterialTheme.typography.labelMedium)
                                Text(candidate.fullName, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Rol Actual:", style = MaterialTheme.typography.labelMedium)
                                Text(candidate.position, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Departamento:", style = MaterialTheme.typography.labelMedium)
                                Text(uiState.candidateDepartment, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SkillBar(skillName: String, requiredGrade: Int, profileGrade: Int) {
    val maxGrade = 3f // Usar Float para los cálculos
    val requiredPercentage = (requiredGrade / maxGrade).coerceIn(0f, 1f)
    val profilePercentage = (profileGrade / maxGrade).coerceIn(0f, 1f)
    val gapPercentage = (requiredPercentage - profilePercentage).coerceAtLeast(0f)

    Column {
        Text(skillName, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(4.dp))
        
        // El gráfico se dibuja con 3 partes: Dominio, Brecha y Fondo
        Box(modifier = Modifier.fillMaxWidth().height(24.dp)) {
            // 1. Fondo (representa el 100% del skill)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.LightGray.copy(alpha = 0.5f))
            )

            // 2. Barra de Dominio Actual (azul)
            if (profilePercentage > 0) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(fraction = profilePercentage)
                        .align(Alignment.CenterStart)
                        .background(MaterialTheme.colorScheme.primary)
                )
            }

            // 3. Barra de Brecha (rojo) - Se dibuja encima de la azul, a partir de donde termina
            if (gapPercentage > 0) {
                 Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(fraction = requiredPercentage) // La brecha llega hasta donde requiere la vacante
                        .align(Alignment.CenterStart)
                        .background(Color.Red.copy(alpha = 0.6f))
                )
                 // Se redibuja la barra de dominio para que quede por encima de la de brecha
                 if (profilePercentage > 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(fraction = profilePercentage)
                            .align(Alignment.CenterStart)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                }
            }
        }
    }
}