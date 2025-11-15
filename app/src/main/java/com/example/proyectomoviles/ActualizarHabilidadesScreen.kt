package com.example.proyectomoviles

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ActualizarHabilidadesScreen(onNavigateBack: () -> Unit) {
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
            Text("Detalle y Edición: Michael Johnson", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }

        // --- Información del Colaborador ---
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Información del Colaborador", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    InfoRow("Nombre", "Michael Johnson")
                    InfoRow("Rol Actual", "Frontend Developer")
                    InfoRow("Departamento", "IT")
                }
            }
        }

        // --- Skills Section ---
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Skills", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Button(onClick = { /* TODO */ }) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Añadir Skill")
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    SkillEditItem("React", 0.85f)
                    SkillEditItem("TypeScript", 0.75f)
                    SkillEditItem("UI Design", 0.60f)
                }
            }
        }

        // --- Certificaciones Section ---
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Certificaciones/Cursos", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Button(onClick = { /* TODO */ }) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Añadir")
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    CertificationItem("React Advanced Certification")
                    CertificationItem("AWS Cloud Practitioner")
                }
            }
        }

        // --- Disponibilidad & Guardar ---
        item {
            var disponible by remember { mutableStateOf(false) }
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
                    Button(onClick = { /* TODO: Save */ onNavigateBack() }, modifier = Modifier.fillMaxWidth()) {
                        Text("Guardar Cambios")
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

@Composable
private fun SkillEditItem(name: String, initialValue: Float) {
    var sliderValue by remember { mutableStateOf(initialValue) }
    Card(modifier = Modifier.fillMaxWidth().padding(vertical=4.dp), border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(name, fontWeight = FontWeight.Bold)
                IconButton(onClick = { /* TODO: Delete */ }, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
                }
            }
            Text("Nivel de dominio", style=MaterialTheme.typography.bodySmall)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Slider(
                    value = sliderValue,
                    onValueChange = { sliderValue = it },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("${(sliderValue * 100).toInt()}%", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun CertificationItem(name: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.weight(1f),
            colors = OutlinedTextFieldDefaults.colors(disabledTextColor = MaterialTheme.colorScheme.onSurface)
        )
        IconButton(onClick = { /* TODO: Delete */ }) {
            Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
        }
    }
}

