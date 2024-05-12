package com.msr_mc24.mc_newsapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
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
import com.google.firebase.database.*
import com.msr_mc24.mc_newsapp.ui.theme.MC_NewsAppTheme
import com.msr_mc24.mc_newsapp.ui.theme.NewsAppTheme

class FavoriteNews : ComponentActivity() {

    private lateinit var database: DatabaseReference
    private val favorites: MutableState<List<NewsItem>?> = mutableStateOf(null)
    private var databaseListener: ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NewsAppTheme {
                Surface() {
                    FavoriteNewsContent()
                }
            }
        }
        database = FirebaseDatabase.getInstance().reference.child("favorites")
        retrieveFavorites()
        Log.d("data", database.toString())
    }

    private fun retrieveFavorites() {
        databaseListener = database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newsItems = snapshot.children.mapNotNull { it.getValue(NewsItem::class.java) }
                favorites.value = newsItems

                // Log the database response
                Log.d("Firebase", "Data retrieved successfully: $newsItems")
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
                Log.e("Firebase", "Failed to retrieve data from database: ${error.message}")
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        databaseListener?.let { database.removeEventListener(it) } // Remove the listener to avoid memory leaks
    }

    @Composable
    fun FavoriteNewsContent() {
        val favList = favorites.value
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
                    Image(
                        painter = painterResource(id = R.drawable.fav),
                        contentDescription = null,
                        modifier = Modifier
                            .height(60.dp)
                            .padding(5.dp)
                    )
                    Text(
                        text = "FAVORITES",
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

            if (favList != null) {
                LazyColumn {
                    items(favList) { newsItem ->
                        NewsArticleCard2(newsItem)
                    }
                }
            } else {
                Text("Loading...") // Placeholder for loading state
            }
        }
    }

    @Composable
    fun NewsArticleCard2(newsItem: NewsItem) {
        val context = LocalContext.current
        val intent = Intent(this, Description::class.java).apply {
            putExtra("title", newsItem.title)
            putExtra("description", newsItem.description)
            putExtra("content", newsItem.content)
            putExtra("author", newsItem.author)
            putExtra("date", newsItem.date)
            putExtra("url", newsItem.url)
            putExtra("image", newsItem.image)

            putExtra("fromFavorites", true)
        }
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
                    text = newsItem.title ?: "No Title",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineMedium,
                    fontSize = 27.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = newsItem.author ?: "Unknown",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        Divider(modifier = Modifier.height(1.dp))
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        MC_NewsAppTheme {
            FavoriteNewsContent()
        }
    }
}
