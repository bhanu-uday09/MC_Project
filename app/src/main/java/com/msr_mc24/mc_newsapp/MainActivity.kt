package com.msr_mc24.mc_newsapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.msr_mc24.mc_newsapp.ui.theme.MC_NewsAppTheme
import com.msr_mc24.mc_newsapp.data.ApiInterface
import com.msr_mc24.mc_newsapp.data.NewsResponse
import com.msr_mc24.mc_newsapp.data.NewsArticle
import com.msr_mc24.mc_newsapp.data.RetrofitClient

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MC_NewsAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Provide an instance of ApiInterface here
                    val apiInterface = ApiInterface.create()
                    NewsScreen(apiInterface)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NewsScreen(apiInterface: ApiInterface) {
    val newsList = remember { mutableStateListOf<NewsArticle>() }
    val loading = remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            val response = apiInterface.getTopHeadlines("us", "fdc64456321e4f309868a465f1aa750e")
            newsList.addAll(response.articles)
            loading.value = false
        } catch (e: Exception) {
            // Handle error
            e.printStackTrace()
            loading.value = false
        }
    }

    if (loading.value) {
        // Show loading indicator
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        // Show news articles
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(newsList) { newsArticle ->
                NewsArticleItem(newsArticle = newsArticle)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun NewsArticleItem(newsArticle: NewsArticle) {
    val context = LocalContext.current // Accessing the Context using LocalContext

    val intent = Intent(context, Description::class.java).apply {
        putExtra("title", newsArticle.title)
        putExtra("description", newsArticle.description)
    }

    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                context.startActivity(intent)
            }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Display article title
            Text(
                text = newsArticle.title ?: "No Title",
                style = MaterialTheme.typography.headlineMedium
            )
            // Display article description
            Text(
                text = newsArticle.description ?: "No Description",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MC_NewsAppTheme {
        NewsScreen(ApiInterface.create())
    }
}