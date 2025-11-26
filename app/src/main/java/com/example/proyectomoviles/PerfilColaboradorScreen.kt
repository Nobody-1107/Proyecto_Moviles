package com.example.proyectomoviles

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectomoviles.viewmodel.ActualizarHabilidadesViewModel

@Composable
fun PerfilColaboradorScreen(
    onLogout: () -> Unit, 
    onNavigateToHabilidades: () -> Unit,
    viewModel: ActualizarHabilidadesViewModel = viewModel()
) {
    val profile by viewModel.profile.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Load data when the screen is first composed
    LaunchedEffect(Unit) {
        if (profile == null) { // Load only if not already loaded
            viewModel.cargarDatosProfile()
        }
    }

    if (isLoading && profile == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Display data from the profile object
        profile?.let { userProfile ->
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.Person,
                                contentDescription = "User Icon",
                                modifier = Modifier.size(40.dp),
                                tint = Color.Green
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(userProfile.fullName, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                Text(userProfile.position, fontSize = 16.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Botón "Actualizar Habilidades" eliminado.
            // En su lugar, quizás quieras permitir navegar a la pantalla de "Estado de Disponibilidad" 
            // haciendo click en la tarjeta de disponibilidad más abajo, o simplemente dejarlo
            // como una pantalla informativa si así lo prefieres.
            
            // Si deseas redirigir al usuario a "Estado de Disponibilidad" desde una tarjeta más amigable:
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToHabilidades() }, // Hacemos clickeable toda la tarjeta
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Edit, contentDescription = null)
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text("Gestionar Disponibilidad", fontWeight = FontWeight.Bold)
                                Text("Ver estado e intereses", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        Icon(Icons.Default.KeyboardArrowRight, contentDescription = null)
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Rol", fontWeight = FontWeight.Bold)
                        Text(userProfile.role)
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Departamento ID", fontWeight = FontWeight.Bold)
                        Text(userProfile.departmentId?.toString() ?: "No asignado")
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            // La antigua tarjeta de switch la podemos dejar solo como visualización
            // ya que la edición ahora se hace en la otra pantalla
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Estado Actual")
                        // Solo texto, sin switch interactivo aquí para no duplicar lógica
                        Text(
                            if (userProfile.isAvailableForChange) "Disponible" else "No disponible",
                            fontWeight = FontWeight.Bold,
                            color = if (userProfile.isAvailableForChange) Color(0xFF2E7D32) else Color.Gray
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cerrar Sesión")
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF0F4F8))
                    .padding(16.dp)
            ) {
                Text("Información", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("• Vista de colaborador")
                Text("• Consulta tu equipo y sus habilidades")
                Text("• Sugiere actualizaciones de perfiles")
                Text("• Accede a la asistencia de Onboarding")
            }
        }
    }
}