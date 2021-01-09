package me.scana.okgradle;

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.module.Module
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.PopupMenuListenerAdapter
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.labels.LinkLabel
import com.intellij.ui.components.panels.HorizontalLayout
import com.intellij.ui.components.panels.VerticalLayout
import io.reactivex.Observable
import me.scana.okgradle.data.repository.Artifact
import me.scana.okgradle.util.*
import java.awt.Dimension
import java.awt.Font
import java.awt.event.KeyEvent
import javax.swing.*
import javax.swing.event.PopupMenuEvent


class OkGradleDialog(private val presenter: OkGradle.Presenter) : DialogWrapper(false), OkGradle.View {

    companion object {
        private val logger: Logger = Logger.getInstance(OkGradleDialog::class.java)
    }

    private val hintLink = LinkLabel<Any>("", null).apply {
        setListener({ _, _ -> presenter.onSuggestionClick(text) }, null)
    }

    private val hintPanel = JPanel(HorizontalLayout(2)).apply {
        add(JLabel(Copys.SUGGESTION_FIRST_PART))
        add(hintLink)
        add(JLabel(Copys.SUGGESTION_LAST_PART))
    }

    private val libraryQuery = HintTextField().apply {
        hint = Copys.INPUT_HINT
        onKeyPress(KeyEvent.VK_DOWN) {
            transferFocus()
            resultList.selectedIndex = 0
        }
    }

    private val resultsListModel = ArtifactListModel()
    private val resultList = JBList(resultsListModel).apply {
        selectionMode = ListSelectionModel.SINGLE_SELECTION
        visibleRowCount = -1
    }

    override fun displayError(throwable: Throwable) {
        logger.warn(throwable.message)
    }

    override fun setUpButtons(allEnabled: Boolean, isAddDependencyVisible: Boolean) {
        addDependencyButton.isEnabled = allEnabled
        clipboardCopyButton.isEnabled = allEnabled
        addDependencyButton.isVisible = isAddDependencyVisible
    }

    private val clipboardCopyButton = JButton().apply {
        text = Copys.COPY_TO_CLIPBOARD_ACTION
        isEnabled = false
        addActionListener { presenter.onCopyToClipboardClick() }
    }

    private val addDependencyButton = JButton().apply {
        text = Copys.ADD_DEPENDENCY_ACTION
        isEnabled = false
        addActionListener { presenter.onAddDependencyClicked() }
    }

    init {
        init()
        title = Copys.TITLE
    }

    override fun show() {
        presenter.takeView(this)
        super.show()
    }

    override fun dispose() {
        presenter.dropView()
        super.dispose()
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
                    add(JLabel(Copys.PROMPT_HEADER))
                    add(hintPanel)
                }
        )

        panel.add(libraryQuery)
        panel.add(JLabel(Copys.RESULT_LIST_TITLE))
        val scrollPane = JBScrollPane(resultList)
        scrollPane.preferredSize = Dimension(500, 200)
        panel.add(scrollPane)
        panel.add(
                JPanel(HorizontalLayout(8)).apply {
                    add(clipboardCopyButton)
                    add(addDependencyButton)
                }
        )
        val tip = Copys.TIP.format(Copys.TIPS.shuffled().first())
        panel.add(
                JLabel(tip).also {
                    it.foreground = it.foreground.transparent(123)
                    it.font = Font(it.font.name, it.font.style, it.font.size - 2)
                }
        )
        return panel
    }

    override fun getPreferredFocusedComponent() = libraryQuery

    override fun createActions(): Array<Action> = emptyArray()

    override fun userTextInputObservable(): Observable<String> = libraryQuery.observeText()

    override fun userArtifactSelectionObservable(): Observable<Selection<Artifact>> =
            resultList.observeSelection()
                    .doOnNext { registerResultListKeyListener() }

    private fun registerResultListKeyListener() {
        resultList.singleOnKeyPress(KeyEvent.VK_ENTER) {
            presenter.onAddDependencyClicked()
        }
    }

    override fun displayModules(modules: List<Module>) {
        val menu = JPopupMenu(Copys.MODULES_TITLE)
        menu.add(Copys.MODULES_TITLE).also {
            it.isEnabled = false
        }
        modules.forEach {
            menu.add(it.name).addActionListener { _ ->
                presenter.onModuleSelected(it)
            }
        }
        with(addDependencyButton) {
            menu.addPopupMenuListener(object : PopupMenuListenerAdapter() {
                override fun popupMenuCanceled(e: PopupMenuEvent?) {
                    registerResultListKeyListener()
                }
            })
            menu.show(this, this.x, this.y)
            menu.invokeKeyDownPress()
        }
    }

    private fun configureViews() {
        hideSuggestion()
    }

    override fun showArtifacts(artifacts: List<Artifact>) {
        resultsListModel.addAll(artifacts)
    }

    override fun showSuggestion(suggestion: String) {
        hintLink.text = suggestion
        hintPanel.isVisible = true
    }

    private fun hideSuggestion() {
        hintPanel.isVisible = false
    }

    override fun resetListState() {
        resultList.clearSelection()
        resultsListModel.clear()
        hideSuggestion()
    }

    override fun fillSearchPhrase(searchPhrase: String) {
        libraryQuery.text = searchPhrase
    }
}