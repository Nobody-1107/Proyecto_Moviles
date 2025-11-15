package com.example.proyectomoviles

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.proyectomoviles.ui.theme.ProyectoMovilesTheme

class DashboardColaboradorActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProyectoMovilesTheme {
                val navController = rememberNavController()
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Gestión de Talento") },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color.Transparent,
                                titleContentColor = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    },
                    bottomBar = {
                        var selectedItem by remember { mutableStateOf("perfil") }
                        NavigationBar {
                            NavigationBarItem(
                                icon = { Icon(Icons.Filled.Person, contentDescription = "Perfil") },
                                label = { Text("Perfil") },
                                selected = selectedItem == "perfil",
                                onClick = { 
                                    selectedItem = "perfil"
                                    navController.navigate("perfil") 
                                }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Filled.Group, contentDescription = "Mi Equipo") },
                                label = { Text("Mi Equipo") },
                                selected = selectedItem == "mi_equipo",
                                onClick = { 
                                    selectedItem = "mi_equipo"
                                    navController.navigate("mi_equipo")
                                }
                            )
                        }
                    },
                    floatingActionButton = {
                        FloatingActionButton(onClick = { /* TODO: Open chat */ }) {
                            Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "Chat")
                        }
                    }
                ) { innerPadding ->
                    NavHost(navController = navController, startDestination = "perfil", modifier = Modifier.padding(innerPadding)) {
                        composable("perfil") { 
                            PerfilColaboradorScreen(onLogout = { 
                                finish() 
                            })
                        }
                        composable("mi_equipo") { MiEquipoColaboradorScreen() }
                    }
                }
            }
        }
    }
}
