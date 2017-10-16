package me.scana.okgradle.data

data class SearchResult(
        val artifact: String? = null,
        val suggestion: String? = null,
        val error: Exception? = null
)
