package com.example.proyectomoviles

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ReportesScreen() {
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
                    Button(onClick = { /* TODO */ }, modifier = Modifier.fillMaxWidth()) {
                        Text("Actualizar reportes")
                    }
                }
            }
        }

        // KPIs
        item {
            KpiCard("Vacantes cubiertas internamente", "68%", "+9.7%", Color(0xFFE3F3E8), Color(0xFF3E8E41))
        }
        item {
            KpiCard("Skills críticos cubiertos", "82%", "+9.3%", Color(0xFFE4F2F8), Color(0xFF0073E6))
        }
        item {
            KpiCard("Brechas detectadas", "14", "-22.2%", Color(0xFFFFF4E5), Color(0xFFFFA000), Color.Red)
        }
        
        // Gráficos (placeholders)
        item {
            Card(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Placeholder para Gráfico de Barras")
                }
            }
        }
        item {
            Card(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                 Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Placeholder para Gráfico de Pastel")
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
