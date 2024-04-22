package com.msr_mc24.mc_newsapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.msr_mc24.mc_newsapp.ui.theme.MC_NewsAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MC_NewsAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NewsScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NewsScreen() {
    val newsList = remember {
        // Dummy list of news articles for preview
        listOf(
            NewsArticle("Title 1", "Description 1"),
            NewsArticle("Title 2", "Description 2"),
            NewsArticle("Title 3", "Description 3"),
            NewsArticle("Title 4", "Description 4"),
            NewsArticle("Title 5", "Description 5"),
            NewsArticle("Title 6", "Description 6"),
            NewsArticle("Title 7", "Description 7"),
            NewsArticle("Title 8", "Description 8"),
            NewsArticle("Title 9", "Description 9"),
            NewsArticle("Title 10", "Description 10")
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(newsList) { newsArticle ->
            NewsArticleItem(newsArticle)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun NewsArticleItem(newsArticle: NewsArticle) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = newsArticle.title,
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = newsArticle.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

data class NewsArticle(val title: String, val description: String)

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MC_NewsAppTheme {
        NewsScreen()
    }
}