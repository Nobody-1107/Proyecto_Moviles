package com.example.proyectomoviles

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun SugerirActualizacionDialog(
    onDismissRequest: () -> Unit,
    onEnviarSugerencia: (String) -> Unit
) {
    var suggestionText by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color(0xFF0073E6)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Sugerir actualización de perfil",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onDismissRequest) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }
                Text(
                    text = "Envía una solicitud al equipo de RRHH para actualizar tu perfil profesional",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 32.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Input field
                Text(
                    "Describe los cambios que deseas realizar en tu perfil",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = suggestionText,
                    onValueChange = { suggestionText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    placeholder = {
                        Text(
                            "Hola, quisiera sugerir una actualización en mi perfil profesional. " +
                                    "He completado recientemente nuevas certificaciones y he adquirido " +
                                    "experiencia en tecnologías emergentes que me gustaría reflejar en mi " +
                                    "perfil para estar disponible para nuevas oportunidades internas."
                        )
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tu sugerencia será revisada por el equipo de Recursos Humanos para actualizar tu perfil.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismissRequest) {
                        Text("Cancelar")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onEnviarSugerencia(suggestionText)
                            onDismissRequest()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0073E6))
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Enviar sugerencia")
                    }
                }
            }
        }
    }
}
