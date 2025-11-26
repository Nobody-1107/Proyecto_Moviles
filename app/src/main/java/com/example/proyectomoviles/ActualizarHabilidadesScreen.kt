package com.example.proyectomoviles

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectomoviles.viewmodel.ActualizarHabilidadesViewModel

@Composable
fun ActualizarHabilidadesScreen(
    onNavigateBack: () -> Unit,
    viewModel: ActualizarHabilidadesViewModel = viewModel()
) {
    // Use the new 'profile' StateFlow from the ViewModel
    val profile by viewModel.profile.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // Call the updated function to load data
    LaunchedEffect(Unit) {
        viewModel.cargarDatosProfile()
    }

    if (isLoading) {
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

    // Use the new 'profile' object
    profile?.let { data ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- Back Button & Title ---
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                    Text("Volver a Gestión", style = MaterialTheme.typography.titleMedium)
                }
                Spacer(modifier = Modifier.height(8.dp))
                // Use properties from the 'Profile' model
                Text("Detalle y Edición: ${data.fullName}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }

            // --- Información del Perfil ---
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Información del Perfil", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(16.dp))
                        // Use properties from the 'Profile' model
                        InfoRow("Nombre Completo", data.fullName)
                        InfoRow("Cargo", data.position)
                        InfoRow("Rol", data.role)
                    }
                }
            }

            // --- Disponibilidad & Guardar ---
            item {
                // Use 'isAvailableForChange' from the 'Profile' model
                var disponible by remember { mutableStateOf(data.isAvailableForChange) }
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)){
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Disponible para movilidad interna")
                            Switch(checked = disponible, onCheckedChange = { disponible = it })
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { /* TODO: Save logic */ onNavigateBack() }, modifier = Modifier.fillMaxWidth()) {
                            Text("Guardar Cambios")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(label, style = MaterialTheme.typography.labelSmall)
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(disabledTextColor = MaterialTheme.colorScheme.onSurface)
        )
    }
}