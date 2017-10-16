package me.scana.okgradle.data

interface ArtifactRepository {
    fun search(query: String): SearchResult
}