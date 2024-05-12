package com.msr_mc24.mc_newsapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.core.content.ContextCompat.startActivity
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.Locale

data class NewsItem(
    val title: String,
    val description: String,
    val content: String,
    val author: String,
    val date: String,
    val url: String,
    val image: String
) {
    constructor() : this(" ", " "," ", " "," ", " "," ")
}

class Description : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val title = intent.getStringExtra("title") ?: "No Title"
        val description = intent.getStringExtra("description") ?: "No Description"
        val content = intent.getStringExtra("content") ?: "No Content"
        val author = intent.getStringExtra("author") ?: "No Author"
        val date = intent.getStringExtra("date") ?: "No Date"
        val url = intent.getStringExtra("url") ?: "No URL"
        val image = intent.getStringExtra("image") ?: "No Image"

        // Check if it's from favorites
        val fromFavorites = intent.getBooleanExtra("fromFavorites", false)

        setContent {
            DescriptionScreen(
                title = title,
                description = description,
                content = content,
                author = author,
                date = date,
                url = url,
                image = image,
                fromFavorites = fromFavorites // Pass the flag to DescriptionScreen
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
    image: String,
    fromFavorites: Boolean
) {

    val context = LocalContext.current
    var webpageContent by remember { mutableStateOf("") }
    var showWebpageContent by remember { mutableStateOf(false) }
    val database = Firebase.database
    val favoritesRef = database.getReference("favorites")

    // Initialize TextToSpeech object
    var tts: TextToSpeech? by remember { mutableStateOf(null) }
    DisposableEffect(context) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // TTS engine initialized successfully
                tts?.language = Locale.getDefault()
            } else {
                Log.e("TTS", "Initialization failed")
            }
        }

        onDispose {
            // Release TextToSpeech object when composable is disposed
            tts?.stop()
            tts?.shutdown()
        }
    }

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
                            modifier = Modifier
                                .weight(1f)
                                .padding(top = 6.dp,end = 10.dp)
                        )
                        Text(
                            text = date,
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(top = 6.dp,end = 10.dp)
                        )
                        // ClickableTextToSpeech for title, author, and description
                        ClickableTextToSpeech(
                            texts = listOf(title, author, description),
                            tts = tts
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
        Row {
            // Change the button label and functionality based on whether it's from favorites or not
            Button(
                onClick = {
                    if (fromFavorites) {
                        // Remove from favorites logic
                        val query = favoritesRef.orderByChild("title").equalTo(title)
                        query.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for (postSnapshot in snapshot.children) {
                                    postSnapshot.ref.removeValue()
                                        .addOnSuccessListener {
                                            Toast.makeText(context, "Removed from Favorites", Toast.LENGTH_SHORT).show()
                                            Log.e("mas","Removed")
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(context, "Failed to remove from Favorites", Toast.LENGTH_SHORT).show()
                                        }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.e("mas", "Failed to read value.", error.toException())
                            }
                        })
                    } else {
                        // Add to favorites logic
                        val newsItem = NewsItem(
                            title = title,
                            description = description,
                            content = content,
                            author = author,
                            date = date,
                            url = url,
                            image = image
                        )

                        val newsItemId = favoritesRef.push().key
                        newsItemId?.let {
                            favoritesRef.child(it).setValue(newsItem)
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Added to Favorites", Toast.LENGTH_SHORT).show()
                                    Log.e("mas","Added")
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Failed to add to Favorites", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = if (fromFavorites) "Remove from Favorites" else "Add to Favorites"
                )
            }
        }
    }
}

@Composable
fun ClickableTextToSpeech(texts: List<String>, tts: TextToSpeech?) {
    var isSpeaking by remember { mutableStateOf(false) }

    // Image for the button
    val image = if (isSpeaking) {
        // If TTS is speaking, use stop icon
        R.drawable.stop
    } else {
        // If TTS is not speaking, use read icon
        R.drawable.play
    }

    // Clickable image
    Image(
        painter = painterResource(id = image),
        contentDescription = if (isSpeaking) "Stop Speaking" else "Read Aloud",
        modifier = Modifier
            .padding(start = 20.dp)
            .height(30.dp)
            .clickable {
                if (isSpeaking) {
                    // If TTS is speaking, stop it
                    tts?.stop()
                    isSpeaking = false
                } else {
                    // If TTS is not speaking, start it
                    texts.forEach { text ->
                        tts?.speak(text, TextToSpeech.QUEUE_ADD, null, null)
                    }
                    isSpeaking = true
                }
            }
    )
}
