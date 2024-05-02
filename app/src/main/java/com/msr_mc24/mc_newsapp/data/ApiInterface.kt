package com.msr_mc24.mc_newsapp.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {

    @GET("top-headlines")
    suspend fun getTopHeadlines(
        @Query("country") country: String = "in",
        @Query("apiKey") apiKey: String = "fdc64456321e4f309868a465f1aa750e",
        @Query("language") language: String = "en",
        @Query("category") category: String? = null
    ): NewsResponse

    @GET("everything")
    suspend fun getSearch(
        @Query("apiKey") apiKey: String = "fdc64456321e4f309868a465f1aa750e",
        @Query("language") language: String = "en",
        @Query("sortBy") sortBy: String? = "popularity",
        @Query("q") q: String? = null
    ): NewsResponse

    companion object {
        private const val BASE_URL = "https://newsapi.org/v2/"

        fun create(): ApiInterface {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(ApiInterface::class.java)
        }
    }
}
