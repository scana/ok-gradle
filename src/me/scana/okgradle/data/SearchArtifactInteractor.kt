package me.scana.okgradle.data


class SearchArtifactInteractor(val repositories: List<ArtifactRepository>) {

    suspend fun search(query: String): SearchResult {
        var artifact: String? = null
        var suggestion: String? = null
        var error: Exception? = null
        for (repository in repositories) {
            val result = repository.search(query)
            if (result.error != null) {
                error = result.error
                continue
            }
            result.suggestion?.let {
                suggestion = it
            }
            if (result.artifact != null) {
                artifact = result.artifact
                break
            }
        }
        return SearchResult(artifact, suggestion, error)
    }
}
