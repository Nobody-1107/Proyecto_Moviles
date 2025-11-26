package com.example.proyectomoviles

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectomoviles.viewmodel.MiEquipoViewModel

@Composable
fun MiEquipoColaboradorScreen(
    viewModel: MiEquipoViewModel = viewModel()
) {
    var showDialog by remember { mutableStateOf(false) }
    val teamMembers by viewModel.teamMembers.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarEquipo()
    }

    if (showDialog) {
        SugerirActualizacionDialog(
            onDismissRequest = { showDialog = false },
            onEnviarSugerencia = { suggestion ->
                // TODO: Handle suggestion
                println("Suggestion: $suggestion")
            }
        )
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (error != null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Error al cargar equipo: $error", color = Color.Red)
        }
        // Aun así permitimos ver la lista vacía o reintentar si se quisiera
    }

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        item {
            Button(
                onClick = { showDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sugerir actualización")
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        items(teamMembers) { member ->
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(member.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(member.title, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    if (member.skills.isEmpty()) {
                        Text("Sin habilidades registradas", style = MaterialTheme.typography.bodySmall)
                    } else {
                        member.skills.forEach { skillName ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(skillName)
                                // Como el backend no devuelve % de progreso, quitamos la barra por ahora
                                // o mostramos un valor fijo si se requiere
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }
        }
    }
}
