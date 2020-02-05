package me.scana.okgradle

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import me.scana.okgradle.data.AddDependencyUseCase
import me.scana.okgradle.data.SearchArtifactsUseCase
import me.scana.okgradle.data.repository.Artifact
import me.scana.okgradle.data.repository.SearchResult
import me.scana.okgradle.util.IntellijTools
import me.scana.okgradle.util.Selection
import java.util.concurrent.TimeUnit

class OkGradleDialogPresenter(
        project: Project?,
        private val interactor: SearchArtifactsUseCase,
        private val addDependencyUseCase: AddDependencyUseCase,
        private val intellijTools: IntellijTools
) : OkGradle.Presenter {

    private val SEARCH_START_DELAY_IN_MILLIS = 500L

    private var selectedArtifact: Artifact? = null
    private var view: OkGradle.View? = null
    private val disposables = CompositeDisposable()
    private val hasProject = project != null

    override fun takeView(view: OkGradle.View) {
        this.view = view
        view.setUpButtons(
                allEnabled = false,
                isAddDependencyVisible = hasProject
        )
        observeInput(view)
        observeArtifactSelection(view)
    }

    private fun observeInput(view: OkGradle.View) {
        view.userTextInputObservable()
                .debounce(SEARCH_START_DELAY_IN_MILLIS, TimeUnit.MILLISECONDS)
                .doOnNext { this.view?.resetListState() }
                .switchMap { interactor.search(it) }
                .subscribe(this::onSearchResult, this::onCriticalError)
                .attachToLifecycle()
    }

    private fun observeArtifactSelection(view: OkGradle.View) {
        view.userArtifactSelectionObservable()
                .startWith(Selection.None())
                .subscribe({ selection -> onArtifactSelectionChanged(selection) }, this::onCriticalError)
                .attachToLifecycle()
    }

    private fun onSearchResult(result: SearchResult) = when (result) {
        is SearchResult.Success -> displayResult(result)
        is SearchResult.Error -> displayError(result)
    }

    private fun displayResult(result: SearchResult.Success) {
        view?.showArtifacts(result.artifacts)
        result.suggestion?.let {
            view?.showSuggestion(it)
        }
    }

    private fun displayError(error: SearchResult.Error) {
        view?.displayError(error.throwable)
    }

    private fun onCriticalError(error: Throwable) {
        throw error
    }

    private fun onArtifactSelectionChanged(selection: Selection<Artifact>) = when(selection) {
        is Selection.Item -> {
            selectedArtifact = selection.value
            view?.setUpButtons(
                    allEnabled = true,
                    isAddDependencyVisible = hasProject
            )
        }
        is Selection.None -> {
            selectedArtifact = null
            view?.setUpButtons(
                    allEnabled = false,
                    isAddDependencyVisible = hasProject
            )
        }
    }

    override fun dropView() {
        disposables.clear()
        view = null
    }

    override fun onAddDependencyClicked() {
        if (hasProject) {
            val modules = intellijTools.getModules().sortedBy { it.name }
            view?.displayModules(modules)
        } else {
            onCopyToClipboardClick()
        }
    }

    override fun onModuleSelected(module: Module) {
        selectedArtifact?.let {
            addDependencyUseCase.addDependency(module, it)
        }
    }

    override fun onCopyToClipboardClick() {
        selectedArtifact?.let {
            addDependencyUseCase.copyToClipboard(it)
        }
    }

    private fun Disposable.attachToLifecycle() {
        disposables.add(this)
    }

    override fun onSuggestionClick(suggestion: String) {
        view?.resetListState()
        view?.fillSearchPhrase(suggestion)
    }
}
