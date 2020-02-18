package me.scana.okgradle.data.repository

sealed class SearchResult {
    class Success(val artifacts: List<Artifact> = emptyList(), val suggestion: String? = null) : SearchResult()
    class Error(val throwable: Throwable) : SearchResult()
}
