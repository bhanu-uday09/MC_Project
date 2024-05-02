package com.msr_mc24.mc_newsapp

import android.os.AsyncTask
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

class FetchWebpageContent(private val listener: OnFetchCompleteListener) : AsyncTask<String, Void, String>() {

    override fun doInBackground(vararg urls: String): String {
        val url = urls[0]
        val doc: Document = Jsoup.connect(url).get()
        val articleBody = extractArticleBody(doc)
        return articleBody
    }

    override fun onPostExecute(result: String) {
        listener.onFetchComplete(result)
    }

    private fun extractArticleBody(doc: Document): String {
        // Remove unwanted elements
        doc.select("nav, footer, aside").remove()

        // Select only main article content
        val articleElements: Elements = doc.select("p") // You may need to adjust this selector based on the structure of the webpage

        val articleBodyBuilder = StringBuilder()
        for (element in articleElements) {
            // Append text content of all elements in the article body
            articleBodyBuilder.append(element.text())
        }
        return articleBodyBuilder.toString()
    }

    interface OnFetchCompleteListener {
        fun onFetchComplete(content: String)
    }
}
