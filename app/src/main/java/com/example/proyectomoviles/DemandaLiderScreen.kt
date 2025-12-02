package com.example.proyectomoviles

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectomoviles.viewmodel.DemandaLiderViewModel
import com.example.proyectomoviles.viewmodel.VacancyUi

@Composable
fun DemandaLiderScreen(
    onNavigateToCreateVacante: () -> Unit,
    onNavigateToDetail: (Int) -> Unit,
    viewModel: DemandaLiderViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadDashboardData()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Header(onNavigateToCreateVacante)
        }

        if (uiState.isLoading) {
            item { 
                CircularProgressIndicator(modifier = Modifier.wrapContentSize(Alignment.Center)) 
            }
        }

        uiState.error?.let {
            item { Text(text = it, color = MaterialTheme.colorScheme.error) }
        }

        item {
            Text("Demanda de Talento", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(top = 16.dp))
        }

        items(uiState.vacancies) { vacancy ->
            VacancyCard(vacancy, onNavigateToDetail)
        }
    }
}

@Composable
private fun Header(onNavigateToCreateVacante: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Dashboard de Líder", style = MaterialTheme.typography.headlineMedium)
        Button(onClick = onNavigateToCreateVacante) {
            Icon(Icons.Default.Add, contentDescription = "Crear Vacante")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Nueva Vacante")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VacancyCard(vacancy: VacancyUi, onNavigateToDetail: (Int) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onNavigateToDetail(vacancy.id) },
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(vacancy.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
            Text("Departamento: ${vacancy.department}", style = MaterialTheme.typography.bodyMedium)
            Text("Estado: ${vacancy.status}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Requisitos:", fontWeight = FontWeight.SemiBold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                items(vacancy.skills) { skill ->
                    AssistChip(onClick = {}, label = { Text(skill) })
                }
            }
        }
    }
}