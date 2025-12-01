package com.example.proyectomoviles

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectomoviles.model.Profile
import com.example.proyectomoviles.viewmodel.GestionLiderViewModel

@Composable
fun GestionLiderScreen(
    onNavigateToRegisterCollaborator: () -> Unit,
    onNavigateToCollaboratorDetail: (String) -> Unit,
    onNavigateToSugerencias: () -> Unit, 
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
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp, pressedElevation = 2.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Registrar")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Registrar Nuevo Colaborador", style = MaterialTheme.typography.titleMedium)
        }
        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Mi Equipo", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            FilledTonalButton(onClick = onNavigateToSugerencias) {
                Text("Sugerencias")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (error != null) {
            Text(text = error!!, color = Color.Red)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(collaborators) { collaborator ->
                    collaborator.id?.let { 
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
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                collaborator.fullName, 
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                collaborator.position, 
                style = MaterialTheme.typography.bodyMedium, 
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
