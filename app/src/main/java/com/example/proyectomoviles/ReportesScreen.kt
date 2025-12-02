package com.example.proyectomoviles

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectomoviles.model.Department
import com.example.proyectomoviles.viewmodel.ReportesViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportesScreen(
    viewModel: ReportesViewModel = viewModel()
) {
    val metric by viewModel.vacancyCoverageMetric.collectAsState()
    val departments by viewModel.departments.collectAsState()
    val selectedDepartment by viewModel.selectedDepartment.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDateType by remember { mutableStateOf<String?>(null) }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = { 
                TextButton(onClick = { showDatePicker = false }) { Text("OK") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Filtros de análisis", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Rango de fechas", style = MaterialTheme.typography.bodySmall)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        DateTextField(label = "Desde", onClick = { 
                            selectedDateType = "Desde"
                            showDatePicker = true 
                        }, modifier = Modifier.weight(1f))
                        DateTextField(label = "Hasta", onClick = { 
                            selectedDateType = "Hasta"
                            showDatePicker = true 
                        }, modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        DepartmentDropdown(departments, selectedDepartment, viewModel::onDepartmentSelected, Modifier.weight(1f))
                        OutlinedTextField(value = "Todas", onValueChange = {}, label = { Text("Tipo") }, modifier = Modifier.weight(1f), enabled = false)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.cargarMetricas() }, modifier = Modifier.fillMaxWidth(), enabled = !isLoading) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                        } else {
                            Text("Actualizar reportes")
                        }
                    }
                }
            }
        }

        if (isLoading) {
            item {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }

        error?.let {
            item {
                Text("Error al cargar reportes: $it", color = Color.Red)
            }
        }

        item {
            metric?.let { m ->
                KpiCard(
                    title = m.name,
                    value = "${m.value.toInt()}%",
                    trend = "${m.closedVacancies} de ${m.totalVacancies} vacantes",
                    backgroundColor = if (m.value >= 75f) Color(0xFFE3F3E8) else Color(0xFFFFEBEE),
                    trendColor = if (m.value >= 75f) Color(0xFF3E8E41) else Color.Red
                )
            }
        }

        item {
            metric?.let { m ->
                val openVacancies = m.totalVacancies - m.closedVacancies
                Card(modifier = Modifier.fillMaxWidth()) {
                    VacancyPieChart(
                        closed = m.closedVacancies,
                        open = openVacancies,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun DateTextField(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val interactionSource = remember { MutableInteractionSource() }
    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collectLatest {
            if (it is PressInteraction.Release) {
                onClick()
            }
        }
    }
    OutlinedTextField(
        value = "",
        onValueChange = {}, 
        readOnly = true,
        label = { Text(label) },
        leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
        modifier = modifier,
        interactionSource = interactionSource
    )
}

@Composable
private fun DepartmentDropdown(departments: List<Department>, selected: Department?, onSelected: (Department?) -> Unit, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collectLatest {
            if (it is PressInteraction.Release) {
                expanded = !expanded
            }
        }
    }

    Box(modifier = modifier) {
        OutlinedTextField(
            value = selected?.name ?: "Todas",
            onValueChange = {},
            readOnly = true,
            label = { Text("Área") },
            trailingIcon = { Icon(Icons.Default.ArrowDropDown, "Dropdown") },
            modifier = Modifier.fillMaxWidth(),
            interactionSource = interactionSource
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.fillMaxWidth()) {
            DropdownMenuItem(text = { Text("Todas") }, onClick = { onSelected(null); expanded = false })
            departments.forEach { department ->
                DropdownMenuItem(text = { Text(department.name) }, onClick = { onSelected(department); expanded = false })
            }
        }
    }
}

@Composable
private fun KpiCard(title: String, value: String, trend: String, backgroundColor: Color, trendColor: Color, valueColor: Color = Color.Black) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(title, style = MaterialTheme.typography.bodyMedium)
                Text(trend, color = trendColor, style = MaterialTheme.typography.bodySmall)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = valueColor)
        }
    }
}

@Composable
private fun VacancyPieChart(closed: Int, open: Int, modifier: Modifier = Modifier) {
    val total = closed + open
    if (total == 0) {
        Box(modifier = modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
            Text("No hay datos de vacantes para mostrar.")
        }
        return
    }

    val closedColor = Color(0xFF4CAF50)
    val openColor = Color(0xFFE0E0E0)

    Column(modifier = modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Distribución de Vacantes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Box(modifier = Modifier.size(150.dp), contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                var startAngle = -90f
                val angles = listOf(closed.toFloat(), open.toFloat())
                val colors = listOf(closedColor, openColor)
                angles.forEachIndexed { index, angle ->
                    val sweep = (angle / total) * 360f
                    drawArc(color = colors[index], startAngle = startAngle, sweepAngle = sweep, useCenter = true)
                    startAngle += sweep
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            LegendItem(color = closedColor, text = "Cubiertas ($closed)")
            Spacer(modifier = Modifier.width(16.dp))
            LegendItem(color = openColor, text = "Abiertas ($open)")
        }
    }
}

@Composable
private fun LegendItem(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(12.dp).background(color, shape = CircleShape))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}