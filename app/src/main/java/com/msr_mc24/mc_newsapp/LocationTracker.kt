package com.msr_mc24.mc_newsapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.msr_mc24.mc_newsapp.data.ApiInterface
import com.msr_mc24.mc_newsapp.ui.theme.NewsAppTheme
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.msr_mc24.mc_newsapp.data.NewsArticle
import android.Manifest
import android.location.Geocoder
import android.location.Location
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import java.util.*


class LocationTracker : ComponentActivity(){
    private lateinit var apiInterface: ApiInterface
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var geocoder: Geocoder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        apiInterface = ApiInterface.create()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        geocoder = Geocoder(this, Locale.getDefault())

        getLastLocation()


    }


    private fun getLastLocation(){
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),10101)
        }

        val lastLocation = fusedLocationProviderClient.lastLocation

        lastLocation.addOnSuccessListener {
            Log.d("Location","get_last_latitude : ${it.latitude}")
            Log.d("Location","get_last_longitude : ${it.longitude}")


            val address = geocoder.getFromLocation(it.latitude,it.longitude,1)

            Log.d("Address","${address?.get(0)?.getAddressLine(0)}")
            Log.d("Locality","${address?.get(0)?.locality}")

            val currloc = address?.get(0)?.getAddressLine(0)
            val locality = address?.get(0)?.locality

            setContent {
                val searchText = intent.getStringExtra("searchText") ?: ""
                NewsAppTheme {
                    // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Column {
                            Row(
                                modifier = Modifier
                                    .background(
                                        color = Color(0xFFB9A6E4),
                                        shape = RoundedCornerShape(6.dp)
                                    )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .wrapContentWidth(align = Alignment.CenterHorizontally)
                                ) {
                                    val imageResource = R.drawable.location
                                    Image(
                                        painter = painterResource(id = imageResource),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .height(60.dp)
                                            .padding(5.dp)
                                    )
                                    if (locality != null) {
                                        Text(
                                            text = locality.uppercase(),
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
                            }
                            if (locality != null && currloc != null) {
                                LocationScreen(apiInterface, locality, currloc)
                            }
                        }
                    }
                }
            }

        }

        lastLocation.addOnFailureListener {
            Log.d("Location","Failed get location")
        }
    }

}





@Composable
fun LocationScreen(apiInterface: ApiInterface, location: String, address: String) {
    var newsList by remember { mutableStateOf<List<NewsArticle>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    val coroutineScope = rememberCoroutineScope()

    // Fetch news articles when the composable is first initialized
    LaunchedEffect(location) {
        try {
            Log.d("LocationScreen", "Fetching news articles for location: $location")
            // Fetch news articles from the API using the provided location
            val response = apiInterface.getSearch(
                apiKey = "fdc64456321e4f309868a465f1aa750e",
                language = "en",
                sortBy = "popularity",
                q = location
            )
            Log.d("LocationScreen", "Response: $response")
            // Filter out articles with title "REMOVED"
            val filteredArticles = response.articles.filter { it.title != "[Removed]" }
            // Update news list with filtered articles
            newsList = filteredArticles
            // Update loading state
            loading = false
        } catch (e: Exception) {
            // Handle error
            e.printStackTrace()
            Log.e("LocationScreen", "Error fetching news articles: ${e.message}")
            loading = false
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Display search query
        if (newsList.isNotEmpty() && !loading) {
            Text(
                text = "Current Location: \"$address\"",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(26.dp, 4.dp, 20.dp, 5.dp)
            )
            Text(
                text = "Showing NEWS of \"$location\"",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(26.dp, 4.dp, 20.dp, 5.dp)
            )
        }

        // Display news list or loading indicator
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
                    NewsArticleCard(newsArticle, location)
                }
            }
        }
    }
}
