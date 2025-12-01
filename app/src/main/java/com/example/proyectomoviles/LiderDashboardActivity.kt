package com.example.proyectomoviles

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.proyectomoviles.ui.theme.ProyectoMovilesTheme

class LiderDashboardActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userRole = intent.getStringExtra("USER_ROLE") ?: "ROLE_LIDER"

        setContent {
            ProyectoMovilesTheme {
                val navController = rememberNavController()
                var selectedItem by remember { mutableStateOf("demanda") }

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
                        NavigationBar {
                            NavigationBarItem(
                                icon = { Icon(Icons.Filled.Work, contentDescription = "Demanda") },
                                label = { Text("Demanda") },
                                selected = selectedItem == "demanda",
                                onClick = {
                                    selectedItem = "demanda"
                                    navController.navigate("demanda")
                                }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Filled.Group, contentDescription = "Gestión") },
                                label = { Text("Gestión") },
                                selected = selectedItem == "gestion",
                                onClick = {
                                    selectedItem = "gestion"
                                    navController.navigate("gestion")
                                }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Filled.BarChart, contentDescription = "Reportes") },
                                label = { Text("Reportes") },
                                selected = selectedItem == "reportes",
                                onClick = {
                                    selectedItem = "reportes"
                                    navController.navigate("reportes")
                                }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Filled.Person, contentDescription = "Perfil") },
                                label = { Text("Perfil") },
                                selected = selectedItem == "perfil",
                                onClick = {
                                    selectedItem = "perfil"
                                    navController.navigate("perfil")
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
                    NavHost(navController = navController, startDestination = "demanda", Modifier.padding(innerPadding)) {
                        composable("demanda") {
                            DemandaLiderScreen(
                                onNavigateToCreateVacante = { navController.navigate("formulario_vacante") },
                                onNavigateToDetail = { vacancyId -> navController.navigate("vacancyDetail/$vacancyId") },
                                onNavigateToEditProfile = { profileId -> navController.navigate("edit_collaborator/$profileId") }
                            )
                        }
                        composable(
                            "vacancyDetail/{vacancyId}",
                            arguments = listOf(navArgument("vacancyId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val vacancyId = backStackEntry.arguments?.getInt("vacancyId") ?: 0
                            VacancyDetailScreen(vacancyId = vacancyId, navController = navController)
                        }
                        composable(
                            "edit_vacancy/{vacancyId}",
                            arguments = listOf(navArgument("vacancyId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val vacancyId = backStackEntry.arguments?.getInt("vacancyId") ?: 0
                            EditVacancyScreen(vacancyId = vacancyId, onNavigateBack = { navController.popBackStack() })
                        }
                        composable("formulario_vacante") { FormularioVacanteScreen(onNavigateBack = { navController.popBackStack() }) }
                        
                        composable("gestion") { 
                            GestionLiderScreen(
                                onNavigateToRegisterCollaborator = { navController.navigate("formulario_colaborador") },
                                onNavigateToCollaboratorDetail = { profileId ->
                                    navController.navigate("collaboratorDetail/$profileId")
                                },
                                onNavigateToSugerencias = { navController.navigate("sugerencias") } // <-- INSTRUCCIÓN AÑADIDA
                            )
                        }

                        composable(
                            "collaboratorDetail/{profileId}",
                            arguments = listOf(navArgument("profileId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val profileId = backStackEntry.arguments?.getString("profileId") ?: ""
                            CollaboratorDetailScreen(
                                profileId = profileId,
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToEdit = { navController.navigate("edit_collaborator/$profileId") }
                            )
                        }

                        composable(
                            "edit_collaborator/{profileId}",
                            arguments = listOf(navArgument("profileId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val profileId = backStackEntry.arguments?.getString("profileId") ?: ""
                            EditCollaboratorScreen(profileId = profileId, onNavigateBack = { navController.popBackStack() })
                        }

                        composable("formulario_colaborador") { FormularioColaboradorScreen(onNavigateBack = { navController.popBackStack() }) }
                        composable("reportes") { ReportesScreen() }
                        composable("perfil") {
                            if (userRole == "ROLE_ADMIN") {
                                AdminPerfilScreen(
                                    onLogout = { finish() },
                                    onNavigateToUpdateSkills = { navController.navigate("actualizar_habilidades") },
                                    onNavigateToGestionSeguridad = { navController.navigate("gestion_seguridad") },
                                    onNavigateToRegisterCollaborator = { navController.navigate("formulario_colaborador") }
                                )
                            } else {
                                LiderPerfilScreen(onLogout = { finish() }, onNavigateToUpdateSkills = { navController.navigate("actualizar_habilidades") })
                            }
                        }
                        composable("actualizar_habilidades") { ActualizarHabilidadesScreen(onNavigateBack = { navController.popBackStack() }) }
                        composable("gestion_seguridad") { GestionSeguridadScreen(onNavigateBack = { navController.popBackStack() }) }

                        composable("sugerencias") {
                            SugerenciasScreen(
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToEditProfile = { profileId ->
                                    navController.navigate("edit_collaborator/$profileId")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}