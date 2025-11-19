package com.example.proyectomoviles

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GestionLiderScreen(onNavigateToUpdateSkills: () -> Unit, onNavigateToAgregarColaborador: () -> Unit) {
    val team = listOf(
        TeamMember("Michael Johnson", "Frontend Developer", listOf("React" to 0.85f, "TypeScript" to 0.75f, "UI Design" to 0.6f)),
        TeamMember("Sarah Williams", "Backend Developer", listOf("Node.js" to 0.9f, "Python" to 0.6f)),
    )
    var showOtherOptions by remember { mutableStateOf(false) }

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Gestión de Habilidades", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onNavigateToAgregarColaborador,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF003366))
                        ) {
                            Text("Agregar colaborador")
                        }
                        Button(
                            onClick = { /* TODO: Carga masiva */ },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8623B5))
                        ) {
                            Text("Carga masiva")
                        }

                        Box {
                            Button(
                                onClick = { showOtherOptions = !showOtherOptions },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B))
                            ) {
                                Text("Otros")
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Desplegar otras opciones"
                                )
                            }
                            DropdownMenu(
                                expanded = showOtherOptions,
                                onDismissRequest = { showOtherOptions = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Recomendaciones de capacitación") },
                                    onClick = {
                                        /* TODO */
                                        showOtherOptions = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Evaluar habilidades del equipo") },
                                    onClick = {
                                        /* TODO */
                                        showOtherOptions = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Filtrar habilidades por área") },
                                    onClick = {
                                        /* TODO */
                                        showOtherOptions = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Ver indicadores SLA") },
                                    onClick = {
                                        /* TODO */
                                        showOtherOptions = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        items(team) { member ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable { onNavigateToUpdateSkills() }
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(member.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(member.title, color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                        member.skills.forEach { (skill, progress) ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(skill)
                                Text("${(progress * 100).toInt()}% ")
                            }
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Navigate")
                }
            }
        }
    }
}