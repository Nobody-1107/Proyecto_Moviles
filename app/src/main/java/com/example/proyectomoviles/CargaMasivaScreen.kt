package com.example.proyectomoviles

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CargaMasivaScreen(onNavigateBack: () -> Unit) {
    var selectedFileName by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Carga Masiva de Colaboradores") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.UploadFile,
                contentDescription = "Upload Icon",
                modifier = Modifier.size(100.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Sube un archivo para registrar colaboradores en bloque.",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Formatos soportados: CSV, Excel (.xlsx)",
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = { selectedFileName = "colaboradores.csv" }) {
                Text("Seleccionar Archivo")
            }

            if (selectedFileName != null) {
                Text(
                    text = "Archivo seleccionado: $selectedFileName",
                    modifier = Modifier.padding(top = 16.dp),
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = { /* No-op */ },
                enabled = selectedFileName != null
            ) {
                Text("Iniciar Carga")
            }
        }
    }
}
