package com.example.proyectomoviles

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class Vacante(
    val title: String,
    val department: String,
    val skills: List<String>,
    val coverage: Float
)

@Composable
fun DemandaLiderScreen(onNavigateToCreateVacante: () -> Unit) {
    val vacantes = listOf(
        Vacante("Software Engineer", "Ingeniería", listOf("React", "TypeScript"), 0.75f),
        Vacante("Product Manager", "Producto", listOf("Jira", "Confluence"), 0.5f)
    )

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        item {
            Button(
                onClick = onNavigateToCreateVacante,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrar Nueva Vacante")
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Text("Demanda de Talento", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(vacantes) { vacante ->
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(vacante.title, fontWeight = FontWeight.Bold)
                    Text(vacante.department, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Skills Requeridos:", fontWeight = FontWeight.Medium)
                    Row {
                        vacante.skills.forEach { skill ->
                            Chip(label = { Text(skill) }, modifier = Modifier.padding(end = 4.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Cobertura: ${(vacante.coverage * 100).toInt()}%")
                    LinearProgressIndicator(progress = { vacante.coverage }, modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}

@Composable
private fun Chip(label: @Composable () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Box(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
            label()
        }
    }
}
