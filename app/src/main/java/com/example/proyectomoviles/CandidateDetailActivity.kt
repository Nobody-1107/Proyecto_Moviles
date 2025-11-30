package com.example.proyectomoviles

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.proyectomoviles.ui.theme.ProyectoMovilesTheme

class CandidateDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val vacancyId = intent.getIntExtra("VACANCY_ID", -1)
        val candidateId = intent.getStringExtra("CANDIDATE_ID")

        // Basic validation
        if (vacancyId == -1 || candidateId == null) {
            // Handle error, maybe finish the activity
            finish()
            return
        }

        setContent {
            ProyectoMovilesTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CandidateDetailScreen(
                        vacancyId = vacancyId,
                        candidateId = candidateId,
                        onNavigateBack = { finish() } // Simply finish the activity to go back
                    )
                }
            }
        }
    }
}