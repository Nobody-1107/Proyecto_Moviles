package com.example.proyectomoviles

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
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
fun PerfilColaboradorScreen(
    onLogout: () -> Unit,
    onNavigateToHabilidades: () -> Unit,
    viewModel: ActualizarHabilidadesViewModel = viewModel()
) {
    val context = LocalContext.current
    val sharedPref = remember {
        context.getSharedPreferences("UserSession", android.content.Context.MODE_PRIVATE)
    }

    val realName = sharedPref.getString("USER_NAME", "Usuario") ?: "Usuario"
    val realRole = sharedPref.getString("USER_ROLE", "Colaborador") ?: "Colaborador"
    val realPosition = sharedPref.getString("USER_POSITION", "Puesto no definido") ?: "Sin puesto"
    val realDeptId = sharedPref.getString("USER_DEPARTMENT", "N/A") ?: "N/A"
    
    var isAvailable by remember { 
        mutableStateOf(sharedPref.getBoolean("USER_IS_AVAILABLE", false)) 
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp) 
    ) {

        item {
            Text(
                text = "Gestión de Talento",
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFF1E1E1E),
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White) 
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(50.dp),
                            shape = MaterialTheme.shapes.medium,
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.padding(10.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = realName, 
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = realPosition, 
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Rol Asignado", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                            Text(text = realRole, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Departamento ID", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                            Text(text = realDeptId, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // --- Availability Section ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Column {
                        Text("Disponibilidad", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text("¿Abierto a nuevas oportunidades?", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                    Switch(
                        checked = isAvailable,
                        onCheckedChange = { 
                            isAvailable = it
                            // Here you'd call the ViewModel to save the state
                            // For now, we save it directly to SharedPreferences for persistence across app restarts
                            with(sharedPref.edit()) {
                                putBoolean("USER_IS_AVAILABLE", it)
                                apply()
                            }
                        }
                    )
                }
            }
        }

        item {
            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A5C85)) 
            ) {
                Text("Cerrar Sesión")
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF0F4F8), shape = MaterialTheme.shapes.medium)
                    .padding(16.dp)
            ) {
                Text("Información", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("• Vista de colaborador")
                Text("• Consulta tu equipo y sus habilidades")
                Text("• Sugiere actualizaciones de perfiles")
                Text("• Accede a la asistencia de Onboarding")
            }
        }
    }
}