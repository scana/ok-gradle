package me.scana.okgradle.data

import com.google.gson.Gson
import me.scana.okgradle.util.fromJson
import org.apache.http.HttpStatus
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.util.EntityUtils
import java.net.URLEncoder

class JitPackRepository(private val httpClient: HttpClient, private val gson: Gson) : ArtifactRepository {

    companion object {
        val JITPACK_URL = "https://jitpack.io/api/search"
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
        } catch (e: Exception) {
            SearchResult(error = e)
        }
    }

    private fun artifactIdForName(name: String): SearchResult {
        val args = URLEncoder.encode(name, "UTF-8")
        val request = HttpGet("$JITPACK_URL?q=$args&limit=10")
        val response = httpClient.execute(request)
        val statusLine = response.statusLine
        val responseEntity = response.entity
        val responseString = responseEntity?.let { EntityUtils.toString(responseEntity) }
        EntityUtils.consumeQuietly(responseEntity)

        if (statusLine.statusCode != HttpStatus.SC_OK) {
            return SearchResult(error = JitPackSearchHttpException(statusLine.statusCode))
        }
        return parseSearchResult(responseString)
    }

    private fun parseSearchResult(responseString: String?): SearchResult {
        val result = parseJson(responseString)
        val artifact = parseArtifact(result)
        return SearchResult(artifact, "")
    }

    private fun parseJson(responseString: String?): JitPackResult {
        val artifacts = mutableListOf<Artifact>()

        responseString?.let {
            val libs = gson.fromJson<Map<String, List<String>>>(it)
            val iterator = libs.entries.iterator()

            while (iterator.hasNext()) {
                val lib = iterator.next()
                artifacts.add(Artifact(lib.key, lib.value))
            }
        }

        return JitPackResult(artifacts)
    }

    private fun parseArtifact(result: JitPackResult): String? {
        result.artifacts.firstOrNull {
            it.id.isNotEmpty() && it.versions.isNotEmpty()
        }?.let {
            return "${it.id}:${it.versions[0]}"
        }

        return null
    }

    private fun shouldRetry(searchResult: SearchResult): Boolean {
        return searchResult.error is JitPackSearchHttpException &&
                searchResult.error.statusCode >= HttpStatus.SC_INTERNAL_SERVER_ERROR
    }

    private class JitPackSearchHttpException(val statusCode: Int) : Exception() {
        override val message: String?
            get() = "Could not acquire results ($statusCode)"
    }
}