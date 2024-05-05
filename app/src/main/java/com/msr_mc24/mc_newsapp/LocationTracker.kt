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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationServices
import com.msr_mc24.mc_newsapp.data.NewsArticle
import com.msr_mc24.mc_newsapp.ui.theme.MC_NewsAppTheme
import com.msr_mc24.mc_newsapp.ui.theme.NewsAppTheme
import kotlinx.coroutines.launch
import android.Manifest
import android.annotation.SuppressLint
import android.location.Geocoder
import android.location.Location
import android.widget.Toast
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


            setContent {
                NewsAppTheme {
                    // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        LocationScreen(apiInterface, search = "${address?.get(0)?.locality}")
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
fun LocationScreen(apiInterface: ApiInterface,search:String) {
    val searchText = search      // Replace "YourFixedSearchTextHere" with your desired fixed search text

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
                onValueChange = { /* No-op as searchText is fixed */ },
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
                    NewsArticleCard(newsArticle, searchText)
                }
            }
        }
    }
}
