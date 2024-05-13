package com.msr_mc24.mc_newsapp

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.nfc.cardemulation.CardEmulation.EXTRA_CATEGORY
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.msr_mc24.mc_newsapp.data.ApiInterface
import com.msr_mc24.mc_newsapp.data.NewsArticle
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

    val context = LocalContext.current

    // Check for internet connectivity
    val isConnected by remember {
        mutableStateOf(checkNetworkConnectivity(context))
    }

    if (!isConnected) {
        Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
    }

    // Fetch news articles when the category changes or the component is recomposed
    LaunchedEffect(category) {
        if (isConnected) {
            try {
                if (category.isNotEmpty()) {
                    // Fetch news articles from the API using the provided category
                    val response = apiInterface.getTopHeadlines(apiKey = "fdc64456321e4f309868a465f1aa750e", language = "en", country = "in", category = category)
                    // Filter out articles with title "[Removed]"
                    val filteredArticles = response.articles.filter { it.title != "[Removed]" }
                    // Clear existing list before adding new articles
                    newsList.clear()
                    // Add filtered articles to the list
                    newsList.addAll(filteredArticles)
                    Log.w("news", category)
                    Log.d("res","$response")

                    // Update loading state
                    loading = false
                } else {
                    // If search query is empty, clear the list
                    newsList.clear()
                    loading = false
                }
            } catch (e: Exception) {
                // Handle error
                e.printStackTrace()
                loading = false
            }
        } else {
            loading = false
        }
    }

    Surface {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .background(color = Color(0xFFB9A6E4), shape = RoundedCornerShape(6.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .wrapContentWidth(align = Alignment.CenterHorizontally)
                ) {
                    val imageResource = when (category) {
                        "business" -> R.drawable.business
                        "technology" -> R.drawable.tech
                        "entertainment" -> R.drawable.entertainment
                        "sports" -> R.drawable.sport
                        "health" -> R.drawable.health
                        "science" -> R.drawable.science
                        // Add more cases for each category
                        else -> R.drawable.headlines // Provide a default image if category doesn't match any known category
                    }
                    Image(
                        painter = painterResource(id = imageResource),
                        contentDescription = null,
                        modifier = Modifier
                            .height(60.dp)
                            .padding(5.dp)
                    )
                    Text(
                        text = category.uppercase(),
                        fontSize = 28.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier
                            .padding(4.dp, 12.dp, 23.dp, 10.dp)
                    )
                }
            }

            // Display news articles
            if (loading) {
                // Show loading indicator
                CircularProgressIndicator()
            } else {
                // Show news articles
                LazyColumn {
                    items(newsList) { newsArticle ->
                        NewsArticleCard(newsArticle, category)
                    }
                }
            }
        }
    }
}

@Composable
fun NewsArticleCard(newsArticle: NewsArticle, category: String) {
    val context = LocalContext.current
    val intent = Intent(context, Description::class.java).apply {
        putExtra("title", newsArticle.title)
        putExtra("author",newsArticle.author)
        putExtra("description", newsArticle.description)
        putExtra("content",newsArticle.content)
        putExtra("image",newsArticle.urlToImage)
        putExtra("url",newsArticle.url)
        putExtra("date",newsArticle.publishedAt)
    }
    Surface {
        Column {
            Card(
                modifier = Modifier
                    .padding(8.dp)
                    .clickable(onClick = {
                        context.startActivity(intent)
                    })
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = newsArticle.title ?: "No Title",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineMedium,
                        fontSize = 27.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${newsArticle.author ?: "Unknown"}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Divider(modifier = Modifier.height(1.dp))
        }
    }
}

// Function to check network connectivity
fun checkNetworkConnectivity(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
    return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}
