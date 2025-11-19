package com.example.proyectomoviles

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

data class Habilidad(val nombre: String, val dominio: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarColaboradorScreen(onNavigateUp: () -> Unit) {
    var nombreCompleto by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var rol by remember { mutableStateOf("") }
    var disponibleParaMovilidad by remember { mutableStateOf(false) }

    var departamentoExpanded by remember { mutableStateOf(false) }
    val departamentos = listOf(
        "IT/Tecnología",
        "Marketing",
        "Analytics",
        "Producto",
        "Diseño",
        "Ventas",
        "Recursos Humanos",
        "Finanzas",
        "Operaciones"
    )
    var departamentoSeleccionado by remember { mutableStateOf(departamentos[0]) }

    var habilidadNombre by remember { mutableStateOf("") }
    var dominioSliderValue by remember { mutableStateOf(50f) }
    val habilidadesAnadidas = remember { mutableStateListOf<Habilidad>() }

    var certificacionNombre by remember { mutableStateOf("") }
    val certificacionesAnadidas = remember { mutableStateListOf<String>() }

    val context = LocalContext.current


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Agregar Colaborador", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Registra un nuevo colaborador en el sistema.", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Información personal", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(16.dp))

                // Nombre Completo
                OutlinedTextField(
                    value = nombreCompleto,
                    onValueChange = { nombreCompleto = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Nombre completo") },
                    placeholder = { Text("Ej: Juan Pérez") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Correo electrónico
                OutlinedTextField(
                    value = correo,
                    onValueChange = { correo = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Correo electrónico corporativo") },
                    placeholder = { Text("Ej: juan.perez@empresa.com") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Número de teléfono
                OutlinedTextField(
                    value = telefono,
                    onValueChange = { telefono = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Número de teléfono") },
                    placeholder = { Text("Ej: 987654321") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Rol/Cargo
                OutlinedTextField(
                    value = rol,
                    onValueChange = { rol = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Rol/Cargo") },
                    placeholder = { Text("Ej: Desarrollador Android") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Selector de Departamento
                ExposedDropdownMenuBox(
                    expanded = departamentoExpanded,
                    onExpandedChange = { departamentoExpanded = !departamentoExpanded }
                ) {
                    OutlinedTextField(
                        value = departamentoSeleccionado,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Departamento") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = departamentoExpanded)
                        },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = departamentoExpanded,
                        onDismissRequest = { departamentoExpanded = false }
                    ) {
                        departamentos.forEach { depto ->
                            DropdownMenuItem(
                                text = { Text(depto) },
                                onClick = {
                                    departamentoSeleccionado = depto
                                    departamentoExpanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Switch Movilidad Interna
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("¿Disponible para movilidad interna?", style = MaterialTheme.typography.bodyLarge)
                    Switch(
                        checked = disponibleParaMovilidad,
                        onCheckedChange = { disponibleParaMovilidad = it }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Sección de Habilidades
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Habilidades *", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = habilidadNombre,
                    onValueChange = { habilidadNombre = it },
                    label = { Text("Nombre de la habilidad") },
                    placeholder = { Text("Ej: React, Python, Project Management") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Nivel de dominio: ${dominioSliderValue.toInt()}%",
                    style = MaterialTheme.typography.bodyLarge
                )
                Slider(
                    value = dominioSliderValue,
                    onValueChange = { dominioSliderValue = it },
                    valueRange = 0f..100f
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Principiante", style = MaterialTheme.typography.bodySmall)
                    Text("Intermedio", style = MaterialTheme.typography.bodySmall)
                    Text("Avanzado", style = MaterialTheme.typography.bodySmall)
                    Text("Experto", style = MaterialTheme.typography.bodySmall)
                }

                // Lista de habilidades añadidas
                if (habilidadesAnadidas.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Habilidades añadidas", style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    habilidadesAnadidas.forEach { habilidad ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${habilidad.nombre} - ${habilidad.dominio}%",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            IconButton(onClick = { habilidadesAnadidas.remove(habilidad) }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Eliminar habilidad"
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (habilidadNombre.isNotBlank()) {
                            habilidadesAnadidas.add(Habilidad(habilidadNombre, dominioSliderValue.toInt()))
                            habilidadNombre = ""
                            dominioSliderValue = 50f
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = "Añadir habilidad"
                    )
                    Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                    Text("Añadir habilidad")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Sección de Certificaciones
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Certificaciones (Opcional)", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = certificacionNombre,
                    onValueChange = { certificacionNombre = it },
                    label = { Text("Nombre de la certificación") },
                    placeholder = { Text("Ej: AWS Certified Developer, PMP, Google Analytics") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Lista de certificaciones añadidas
                if (certificacionesAnadidas.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Certificaciones añadidas", style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    certificacionesAnadidas.forEach { certificacion ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = certificacion,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            IconButton(onClick = { certificacionesAnadidas.remove(certificacion) }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Eliminar certificación"
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (certificacionNombre.isNotBlank()) {
                            certificacionesAnadidas.add(certificacionNombre)
                            certificacionNombre = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = "Añadir certificación"
                    )
                    Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                    Text("Añadir certificación")
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = onNavigateUp,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancelar")
            }
            Button(
                onClick = {
                    Toast.makeText(context, "Colaborador $nombreCompleto registrado satisfactoriamente", Toast.LENGTH_LONG).show()
                    onNavigateUp()
                },
                modifier = Modifier.weight(1f),
                enabled = nombreCompleto.isNotBlank()
            ) {
                Text("Agregar colaborador *")
            }
        }
    }
}
