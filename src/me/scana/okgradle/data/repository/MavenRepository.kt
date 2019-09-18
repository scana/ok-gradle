package me.scana.okgradle.data.repository

import com.google.gson.Gson
import io.reactivex.Single
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Request

class MavenRepository(private val networkClient: NetworkClient, private val gson: Gson) : ArtifactRepository {

    companion object {
        val MAVEN_URL: HttpUrl = "http://search.maven.org/solrsearch/select".toHttpUrl()
    }

    override fun search(query: String): Single<SearchResult> {
        return Single.create {
            val result = when {
                query.isEmpty() -> SearchResult.Success()
                else -> artifactIdForName(query)
            }
            it.onSuccess(result)
        }
    }

    private fun artifactIdForName(name: String): SearchResult {
        val url = MAVEN_URL.newBuilder()
                .addQueryParameter("q", name)
                .build()

        val request = Request.Builder()
                .url(url)
                .build()

        val response = networkClient.execute(request) {
            val mavenResult = gson.fromJson(this.charStream(), MavenResult::class.java)
            val suggestion = extractSuggestion(mavenResult.spellcheck)
            val artifacts = extractArtifacts(mavenResult.response)
            return@execute Pair(artifacts, suggestion)
        }
        return when(response) {
            is NetworkResult.Success -> SearchResult.Success(response.data.first, response.data.second)
            is NetworkResult.Failure -> SearchResult.Error(response.throwable)
        }
    }

    private fun extractArtifacts(response: Response): List<Artifact> {
        if (response.docs.isEmpty()) {
            return emptyList()
        }
        return response.docs.map { Artifact(it.g, it.a, it.latestVersion) }
    }

    private fun extractSuggestion(spellcheck: Spellcheck): String? {
        if (spellcheck.suggestions.isEmpty()) {
            return null
        }
        return spellcheck.suggestions[0].suggestion[0]
    }
}