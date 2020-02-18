package me.scana.okgradle.data.repository

import com.google.gson.Gson
import io.reactivex.Single
import me.scana.okgradle.util.fromJson
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Request

class JitPackRepository(private val networkClient: NetworkClient, private val gson: Gson) : ArtifactRepository {

    companion object {
        val JITPACK_URL: HttpUrl = "https://jitpack.io/api/search".toHttpUrl()
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
        val url = JITPACK_URL.newBuilder()
                .addQueryParameter("q", query)
                .addQueryParameter("limit", "5")
                .build()

        val request = Request.Builder()
                .url(url)
                .build()

        val response = networkClient.execute(request) {
            val jitPackResult = gson.fromJson<Map<String, List<String>>>(this.charStream())
            return@execute jitPackResult.entries.map {
                val (groupId, name) = it.key.split(":".toRegex(), 2)
                Artifact(groupId, name, it.value[0])
            }
        }

        return when(response) {
            is NetworkResult.Success -> SearchResult.Success(response.data)
            is NetworkResult.Failure -> SearchResult.Error(response.throwable)
        }
    }

}