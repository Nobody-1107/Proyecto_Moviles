package com.example.proyectomoviles

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LiderPerfilScreen(onLogout: () -> Unit, onNavigateToUpdateSkills: () -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, contentDescription = "User Icon", modifier = Modifier.size(40.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("James Smith", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text("Líder de Área", fontSize = 16.sp)
                    }
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Rol", fontWeight = FontWeight.Bold)
                    Text("Líder de Área")
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Departamento", fontWeight = FontWeight.Bold)
                    Text("Marketing")
                }
            }
        }

        item {
            Column {
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
                    Text("• Acceso como líder de área.")
                    Text("• Gestiona habilidades y vacantes.")
                }
            }
        }
    }
}
