package com.example.proyectomoviles

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.proyectomoviles.model.Profile
import com.example.proyectomoviles.viewmodel.GestionLiderViewModel

@Composable
fun GestionLiderScreen(
    onNavigateToRegisterCollaborator: () -> Unit,
    onNavigateToCollaboratorDetail: (String) -> Unit, // Para navegar al detalle/editar
    viewModel: GestionLiderViewModel = viewModel()
) {
    val collaborators by viewModel.collaborators.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadCollaborators()
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Button(
            onClick = onNavigateToRegisterCollaborator,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrar Nuevo Colaborador")
        }
        Spacer(modifier = Modifier.height(24.dp))

        Text("Mi Equipo", style = MaterialTheme.typography.titleLarge)
        
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (error != null) {
            Text(text = error!!, color = Color.Red)
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(collaborators) { collaborator ->
                    collaborator.id?.let { // Asegurarnos de que el ID no es nulo
                        CollaboratorCard(
                            collaborator = collaborator,
                            onClick = { onNavigateToCollaboratorDetail(it) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CollaboratorCard(collaborator: Profile, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(collaborator.fullName, fontWeight = FontWeight.Bold)
            Text(collaborator.position, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        }
    }
}