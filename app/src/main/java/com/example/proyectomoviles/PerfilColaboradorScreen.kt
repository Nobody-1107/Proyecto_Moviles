package com.example.proyectomoviles

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PerfilColaboradorScreen(onLogout: () -> Unit, onNavigateToHabilidades: () -> Unit) {
    var isAvailableForChange by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
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
                            Text("Michael Johnson", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            Text("Frontend Developer", fontSize = 16.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // --- Nuevo Botón para ir a Editar Habilidades ---
        item {
            Button(
                onClick = onNavigateToHabilidades,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
            ) {
                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Actualizar Habilidades y Certificaciones")
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Rol", fontWeight = FontWeight.Bold)
                    Text("Colaborador")
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Departamento", fontWeight = FontWeight.Bold)
                    Text("IT")
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Disponible para cambio")
                    Switch(
                        checked = isAvailableForChange,
                        onCheckedChange = { isAvailableForChange = it }
                    )
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