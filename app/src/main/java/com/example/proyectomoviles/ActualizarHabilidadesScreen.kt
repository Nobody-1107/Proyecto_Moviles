package com.example.proyectomoviles

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectomoviles.viewmodel.ActualizarHabilidadesViewModel

@Composable
fun ActualizarHabilidadesScreen(
    onNavigateBack: () -> Unit,
    viewModel: ActualizarHabilidadesViewModel = viewModel()
) {
    val profile by viewModel.profile.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val updateSuccess by viewModel.updateSuccess.collectAsState()
    val context = LocalContext.current

    // Cargar datos al inicio
    LaunchedEffect(Unit) {
        viewModel.cargarDatosProfile()
    }

    // Escuchar si la actualización fue exitosa
    LaunchedEffect(updateSuccess) {
        if (updateSuccess) {
            Toast.makeText(context, "Disponibilidad actualizada correctamente", Toast.LENGTH_SHORT).show()
            onNavigateBack()
        }
    }

    if (isLoading && profile == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (error != null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Error: $error", color = Color.Red)
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { viewModel.cargarDatosProfile() }) {
                    Text("Reintentar")
                }
            }
        }
        return
    }

    profile?.let { data ->
        // Mantenemos el estado local del switch sincronizado con el perfil, 
        // pero permitimos que el usuario lo cambie antes de guardar.
        var disponible by remember(data.isAvailableForChange) { mutableStateOf(data.isAvailableForChange) }
        var preferences by remember { mutableStateOf("") } // Campo nuevo para intereses

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- Header ---
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                    Text("Estado de Disponibilidad", style = MaterialTheme.typography.titleMedium)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Gestiona tu estado: ${data.fullName}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }

            // --- Tarjeta de Disponibilidad (Semáforo) ---
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (disponible) Color(0xFFE8F5E9) else Color(0xFFFFEBEE) // Verde claro o Rojo claro
                    )
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (disponible) "DISPONIBLE" else "NO DISPONIBLE",
                                    fontWeight = FontWeight.Bold,
                                    color = if (disponible) Color(0xFF2E7D32) else Color(0xFFC62828)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (disponible) 
                                        "Estás visible para nuevos proyectos y ascensos." 
                                    else 
                                        "Actualmente no estás buscando cambios.",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Switch(
                                checked = disponible, 
                                onCheckedChange = { disponible = it }
                            )
                        }
                    }
                }
            }

            // --- Campo de Intereses (Nuevo) ---
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("¿Qué buscas?", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = preferences,
                            onValueChange = { preferences = it },
                            label = { Text("Ej: Proyectos de IA, Liderazgo, etc.") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = disponible // Solo habilitado si está disponible
                        )
                        if (!disponible) {
                            Text(
                                "Activa tu disponibilidad para indicar tus preferencias.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }

            // --- Botón Guardar ---
            item {
                Button(
                    onClick = { 
                        // Llamamos al ViewModel para guardar en el backend
                        viewModel.guardarDisponibilidad(disponible)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading // Deshabilitar si está cargando
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Guardando...")
                    } else {
                        Text("Guardar Estado")
                    }
                }
            }
        }
    }
}
