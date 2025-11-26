package com.example.proyectomoviles

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
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
import com.example.proyectomoviles.viewmodel.ReportesViewModel
import com.example.proyectomoviles.model.SlaMetric

@Composable
fun ReportesScreen(
    viewModel: ReportesViewModel = viewModel()
) {
    val metrics by viewModel.slaMetrics.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarMetricas()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Filtros de análisis
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Filtros de análisis", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text("Rango de fechas", style = MaterialTheme.typography.bodySmall)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = "Desde",
                            onValueChange = {},
                            leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = "Hasta",
                            onValueChange = {},
                            leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = "Todas", onValueChange = {}, label = { Text("Área") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = "Todas", onValueChange = {}, label = { Text("Tipo") }, modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.cargarMetricas() }, modifier = Modifier.fillMaxWidth()) {
                        Text("Actualizar reportes")
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
        
        if (error != null) {
            item {
                Text("Error al cargar reportes: $error", color = Color.Red)
            }
        }

        // Métricas SLA dinámicas
        if (!isLoading && metrics.isNotEmpty()) {
             items(metrics) { metric ->
                 // Simulamos un valor actual para el demo, ya que el backend solo da el target
                 // En un caso real, el backend devolvería "current_value" también.
                 val simulatedCurrent = (metric.targetPercentage * (0.8 + Math.random() * 0.4)).coerceAtMost(100.0)
                 val isPositive = simulatedCurrent >= metric.targetPercentage
                 
                 KpiCard(
                     title = metric.name,
                     value = "${simulatedCurrent.toInt()}%",
                     trend = "Target: ${metric.targetPercentage}%",
                     backgroundColor = if (isPositive) Color(0xFFE3F3E8) else Color(0xFFFFEBEE),
                     trendColor = if (isPositive) Color(0xFF3E8E41) else Color.Red,
                     valueColor = Color.Black
                 )
             }
        } else if (!isLoading && error == null) {
             // Fallback si no hay métricas en el backend
             item { Text("No hay métricas SLA definidas.") }
        }
        
        // Gráficos (placeholders)
        item {
            Card(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Gráfico de Rendimiento (Próximamente)")
                }
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
