package me.scana.okgradle.data.repository

import com.google.gson.Gson
import io.reactivex.Single
import me.scana.okgradle.util.fromJson
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Request

class BintrayRepository(private val networkClient: NetworkClient, private val gson: Gson) : ArtifactRepository {

    companion object {
        val BINTRAY_URL: HttpUrl = "https://api.bintray.com/search/packages/maven".toHttpUrl()
    }

    override fun search(query: String): Single<SearchResult> {
        return Single.create {
            val result = when {
                query.isEmpty() -> SearchResult.Success()
                else -> findArtifacts(query)
            }
            it.onSuccess(result)
        }
    }

    private fun findArtifacts(query: String): SearchResult {
        val url = BINTRAY_URL.newBuilder()
                .addQueryParameter("a", "*$query*")
                .addQueryParameter("repo", "jcenter")
                .addQueryParameter("repo", "bintray")
                .build()

        val request = Request.Builder()
                .url(url)
                .build()

        val response = networkClient.execute(request) {
            val result = gson.fromJson<List<BintrayResult>>(this.charStream())
            result.map {
                val (groupId, artifactId) = it.name.split(":".toRegex(), 2)
                Artifact(groupId, artifactId, it.versions.first())
            }
        }

        return when(response) {
            is NetworkResult.Success -> SearchResult.Success(response.data)
            is NetworkResult.Failure -> SearchResult.Error(response.throwable)
        }
    }

}