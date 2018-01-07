package me.scana.okgradle;

import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.labels.LinkLabel
import com.intellij.ui.components.panels.HorizontalLayout
import com.intellij.ui.components.panels.VerticalLayout
import com.intellij.util.ui.TextTransferable
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.swing.Swing
import me.scana.okgradle.data.SearchArtifactInteractor
import me.scana.okgradle.data.SearchResult
import me.scana.okgradle.util.SimpleDocumentListener
import javax.swing.*
import javax.swing.event.DocumentEvent


class OkGradleDialog(val interactor: SearchArtifactInteractor) : DialogWrapper(false) {

    companion object {
        const private val SEARCH_START_DELAY_IN_MILLIS = 500L
    }

    private val hintLink = LinkLabel<Any>("", null).apply {
        setListener({ _, _ -> libraryQuery.text = result.suggestion }, null)
    }

    private val hintPanel = JPanel(HorizontalLayout(2)).apply {
        add(JLabel("(did you mean: "))
        add(hintLink)
        add(JLabel("?)"))
    }

    private val tutorialHint = LinkLabel<Any>("try me!", null)

    private val libraryQuery = JTextField()
    private val resultArea = JTextArea(3, 80).apply {
        isEditable = false
    }
    private val copyButton = LinkLabel<Any>(" ", null)

    private var job: Job? = null
    private var result: SearchResult = SearchResult()

    init {
        init()
        title = "OK, Gradle!"
    }

    override fun createCenterPanel(): JComponent {
        val panel = buildPanel()
        configureViews()
        return panel
    }

    private fun buildPanel(): JPanel {
        val panel = JPanel(VerticalLayout(8))
        panel.add(
                JPanel(HorizontalLayout(8)).apply {
                    add(JLabel("Which library you need?"))
                    add(hintPanel)
                    add(tutorialHint)
                }
        )
        panel.add(libraryQuery)
        panel.add(JLabel("Put this in your build.gradle file:"))
        panel.add(resultArea)
        panel.add(copyButton)
        return panel
    }

    private fun configureViews() {
        hideSuggestion()
        showClipboardButton(false)
        copyButton.setListener({ _, _ -> onCopyToClipboardClick() }, null)
        libraryQuery.document.addDocumentListener(object : SimpleDocumentListener() {
            override fun update(e: DocumentEvent?) {
                showClipboardButton(false)
                hideSuggestion()
                showProgress()
                job?.cancel()
                job = launch(Swing) {
                    delay(SEARCH_START_DELAY_IN_MILLIS)
                    result = interactor.search(libraryQuery.text)
                    if (result.error != null) {
                        displayError(result.error)
                    } else {
                        when (result.artifact) {
                            null -> showNoResultsFoundMessage()
                            else -> showResults(result.artifact as String) //todo: avoid smartcast
                        }
                    }
                    result.suggestion?.let {
                        showSuggestion(it)
                    }
                }
            }
        })
        tutorialHint.setListener({ _, _ ->
            libraryQuery.text = "retrofit "
            tutorialHint.isVisible = false
        }, null)
    }

    private fun displayError(error: Exception?) {
        resultArea.text = "${error?.javaClass?.name}:${error?.message}"
    }

    private fun onCopyToClipboardClick() {
        CopyPasteManager.getInstance().setContents(TextTransferable(result.artifact))
    }

    private fun showProgress() {
        resultArea.text = wrapAsDependencyExample("...")
    }

    private fun showNoResultsFoundMessage() {
        resultArea.text = "No results found :("
    }

    private fun showResults(artifact: String) {
        resultArea.text = wrapAsDependencyExample(artifact)
        showClipboardButton(true)
    }

    private fun wrapAsDependencyExample(artifact: String): String {
        val builder = StringBuilder()
        builder.append("dependencies {\n")
        builder.append("    implementation '$artifact'\n")
        builder.append("}")
        return builder.toString()
    }

    private fun showSuggestion(suggestion: String) {
        hintLink.text = suggestion
        hintPanel.isVisible = true
    }

    private fun hideSuggestion() {
        hintPanel.isVisible = false
    }

    private fun showClipboardButton(isVisible: Boolean) {
        when (isVisible) {
            true -> copyButton.text = "Copy result to clipboard"
            false -> copyButton.text = " "
        }
    }

    override fun getPreferredFocusedComponent(): JComponent? {
        return libraryQuery
    }

    override fun createActions() = arrayOf(okAction)
}