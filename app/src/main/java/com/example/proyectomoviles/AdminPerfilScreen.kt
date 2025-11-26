package com.example.proyectomoviles

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AdminPerfilScreen(
    onLogout: () -> Unit, 
    onNavigateToUpdateSkills: () -> Unit,
    onNavigateToGestionSeguridad: () -> Unit,
    onNavigateToRegisterCollaborator: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Shield, contentDescription = "Admin Icon", modifier = Modifier.size(40.dp), tint = Color.Magenta)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Administrador del Sistema", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text("admin@empresa.com", fontSize = 16.sp)
                    }
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Rol", fontWeight = FontWeight.Bold)
                    Text("Administrador del Sistema")
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Departamento", fontWeight = FontWeight.Bold)
                    Text("TI - Sistemas")
                }
            }
        }

        item {
            Column {
                Button(onClick = onNavigateToRegisterCollaborator, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0073E6))) {
                    Text("Registrar Nuevo Colaborador")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onNavigateToGestionSeguridad, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color.Magenta)) {
                    Text("Gestión de Seguridad y Autenticación")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onNavigateToUpdateSkills, modifier = Modifier.fillMaxWidth()) {
                    Text("Actualizar Habilidades y Certificaciones")
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(onClick = onLogout, modifier = Modifier.fillMaxWidth()) {
                    Text("Cerrar Sesión")
                }
            }
        }
        
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Información", fontWeight = FontWeight.Bold)
                    Text("• Acceso completo al sistema.")
                    Text("• Gestión de seguridad y roles.")
                }
            }
        }
    }
}
