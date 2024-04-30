package com.msr_mc24.mc_newsapp.data

data class NewsResponse(
    val articles: List<NewsArticle>,
    val status: String,
    val totalResults: Int
)
data class Source(
    val id: String?,
    val name: String
)
