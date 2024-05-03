package com.msr_mc24.mc_newsapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.core.content.ContextCompat.startActivity
import coil.compose.rememberAsyncImagePainter
import java.util.Locale

class Description : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val title = intent.getStringExtra("title") ?: "No Title"
        val description = intent.getStringExtra("description") ?: "No Description"
        val content = intent.getStringExtra("content")?: "No Content"
        val author = intent.getStringExtra("author")?: "No Author"
        val date = intent.getStringExtra("date")?: "No Date"
        val url = intent.getStringExtra("url")?: "No URL"
        val image = intent.getStringExtra("image")?: "No Image"
        setContent {
            DescriptionScreen(
                title = title,
                description = description,
                content = content,
                author = author,
                date = date,
                url = url,
                image = image
            )
        }
    }
    companion object {
        init {
            System.loadLibrary("mc_newsapp")
        }
    }
}

// Import JNI function
external fun preprocessText(inputText: String): String

@Composable
fun DescriptionScreen(
    title: String,
    description: String,
    content: String,
    author: String,
    date: String,
    url: String,
    image: String
) {

    val context = LocalContext.current
    var webpageContent by remember { mutableStateOf("") }
    var showWebpageContent by remember { mutableStateOf(false) }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(10.dp)
    ) {
        // Fixed image card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            shape = RectangleShape
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = rememberAsyncImagePainter(image),
                    contentDescription = image,
                    contentScale = ContentScale.Fit
                )
            }
        }

        // Scrollable content
        Box(modifier = Modifier.weight(1f)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                item {
                    // Display title
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Display author with date
                    Row(
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Text(
                            text = author,
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = date,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    Spacer(modifier = Modifier.height(5.dp))
                    // Display description
                    Text(
                        text = description,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 8.dp),
                        maxLines = 40,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Justify
                    )

                    if (!showWebpageContent) {
                        // Split content into paragraphs
                        val paragraphs = content.split("\n\n")

                        // Display content paragraphs
                        paragraphs.forEach { paragraph ->
                            Text(
                                text = paragraph.trim()
                                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() },
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier.padding(bottom = 8.dp),
                                maxLines = 4,
                                textAlign = TextAlign.Justify
                            )
                        }
                    } else {
                        // Call the JNI function to preprocess the text
                        val preprocessResult = preprocessText(webpageContent)

                        // Split preprocessed text into paragraphs
                        val paragraphs = preprocessResult.split("\n\n")

                        // Display preprocessed summarized text paragraphs
                        paragraphs.forEach { paragraph ->
                            // Split paragraph into sentences
                            val sentences = paragraph.split(". ")

                            // Capitalize the first letter of each sentence and join them back together
                            val formattedParagraph = sentences.joinToString(". ") { sentence ->
                                sentence.trim().capitalize()
                            }

                            Text(
                                text = formattedParagraph.trim(),
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier.padding(bottom = 8.dp),
                                textAlign = TextAlign.Justify
                            )
                        }
                    }
                }
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
        ) {
            Button(
                onClick = {
                    // Fetch webpage content
                    val fetchWebpageContent = FetchWebpageContent(object :
                        FetchWebpageContent.OnFetchCompleteListener {
                        override fun onFetchComplete(content: String) {
                            webpageContent = content
                            // Update the state to trigger recomposition
                            showWebpageContent = true
                        }
                    })
                    fetchWebpageContent.execute(url)
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Read More",
                    style = MaterialTheme.typography.labelLarge
                )
            }
            Button(
                onClick = {
                    // Open URL
                    val uri = Uri.parse(url)
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    context.startActivity(intent)
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Open In Browser",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}
