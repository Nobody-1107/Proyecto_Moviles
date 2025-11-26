package com.example.proyectomoviles

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectomoviles.viewmodel.CrearColaboradorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioColaboradorScreen(
    onNavigateBack: () -> Unit,
    viewModel: CrearColaboradorViewModel = viewModel()
) {
    var fullName by remember { mutableStateOf("") }
    var position by remember { mutableStateOf("") }
    var departmentIdInput by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("Colaborador") }
    
    // Opciones de rol simples para el selector
    val roles = listOf("Colaborador", "Lider", "Admin")
    var isRoleExpanded by remember { mutableStateOf(false) }

    val isLoading by viewModel.isLoading.collectAsState()
    val createSuccess by viewModel.createSuccess.collectAsState()
    val error by viewModel.error.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(createSuccess) {
        if (createSuccess) {
            Toast.makeText(context, "Colaborador creado exitosamente", Toast.LENGTH_LONG).show()
            viewModel.resetSuccess()
            onNavigateBack()
        }
    }

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                }
                Text("Registrar Nuevo Colaborador", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        if (error != null) {
            item {
                Text("Error: $error", color = Color.Red, modifier = Modifier.padding(bottom = 8.dp))
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Información Personal", fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Nombre Completo") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = position,
                        onValueChange = { position = it },
                        label = { Text("Cargo / Puesto") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Información Organizacional", fontWeight = FontWeight.Bold)
                    
                    // Departamento (ID manual por ahora)
                    OutlinedTextField(
                        value = departmentIdInput,
                        onValueChange = { departmentIdInput = it },
                        label = { Text("ID de Departamento (Número)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    // Selector de Rol
                    Box(modifier = Modifier.fillMaxWidth()) {
                        ExposedDropdownMenuBox(
                            expanded = isRoleExpanded,
                            onExpandedChange = { isRoleExpanded = !isRoleExpanded }
                        ) {
                            OutlinedTextField(
                                value = role,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Rol en el Sistema") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isRoleExpanded) },
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = isRoleExpanded,
                                onDismissRequest = { isRoleExpanded = false }
                            ) {
                                roles.forEach { selection ->
                                    DropdownMenuItem(
                                        text = { Text(selection) },
                                        onClick = {
                                            role = selection
                                            isRoleExpanded = false
                                        }
                                    )
                                }
                            }
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
                        if (fullName.isNotBlank() && position.isNotBlank()) {
                            val deptId = departmentIdInput.toIntOrNull()
                            viewModel.crearColaborador(
                                fullName = fullName,
                                position = position,
                                departmentId = deptId,
                                role = role
                            )
                        } else {
                            Toast.makeText(context, "Complete nombre y cargo", Toast.LENGTH_SHORT).show()
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
                        Text("Guardar Colaborador")
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
