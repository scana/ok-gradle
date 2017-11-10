package me.scana.okgradle.data

import com.google.gson.Gson
import org.apache.http.HttpStatus
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.util.EntityUtils
import java.net.URLEncoder

class MavenRepository(private val httpClient: HttpClient, private val gson: Gson) : ArtifactRepository {

    companion object {
        const val MAVEN_URL = "http://search.maven.org/solrsearch/select"
    }

    override fun search(query: String): SearchResult {
        if (query.isEmpty()) {
            return SearchResult()
        }
        return try {
            var searchResult = artifactIdForName(query)
            if (shouldRetry(searchResult)) {
                searchResult = artifactIdForName(query)
            }
            searchResult
        } catch (unrecoverableException: Exception) {
            SearchResult(error = unrecoverableException)
        }
    }

    private fun artifactIdForName(name: String): SearchResult {
        val args = URLEncoder.encode(name, "UTF-8")
        val request = HttpGet("$MAVEN_URL?q=$args")
        val response = httpClient.execute(request)
        val statusLine = response.statusLine
        val responseEntity = response.entity
        val responseString = responseEntity?.let { EntityUtils.toString(responseEntity) }
        EntityUtils.consumeQuietly(responseEntity)
        if (statusLine.statusCode != HttpStatus.SC_OK) {
            return SearchResult(error = MavenSearchHttpException(statusLine.statusCode))
        }
        return parseSearchResult(responseString)
    }

    private fun parseSearchResult(responseString: String?): SearchResult {
        val result = gson.fromJson(responseString, MavenResult::class.java)
        val artifact = parseArtifact(result.response)
        val suggestion = parseFirstWordSuggestion(result.spellcheck)
        return SearchResult(artifact, suggestion)
    }

    private fun parseArtifact(response: Response): String? {
        if (response.docs.isEmpty()) {
            return null
        }
        val doc = response.docs[0]
        return "${doc.id}:${doc.latestVersion}"
    }

    private fun parseFirstWordSuggestion(spellcheck: Spellcheck): String? {
        if (spellcheck.suggestions.isEmpty()) {
            return null
        }
        return spellcheck.suggestions[0].suggestion[0]
    }

    private fun shouldRetry(searchResult: SearchResult): Boolean {
        if (searchResult.error is MavenSearchHttpException) {
            if (searchResult.error.statusCode >= HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                return true
            }
        }
        return false
    }

    private class MavenSearchHttpException(val statusCode: Int) : Exception() {
        override val message: String?
            get() = "Could not acquire results ($statusCode)"
    }

}