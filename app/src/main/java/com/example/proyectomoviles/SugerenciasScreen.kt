package com.example.proyectomoviles

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectomoviles.model.Sugerencia
import com.example.proyectomoviles.viewmodel.SugerenciasViewModel

@Composable
fun SugerenciasScreen(
    onNavigateBack: () -> Unit,
    onNavigateToEditProfile: (String) -> Unit, // Para la navegación a editar perfil
    viewModel: SugerenciasViewModel = viewModel()
) {
    val sugerencias by viewModel.sugerencias.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // Carga las sugerencias cuando la pantalla aparece por primera vez
    LaunchedEffect(Unit) {
        viewModel.loadSugerencias()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Buzón de Sugerencias",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {
            // Muestra un indicador de carga mientras se obtienen los datos
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (error != null) {
            // Muestra un mensaje de error si algo falla
            Text(error!!, color = MaterialTheme.colorScheme.error)
        } else if (sugerencias.isEmpty()) {
            // Mensaje si no hay sugerencias
            Text("No hay sugerencias por el momento.")
        } else {
            // Lista de sugerencias
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(sugerencias) { sugerencia ->
                    SugerenciaCard(
                        sugerencia = sugerencia,
                        onDeleteClick = { viewModel.deleteSugerencia(sugerencia.id) },
                        onEditProfileClick = {
                            sugerencia.profile?.id?.let {
                                onNavigateToEditProfile(it)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SugerenciaCard(
    sugerencia: Sugerencia,
    onDeleteClick: () -> Unit,
    onEditProfileClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Columna para el texto, para que el botón no afecte su alineación
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = sugerencia.profile?.fullName ?: "Anónimo",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = sugerencia.descripcion,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 3, // Un poco más de espacio para la descripción
                        overflow = TextOverflow.Ellipsis
                    )
                }
                // Botón de eliminar alineado a la derecha
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar sugerencia", tint = MaterialTheme.colorScheme.error)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Estado de la sugerencia
                Text(
                    text = "Estado: ${sugerencia.estado.name.replace('_', ' ').lowercase().replaceFirstChar { it.titlecase() }}",
                    style = MaterialTheme.typography.bodySmall,
                    color = when (sugerencia.estado) {
                        com.example.proyectomoviles.model.EstadoSugerencia.ACEPTADA -> Color(0xFF388E3C) // Verde
                        com.example.proyectomoviles.model.EstadoSugerencia.EN_PROCESO -> Color(0xFFFFA000) // Ámbar
                        com.example.proyectomoviles.model.EstadoSugerencia.RECHAZADA -> Color.Red
                    },
                    fontWeight = FontWeight.Medium
                )

                // Botón para editar, solo si no es anónimo
                if (sugerencia.profile?.id != null) {
                    TextButton(onClick = onEditProfileClick) {
                        Text("Editar Perfil")
                    }
                }
            }
        }
    }
}
