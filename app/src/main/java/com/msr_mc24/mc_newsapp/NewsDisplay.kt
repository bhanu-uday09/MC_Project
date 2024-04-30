package com.msr_mc24.mc_newsapp

import android.content.Intent
import android.nfc.cardemulation.CardEmulation.EXTRA_CATEGORY
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.msr_mc24.mc_newsapp.data.ApiInterface
import com.msr_mc24.mc_newsapp.data.NewsArticle
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.msr_mc24.mc_newsapp.ui.theme.NewsAppTheme

class NewsDisplay : ComponentActivity() {
    private lateinit var apiInterface: ApiInterface
    private lateinit var category: String

    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve the selected category from the intent extras
        category = intent.getStringExtra(EXTRA_CATEGORY) ?: ""
        // Initialize the API interface
        apiInterface = ApiInterface.create() // Example: Initialize your API interface here

        setContent {
            NewsAppTheme {
                Surface {
                    NewsScreen(apiInterface = apiInterface, category = category)
                }
            }
        }
    }
}


@Composable
fun NewsScreen(apiInterface: ApiInterface, category: String) {
    val newsList = remember { mutableStateListOf<NewsArticle>() }
    var loading by remember { mutableStateOf(true) }

    // Fetch news articles when the category changes or the component is recomposed
    LaunchedEffect(category) {
        try {
            // Fetch news articles from the API using the provided category
            val response = apiInterface.getTopHeadlines(country = "in", category = category, apiKey = "fdc64456321e4f309868a465f1aa750e", language = "en")
            // Clear existing list before adding new articles
            newsList.clear()
            // Add fetched articles to the list
            newsList.addAll(response.articles)
            Log.w("news","$newsList")

            // Update loading state
            loading = false
        } catch (e: Exception) {
            // Handle error
            e.printStackTrace()
            loading = false
        }
    }

    Surface {
        if (loading) {
            // Show loading indicator
            Text(text = "Loading...")
        } else {
            // Show news articles
            LazyColumn {
                items(newsList) { newsArticle ->
                    NewsArticleCard(newsArticle)
                }
            }
        }
    }
}

@Composable
fun NewsArticleCard(newsArticle: NewsArticle) {
    val context = LocalContext.current
    val intent = Intent(context, Description::class.java).apply {
        putExtra("title", newsArticle.title)
        putExtra("description", newsArticle.content)
    }
    Surface {
        Column {
            Card(
                modifier = Modifier
                    .padding(8.dp)
                    .clickable(onClick = {
                        context.startActivity(intent)
                    }),
//                elevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(text = newsArticle.title ?: "No Title", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineMedium, fontSize = 27.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "${newsArticle.author ?: "Unknown"}", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(4.dp))
//                    Text(text = newsArticle.description ?: "No Description", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

    }
}
