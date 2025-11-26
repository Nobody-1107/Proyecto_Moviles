package com.example.proyectomoviles

import android.app.DatePickerDialog
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectomoviles.viewmodel.DemandaLiderViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioVacanteScreen(
    onNavigateBack: () -> Unit,
    viewModel: DemandaLiderViewModel = viewModel()
) {
    var jobTitle by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("") }
    var urgency by remember { mutableStateOf("Media") }
    var startDate by remember { mutableStateOf("") }
    var skillName by remember { mutableStateOf("") }
    var skillLevel by remember { mutableStateOf("Intermedio") }
    val skillLevels = listOf("Básico", "Intermedio", "Avanzado", "Experto")
    var isDropdownExpanded by remember { mutableStateOf(false) }

    val isLoading by viewModel.isLoading.collectAsState()
    val createSuccess by viewModel.createSuccess.collectAsState()
    val error by viewModel.error.collectAsState()
    val context = LocalContext.current

    // DatePickerDialog Logic
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDayOfMonth: Int ->
            startDate = "$selectedDayOfMonth/${selectedMonth + 1}/$selectedYear"
        }, year, month, day
    )

    LaunchedEffect(createSuccess) {
        if (createSuccess) {
            Toast.makeText(context, "Vacante creada exitosamente", Toast.LENGTH_LONG).show()
            viewModel.resetCreateSuccess()
            onNavigateBack()
        }
    }

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                }
                Text("Registrar Nueva Vacante", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        if (error != null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Error al guardar", fontWeight = FontWeight.Bold, color = Color.Red)
                        Text("Detalle: $error", style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Posibles causas: ID de departamento no existe o usuario creador no válido.", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Información General", fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = jobTitle,
                        onValueChange = { jobTitle = it },
                        label = { Text("Nombre del perfil o puesto") },
                        supportingText = { Text("Ej: Desarrollador Java Senior") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = department,
                        onValueChange = { department = it },
                        label = { Text("ID de Departamento") },
                        placeholder = { Text("Ej: 1") },
                        supportingText = { Text("Debe ser un número entero válido existente en la BD.") },
                        isError = department.isNotEmpty() && department.toIntOrNull() == null,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Nivel de Urgencia", fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                        ToggleButton(text = "Alta", selected = urgency == "Alta") { urgency = "Alta" }
                        ToggleButton(text = "Media", selected = urgency == "Media") { urgency = "Media" }
                        ToggleButton(text = "Baja", selected = urgency == "Baja") { urgency = "Baja" }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // Fecha (Opcional y clickeable)
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Fecha de Inicio (Opcional)", fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = startDate,
                        onValueChange = {}, 
                        label = { Text("Selecciona una fecha") },
                        leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                        readOnly = true,
                        enabled = true,
                        supportingText = { Text("Toca para abrir el calendario") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { datePickerDialog.show() } 
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // Sección Skills (Placeholder visual)
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Skills Requeridos (Visual)", fontWeight = FontWeight.Bold)
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = skillName,
                            onValueChange = { skillName = it },
                            label = { Text("Ej: React") },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(modifier = Modifier.width(140.dp)) {
                            ExposedDropdownMenuBox(
                                expanded = isDropdownExpanded,
                                onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }
                            ) {
                                OutlinedTextField(
                                    value = skillLevel,
                                    onValueChange = {}, 
                                    readOnly = true,
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                                    modifier = Modifier.menuAnchor()
                                )
                                ExposedDropdownMenu(
                                    expanded = isDropdownExpanded,
                                    onDismissRequest = { isDropdownExpanded = false }
                                ) {
                                    skillLevels.forEach { level ->
                                        DropdownMenuItem(
                                            text = { Text(level) },
                                            onClick = {
                                                skillLevel = level
                                                isDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = { /* TODO: Add skill logic */ }) {
                            Icon(Icons.Default.Add, contentDescription = "Añadir Skill")
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        item {
            Column {
                Button(
                    onClick = { 
                        if (jobTitle.isNotBlank()) {
                            val deptId = department.toIntOrNull()
                            
                            if (deptId == null && department.isNotBlank()) {
                                Toast.makeText(context, "El ID de departamento debe ser numérico", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            val finalDescription = buildString {
                                append("Urgencia: $urgency")
                                if (startDate.isNotBlank()) append(" | Fecha Inicio: $startDate")
                            }

                            viewModel.crearVacante(
                                title = jobTitle,
                                departmentId = deptId, // Enviará null si está vacío, o el número
                                description = finalDescription,
                                status = "Abierta"
                            )
                        } else {
                            Toast.makeText(context, "Ingrese un título", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Guardando...")
                    } else {
                        Text("Guardar Vacante")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    Text("Cancelar")
                }
            }
        }
    }
}

@Composable
private fun ToggleButton(text: String, selected: Boolean, onClick: () -> Unit) {
    val colors = if (selected) ButtonDefaults.buttonColors() else ButtonDefaults.outlinedButtonColors()
    Button(onClick = onClick, colors = colors) {
        Text(text)
    }
}
