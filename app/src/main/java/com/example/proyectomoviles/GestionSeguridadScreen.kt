package com.example.proyectomoviles

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
import com.example.proyectomoviles.model.SecurityAlert
import com.example.proyectomoviles.model.OwaspConfig

// --- Datos Fijos ---
private val sampleAlerts = listOf(
    SecurityAlert(1, "Intento de XSS detectado", "Se bloqueó un script sospechoso en el perfil del usuario 'john.doe'.", true, "2024-05-20 10:30:00"),
    SecurityAlert(2, "Contraseña débil", "El usuario 'jane.smith' está usando una contraseña común.", false, "2024-05-19 15:00:00")
)

private val sampleOwaspConfigs = listOf(
    OwaspConfig(1, "A01: Broken Access Control", "Asegurar que los usuarios solo puedan acceder a los recursos que les corresponden.", true),
    OwaspConfig(2, "A02: Cryptographic Failures", "Proteger los datos en tránsito y en reposo con cifrado fuerte.", true),
    OwaspConfig(3, "A03: Injection", "Prevenir ataques de inyección (SQL, NoSQL, etc.) validando y escapando las entradas.", false)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionSeguridadScreen(onNavigateBack: () -> Unit) {

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Bar
        item {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") }
                    Icon(Icons.Default.Shield, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Gestión de seguridad", fontWeight = FontWeight.Bold)
                        Text("Sistema de autenticación", style = MaterialTheme.typography.bodySmall)
                    }
                }
                IconButton(onClick = {}) { Icon(Icons.Default.Lock, "Lock") }
            }
        }
        
        // Panel de admin
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFE3DFFF))) {
                 Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("Panel de administrador", fontWeight = FontWeight.Bold)
                        Text("admin@empresa.com")
                    }
                    AssistChip(onClick = {}, label = {Text("Admin")})
                }
            }
        }
        
        // Stats error message
        item {
            Text("No se pudieron cargar estadísticas.", color = Color.Gray)
        }

        // Alertas de seguridad
        item {
            Text("Alertas de seguridad", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp))
        }
        
        items(sampleAlerts) { alert ->
            SecurityAlertItem(alert)
        }

        // Historial de accesos
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
                    Text("Sin registros recientes.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }
        }

        // OWASP Configuration
        item {
            Text("Configuración OWASP", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp))
        }
        
        items(sampleOwaspConfigs) { config ->
            OwaspConfigItem(config = config)
        }
    }
}

@Composable
private fun SecurityAlertItem(alert: SecurityAlert) {
    val color = if (alert.isCritical) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.tertiaryContainer
    val onColor = if (alert.isCritical) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onTertiaryContainer

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(if (alert.isCritical) Icons.Default.Warning else Icons.Default.Info, contentDescription = null, tint = onColor)
                Spacer(modifier = Modifier.width(8.dp))
                Text(alert.title, fontWeight = FontWeight.Bold, color = onColor)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(alert.description, style = MaterialTheme.typography.bodyMedium, color = onColor)
            Text(alert.timestamp, style = MaterialTheme.typography.bodySmall, color = onColor.copy(alpha = 0.7f))
        }
    }
}

@Composable
private fun OwaspConfigItem(config: OwaspConfig) {
    var isEnabled by remember { mutableStateOf(config.isEnabled) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(Modifier.weight(1f)) {
                Text(config.controlName, fontWeight = FontWeight.Bold)
                Text(config.description, style = MaterialTheme.typography.bodySmall)
            }
            Switch(
                checked = isEnabled,
                onCheckedChange = { isEnabled = it },
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}
