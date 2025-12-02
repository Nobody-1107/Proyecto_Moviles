package com.example.proyectomoviles

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AdminPerfilScreen(
    onLogout: () -> Unit,
    onNavigateToGestionSeguridad: () -> Unit,
    onNavigateToRegisterCollaborator: () -> Unit
) {
    // 1. OBTENER DATOS DE SESIÓN (Igual que en los otros perfiles)
    val context = LocalContext.current
    val sharedPref = remember {
        context.getSharedPreferences("UserSession", android.content.Context.MODE_PRIVATE)
    }

    // Recuperamos los datos guardados en el Login
    val realName = sharedPref.getString("USER_NAME", "Administrador") ?: "Administrador"
    val realRole = sharedPref.getString("USER_ROLE", "Admin del Sistema") ?: "Admin del Sistema"
    val realDept = sharedPref.getString("USER_DEPARTMENT", "TI") ?: "TI"
    // Usamos la posición para el subtítulo (ej: CTO, Gerente de Sistemas)
    val realPosition = sharedPref.getString("USER_POSITION", "Sistemas") ?: "Sistemas"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- TARJETA DE PERFIL ---
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Shield, contentDescription = "Admin Icon", modifier = Modifier.size(40.dp), tint = Color.Magenta)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        // USAMOS EL NOMBRE REAL
                        Text(realName, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        // USAMOS EL PUESTO REAL
                        Text(realPosition, fontSize = 16.sp)
                    }
                }
            }
        }

        // --- TARJETA DE ROL ---
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Rol", fontWeight = FontWeight.Bold)
                    // USAMOS EL ROL REAL
                    Text(realRole)
                }
            }
        }

        // --- TARJETA DE DEPARTAMENTO ---
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Departamento ID", fontWeight = FontWeight.Bold)
                    // USAMOS EL DEPARTAMENTO REAL
                    Text(realDept)
                }
            }
        }

        // --- BOTONES DE ACCIÓN ---
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
                OutlinedButton(onClick = onLogout, modifier = Modifier.fillMaxWidth()) {
                    Text("Cerrar Sesión")
                }
            }
        }

        // --- INFO ---
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