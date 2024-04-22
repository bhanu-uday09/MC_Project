package com.msr_mc24.mc_newsapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class Description : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val title = intent.getStringExtra("title") ?: "No Title"
        val description = intent.getStringExtra("description") ?: "No Description"
        setContent {
            DescriptionScreen(title = title, description = description)
        }
    }
}

@Composable
fun DescriptionScreen(title: String, description: String) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        // Display complete title
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = Color.Black
        )
        // Display complete description
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            item {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DescriptionPreview() {
    DescriptionScreen(
        title = "Complete Title",
        description = "Complete Description"
    )
}
