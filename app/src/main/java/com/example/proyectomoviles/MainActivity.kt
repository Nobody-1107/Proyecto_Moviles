package com.example.proyectomoviles

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectomoviles.ui.theme.ProyectoMovilesTheme
import kotlinx.coroutines.launch

// --- IMPORTS DE RED ---
import com.example.proyectomoviles.network.RetrofitClient
import com.example.proyectomoviles.network.ApiService
import com.example.proyectomoviles.model.LoginRequest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProyectoMovilesTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LoginScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun LoginScreen(modifier: Modifier = Modifier) {
    // Variables de estado UI
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var rememberSession by rememberSaveable { mutableStateOf(false) }

    // Variables de Lógica de Red
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    // Creamos el servicio usando tu RetrofitClient existente
    val apiService = remember { RetrofitClient.instance.create(ApiService::class.java) }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // --- Logos TATA y ESAN ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.tata_logo),
                contentDescription = "Logo de TATA",
                modifier = Modifier.height(50.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.esan_logo),
                contentDescription = "Logo de ESAN",
                modifier = Modifier.height(50.dp)
            )
        }
        Spacer(modifier = Modifier.height(32.dp))

        // --- Títulos ---
        Text(
            text = "Sistema de Gestión de Talento",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Inicia sesión para continuar",
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))

        // --- Campos de texto ---
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo corporativo") },
            placeholder = { Text("usuario@empresa.com") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            placeholder = { Text("••••••••") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (passwordVisible)
                    Icons.Filled.Visibility
                else Icons.Filled.VisibilityOff
                val description = if (passwordVisible) "Hide password" else "Show password"

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, description)
                }
            }
        )
        Spacer(modifier = Modifier.height(16.dp))

        // --- Checkbox y Botón ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = rememberSession,
                onCheckedChange = { rememberSession = it }
            )
            Text(text = "Recordar sesión")
        }
        Spacer(modifier = Modifier.height(16.dp))

        // --- BOTÓN CON LÓGICA DE BACKEND ---
        Button(
            onClick = {
                scope.launch {
                    isLoading = true
                    try {
                        // 1. Llamada Real al Backend
                        val request = LoginRequest(email, password)
                        val profile = apiService.login(request)

                        // --- NUEVO: GUARDAR SESIÓN EN PREFERENCIAS ---
                        val sharedPref = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
                        with(sharedPref.edit()) {
                            putString("USER_ID", profile.id)
                            putString("USER_NAME", profile.fullName) // Aquí guardamos "Boris Fernandez"
                            putString("USER_ROLE", profile.role)
                            putString("USER_POSITION", profile.position)
                            putBoolean("IS_LOGGED_IN", true)
                            apply()
                        }
                        // ---------------------------------------------

                        // 2. Lógica de Redirección según Rol (Normalizamos a minúsculas)
                        val role = profile.role.lowercase().trim()

                        when {
                            role.contains("colaborador") || role.contains("collaborator") -> {
                                Toast.makeText(context, "Bienvenido ${profile.fullName}", Toast.LENGTH_SHORT).show()
                                context.startActivity(Intent(context, DashboardColaboradorActivity::class.java))
                            }
                            role.contains("lider") || role.contains("leader") -> {
                                Toast.makeText(context, "Bienvenido Líder ${profile.fullName}", Toast.LENGTH_SHORT).show()
                                val intent = Intent(context, LiderDashboardActivity::class.java)
                                intent.putExtra("USER_ROLE", "ROLE_LIDER")
                                context.startActivity(intent)
                            }
                            role.contains("admin") -> {
                                Toast.makeText(context, "Bienvenido Admin ${profile.fullName}", Toast.LENGTH_SHORT).show()
                                val intent = Intent(context, LiderDashboardActivity::class.java)
                                intent.putExtra("USER_ROLE", "ROLE_ADMIN")
                                context.startActivity(intent)
                            }
                            else -> {
                                Toast.makeText(context, "Rol no reconocido: $role", Toast.LENGTH_SHORT).show()
                            }
                        }

                    } catch (e: Exception) {
                        // Manejo de errores
                        val msg = if (e.message?.contains("401") == true) "Correo o contraseña incorrectos" else "Error: ${e.message}"
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                    } finally {
                        isLoading = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0073E6)),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("Iniciar sesión", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        ClickableText(
            text = AnnotatedString("¿Olvidaste tu contraseña?"),
            onClick = {},
            style = TextStyle(color = Color(0xFF0073E6), textAlign = TextAlign.Center)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- Credenciales de prueba (Solo visual) ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF0F4F8), shape = RoundedCornerShape(8.dp))
                .border(1.dp, Color.LightGray, shape = RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            Text("Credenciales de prueba:", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("james.smith@empresa.com / Demo2024! (Colaborador)")
            Text("lider@empresa.com / Lider123! (Gerente)")
            Text("admin@empresa.com / Admin123! (Admin)")
        }

        Spacer(modifier = Modifier.weight(1f))

        // --- Footer ---
        Text(
            text = "Sistema Inteligente de Gestión de Talento Interno",
            fontSize = 12.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        Text(
            text = "TCS/ESAN",
            fontSize = 12.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    ProyectoMovilesTheme {
        LoginScreen()
    }
}