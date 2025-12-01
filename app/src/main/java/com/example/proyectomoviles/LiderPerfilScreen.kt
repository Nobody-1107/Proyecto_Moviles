package com.example.proyectomoviles

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
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
fun LiderPerfilScreen(onLogout: () -> Unit, onNavigateToUpdateSkills: () -> Unit) {

    // 1. OBTENER DATOS DE SESIÓN (Igual que en Colaborador)
    val context = LocalContext.current
    val sharedPref = remember {
        context.getSharedPreferences("UserSession", android.content.Context.MODE_PRIVATE)
    }

    // Leemos los datos. Si alguno es null, ponemos un valor por defecto.
    val realName = sharedPref.getString("USER_NAME", "Usuario") ?: "Usuario"
    val realRole = sharedPref.getString("USER_ROLE", "Líder de Área") ?: "Líder de Área"
    val realDept = sharedPref.getString("USER_DEPARTMENT", "General") ?: "General"

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
                    Icon(Icons.Default.Person, contentDescription = "User Icon", modifier = Modifier.size(40.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        // USAMOS EL NOMBRE REAL
                        Text(realName, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        // USAMOS EL ROL REAL (Como subtítulo)
                        Text(realRole, fontSize = 16.sp)
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
                    // USAMOS EL DEPARTAMENTO REAL (El ID que guardamos)
                    Text(realDept)
                }
            }
        }

        // --- GESTIÓN DE DISPONIBILIDAD ---
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToUpdateSkills() },
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
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
                }
            }
        }

        // --- BOTÓN LOGOUT ---
        item {
            OutlinedButton(onClick = onLogout, modifier = Modifier.fillMaxWidth()) {
                Text("Cerrar Sesión")
            }
        }

        // --- INFO ---
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Información", fontWeight = FontWeight.Bold)
                    Text("• Acceso como líder de área.")
                    Text("• Gestiona habilidades y vacantes.")
                }
            }
        }
    }
}
