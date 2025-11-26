package com.example.proyectomoviles

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectomoviles.model.SecurityAlert
import com.example.proyectomoviles.model.AccessLog
import com.example.proyectomoviles.model.OwaspConfig
import com.example.proyectomoviles.viewmodel.GestionSeguridadViewModel

@Composable
fun GestionSeguridadScreen(
    onNavigateBack: () -> Unit,
    viewModel: GestionSeguridadViewModel = viewModel()
) {
    val stats by viewModel.stats.collectAsState()
    val alerts by viewModel.alerts.collectAsState()
    val accessLogs by viewModel.accessLogs.collectAsState()
    val owaspConfig by viewModel.owaspConfig.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarDatosSeguridad()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Top Bar ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                    Icon(Icons.Default.Shield, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Gestión de seguridad", fontWeight = FontWeight.Bold)
                        Text("Sistema de autenticación", style = MaterialTheme.typography.bodySmall)
                    }
                }
                IconButton(onClick = { /* TODO */ }) {
                    Icon(Icons.Default.Lock, contentDescription = "Lock")
                }
            }
        }
        
        if (isLoading && stats == null) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }

        // --- Admin Panel ---
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFE3DFFF))) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Panel de administrador", fontWeight = FontWeight.Bold)
                        Text("admin@empresa.com") // Este email sigue hardcodeado ya que es el usuario logueado local
                    }
                    Chip("Admin", Color(0xFFD0BCFF))
                }
            }
        }
        
        // --- Access stats (Datos reales del backend) ---
        if (stats != null) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F3E8))
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Accesos exitosos")
                            Text(stats!!.successCount.toString(), style = MaterialTheme.typography.headlineMedium)
                        }
                    }
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Intentos fallidos")
                            Text(stats!!.failedCount.toString(), style = MaterialTheme.typography.headlineMedium, color = Color.Red)
                        }
                    }
                }
            }
        } else if (!isLoading) {
            item { Text("No se pudieron cargar estadísticas.", color = Color.Gray) }
        }

        // --- Alertas de seguridad ---
        item {
            Text("Alertas de seguridad", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp))
        }
        
        if (alerts.isEmpty() && !isLoading) {
             item { Text("No hay alertas activas.", style = MaterialTheme.typography.bodySmall) }
        } else {
            items(alerts) { alert ->
                SecurityAlertItem(alert)
            }
        }

        // --- Historial de accesos ---
        item { 
            Card(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text("Historial de accesos", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = "", 
                        onValueChange = {}, 
                        label = { Text("Buscar por usuario, email o IP...") }, 
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    if (accessLogs.isEmpty() && !isLoading) {
                        Text("Sin registros recientes.", style = MaterialTheme.typography.bodySmall)
                    } else {
                        accessLogs.forEach { log ->
                            AccessLogItem(log)
                        }
                    }
                }
            }
        }

        // --- OWASP Configuration ---
        item {
            Text("Configuración OWASP", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp))
        }
        
        if (owaspConfig.isEmpty() && !isLoading) {
            item { Text("No hay configuraciones disponibles.", style = MaterialTheme.typography.bodySmall) }
        } else {
            items(owaspConfig) { config ->
                OwaspConfigItem(
                    config = config, 
                    onToggle = { isEnabled -> 
                        viewModel.actualizarConfiguracionOwasp(config.id, isEnabled) 
                    }
                )
            }
        }

        // --- Cumplimiento OWASP ---
        if (stats != null) {
            item {
                Card(modifier = Modifier.fillMaxWidth(), border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cumplimiento OWASP Top 10")
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            "${stats!!.owaspScore} de ${stats!!.totalControls} controles activos", 
                            color = MaterialTheme.colorScheme.primary, 
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SecurityAlertItem(alert: SecurityAlert) {
    val borderColor = if (alert.isCritical) Color(0xFFD32F2F) else Color(0xFFFFA000)
    val icon = if (alert.isCritical) Icons.Default.WarningAmber else Icons.Default.Info
    
    Card(
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = borderColor)
                Spacer(modifier = Modifier.width(8.dp))
                Text(alert.title, fontWeight = FontWeight.Bold)
            }
            Text("${alert.timestamp} - ${alert.description}", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { /* TODO */ }, colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)) {
                    Text("Bloquear", color = Color.Red)
                }
                Button(onClick = { /* TODO */ }) {
                    Text("Resolver")
                }
            }
        }
    }
}

@Composable
private fun AccessLogItem(log: AccessLog) {
    val borderColor = if(log.isSuccess) Color(0xFF388E3C) else Color(0xFFD32F2F)
    val statusText = if(log.isSuccess) "Éxito" else "Fallido"
    
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), border = BorderStroke(1.dp, borderColor)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(log.userEmail, fontWeight = FontWeight.Bold)
                Chip(statusText, if(log.isSuccess) Color(0xFFC8E6C9) else Color(0xFFFFCDD2))
            }
            Text("Fecha: ${log.timestamp}", style = MaterialTheme.typography.bodySmall)
            Text("IP: ${log.ipAddress}", style = MaterialTheme.typography.bodySmall)
            Text("Dispositivo: ${log.device}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun OwaspConfigItem(config: OwaspConfig, onToggle: (Boolean) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Column(Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(config.controlName, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Switch(
                    checked = config.isEnabled, 
                    onCheckedChange = { onToggle(it) }
                )
            }
            Text(config.description, style = MaterialTheme.typography.bodySmall)
            if (config.isEnabled) {
                Chip("Activo", Color(0xFFC8E6C9))
            } else {
                Chip("Inactivo", Color(0xFFFFCDD2))
            }
        }
    }
}

@Composable
private fun Chip(label: String, color: Color) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = color,
    ) {
        Text(text = label, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
    }
}
