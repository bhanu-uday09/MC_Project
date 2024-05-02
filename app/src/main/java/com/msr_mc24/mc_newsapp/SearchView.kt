package com.msr_mc24.mc_newsapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.msr_mc24.mc_newsapp.data.ApiInterface
import com.msr_mc24.mc_newsapp.data.NewsArticle
import com.msr_mc24.mc_newsapp.ui.theme.MC_NewsAppTheme
import com.msr_mc24.mc_newsapp.ui.theme.NewsAppTheme
import kotlinx.coroutines.launch

class SearchView : ComponentActivity() {
    private lateinit var apiInterface: ApiInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize apiInterface
        apiInterface = ApiInterface.create()

        setContent {
            NewsAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SearchScreen(apiInterface)
                }
            }
        }
    }
}

@Composable
fun SearchScreen(apiInterface: ApiInterface) {
    var searchText by remember { mutableStateOf("") }
    var searchPosition by remember { mutableStateOf(Alignment.Center) }

    val keyboardController = LocalSoftwareKeyboardController.current

    val newsList = remember { mutableStateListOf<NewsArticle>() }
    var loading by remember { mutableStateOf(true) }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp,20.dp,16.dp,5.dp)
                    .background(color = Color(0xFFFFCBCB), shape = RoundedCornerShape(30.dp)),
                label = { Text("Search") },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        // Move TextField to top center when Enter is pressed
                        if (searchText.isNotEmpty()) {
                            searchPosition = Alignment.TopCenter
                        }
                        // Launch a coroutine to perform the search
                        coroutineScope.launch {
                            try {
                                if (searchText.isNotEmpty()) {
                                    // Fetch news articles from the API using the provided category
                                    val response = apiInterface.getSearch(apiKey = "fdc64456321e4f309868a465f1aa750e", language = "en", sortBy = "popularity", q = searchText)
                                    // Filter out articles with title "REMOVED"
                                    val filteredArticles = response.articles.filter { it.title != "[Removed]" }
                                    // Clear existing list before adding new articles
                                    newsList.clear()
                                    // Add filtered articles to the list
                                    newsList.addAll(filteredArticles)
                                    Log.w("news", searchText)
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
                            keyboardController?.hide()
                        }
                    }
                ),
                trailingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.search),
                        contentDescription = null,
                        modifier = Modifier
                            .height(55.dp)
                            .padding(8.dp)
                    )
                }
            )
        }

        // Display search query
        if (newsList.isNotEmpty() && !loading) {
            Text(
                text = "Showing results for \"$searchText\"",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(26.dp,4.dp,20.dp,5.dp)
            )
        }


        // Display news list
        if (loading) {
            // Show a loading indicator while data is being fetched
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(newsList) { newsArticle ->
                    NewsArticleCard(newsArticle,searchText)
                }
            }
        }
    }
}


