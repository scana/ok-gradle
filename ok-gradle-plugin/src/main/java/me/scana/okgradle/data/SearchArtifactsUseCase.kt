package me.scana.okgradle.data

import io.reactivex.Observable
import io.reactivex.Single
import me.scana.okgradle.data.repository.ArtifactRepository
import me.scana.okgradle.data.repository.ArtifactSearchException
import me.scana.okgradle.data.repository.SearchResult

typealias Title = String

class SearchArtifactsUseCase(private val repositories: Map<Title, ArtifactRepository>) {

    fun search(query: String): Observable<SearchResult> {
        return Single.concat(
                repositories.map {
                    it.value.search(query).onErrorReturn {
                        t -> SearchResult.Error(ArtifactSearchException(it.key, t))
                    }
        }).toObservable()
    }
}
