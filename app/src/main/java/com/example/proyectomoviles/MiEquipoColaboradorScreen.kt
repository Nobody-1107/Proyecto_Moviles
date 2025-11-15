package com.example.proyectomoviles

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class TeamMember(
    val name: String,
    val title: String,
    val skills: List<Pair<String, Float>>
)

@Composable
fun MiEquipoColaboradorScreen() {
    var showDialog by remember { mutableStateOf(false) }

    val team = listOf(
        TeamMember("Michael Johnson", "Frontend Developer", listOf("React" to 0.8f, "TypeScript" to 0.7f)),
        TeamMember("Sarah Williams", "Backend Developer", listOf("Node.js" to 0.9f, "Python" to 0.6f)),
        TeamMember("James Smith", "UI/UX Designer", listOf("Figma" to 0.95f, "Adobe XD" to 0.85f)),
        TeamMember("Emily Davis", "QA Tester", listOf("Selenium" to 0.75f, "Jira" to 0.8f))
    )

    if (showDialog) {
        SugerirActualizacionDialog(
            onDismissRequest = { showDialog = false },
            onEnviarSugerencia = { suggestion ->
                // TODO: Handle suggestion
                println("Suggestion: $suggestion")
            }
        )
    }

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        item {
            Button(
                onClick = { showDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sugerir actualización")
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        items(team) { member ->
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(member.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(member.title, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    member.skills.forEach { (skill, progress) ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(skill)
                            Text("${(progress * 100).toInt()}% ")
                        }
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxWidth().height(8.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }
    }
}
