package me.scana.okgradle.data.repository

import io.reactivex.Single

interface ArtifactRepository {
    fun search(query: String): Single<SearchResult>
}