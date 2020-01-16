package me.scana.okgradle

import com.intellij.openapi.module.Module
import io.reactivex.Observable
import me.scana.okgradle.data.repository.Artifact
import me.scana.okgradle.util.Selection

interface OkGradle {

    interface View {
        fun showArtifacts(artifacts: List<Artifact>)
        fun showSuggestion(suggestion: String)
        fun userTextInputObservable(): Observable<String>
        fun userArtifactSelectionObservable(): Observable<Selection<Artifact>>
        fun displayModules(modules: List<Module>)
        fun enableButtons(isEnabled: Boolean)
        fun resetListState()
        fun displayError(throwable: Throwable)
        fun fillSearchPhrase(searchPhrase: String)

    }

    interface Presenter {
        fun takeView(view: View)
        fun dropView()
        fun onAddDependencyClicked()
        fun onModuleSelected(module: Module)
        fun onCopyToClipboardClick()
        fun onSuggestionClick(suggestion: String)

    }

}