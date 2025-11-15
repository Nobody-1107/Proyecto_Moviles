package com.example.proyectomoviles

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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

@Composable
fun GestionSeguridadScreen(onNavigateBack: () -> Unit) {
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
                        Text("admin@empresa.com")
                    }
                    Chip("Admin", Color(0xFFD0BCFF))
                }
            }
        }
        
        // --- Access stats ---
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F3E8))
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Accesos exitosos")
                        Text("4", style = MaterialTheme.typography.headlineMedium)
                    }
                }
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Intentos fallidos")
                        Text("3", style = MaterialTheme.typography.headlineMedium, color = Color.Red)
                    }
                }
            }
        }

        // --- Alertas de seguridad ---
        item {
            Text("Alertas de seguridad", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp))
        }
        item {
            SecurityAlertItem("Múltiples intentos de acceso fallidos desde IP 45.33.22.11", "14/11, 21:19 - Usuario: admin", true)
        }
        item {
            SecurityAlertItem("Intento de acceso con credenciales inexistentes", "14/11, 21:14 - Usuario: unknown_user", false)
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
                    AccessLogItem("admin@empresa.com", "14/11, 21:34", "192.168.1.100", "Chrome - Windows", true)
                    AccessLogItem("james.smith@empresa.com", "14/11, 21:24", "192.168.1.101", "Safari - iPhone", true)
                    AccessLogItem("hacker@malicious.com", "14/11, 21:20", "10.0.0.5", "Unknown", false)

                }
            }
        }

        // --- OWASP Configuration ---
        item {
            Text("Configuración OWASP", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp))
        }
        item {
            OwaspConfigItem("Protección contra XSS", "Sanitización de entradas y validación de contenido HTML", true)
        }
        item {
            OwaspConfigItem("Validación SQL Injection", "Prevención de inyección SQL mediante prepared statements", true)
        }
        item {
            OwaspConfigItem("Prevención CSRF", "Tokens anti-CSRF en formularios y peticiones", true)
        }
        item {
            OwaspConfigItem("Autenticación robusta", "JWT con expiración, refresh tokens y blacklist", true)
        }
        item {
            OwaspConfigItem("Rate Limiting", "Límite de peticiones por IP (100/hora)", true)
        }
         item {
            OwaspConfigItem("Security Headers", "HSTS, X-Frame-Options, CSP, X-Content-Type", true)
        }

        // --- Cumplimiento OWASP ---
        item {
            Card(modifier = Modifier.fillMaxWidth(), border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Shield, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cumplimiento OWASP Top 10")
                    Spacer(modifier = Modifier.weight(1f))
                    Text("6 de 6 controles activos", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun SecurityAlertItem(title: String, subtitle: String, isCritical: Boolean) {
    val borderColor = if (isCritical) Color(0xFFD32F2F) else Color(0xFFFFA000)
    val icon = if (isCritical) Icons.Default.WarningAmber else Icons.Default.Info
    Card(border = BorderStroke(1.dp, borderColor)) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = borderColor)
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, fontWeight = FontWeight.Bold)
            }
            Text(subtitle, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { /* TODO */ }, colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)) {
                    Text("Bloquear cuenta", color = Color.Red)
                }
                Button(onClick = { /* TODO */ }) {
                    Text("Resolver")
                }
            }
        }
    }
}

@Composable
private fun AccessLogItem(user: String, date: String, ip: String, device: String, success: Boolean) {
    val borderColor = if(success) Color(0xFF388E3C) else Color(0xFFD32F2F)
    val statusText = if(success) "Éxito" else "Fallido"
    
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), border = BorderStroke(1.dp, borderColor)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(user, fontWeight = FontWeight.Bold)
                Chip(statusText, if(success) Color(0xFFC8E6C9) else Color(0xFFFFCDD2))
            }
            Text("Fecha: $date", style = MaterialTheme.typography.bodySmall)
            Text("IP: $ip", style = MaterialTheme.typography.bodySmall)
            Text("Dispositivo: $device", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun OwaspConfigItem(title: String, description: String, enabled: Boolean) {
    var isChecked by remember { mutableStateOf(enabled) }
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(title, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Switch(checked = isChecked, onCheckedChange = { isChecked = it })
            }
            Text(description, style = MaterialTheme.typography.bodySmall)
            Chip("Activo", Color(0xFFC8E6C9))
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
