package me.scana.okgradle

import com.google.gson.GsonBuilder
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataKeys
import com.intellij.openapi.project.Project
import me.scana.okgradle.data.AddDependencyUseCase
import me.scana.okgradle.data.SearchArtifactsUseCase
import me.scana.okgradle.data.repository.*
import me.scana.okgradle.util.IntellijTools
import me.scana.okgradle.util.Notifier
import okhttp3.OkHttpClient

class OkGradleAction : AnAction() {

    override fun actionPerformed(event: AnActionEvent) {
        val gson = GsonBuilder()
                .registerTypeAdapter(Spellcheck::class.java, SpellcheckDeserializer())
                .create()
        val networkClient = NetworkClient(OkHttpClient.Builder().build())
        val repositories = mapOf(
                "Google" to GoogleRepository(networkClient),
                "Maven" to MavenRepository(networkClient, gson),
                "JitPack" to JitPackRepository(networkClient, gson)
        )
        val searchUseCase = SearchArtifactsUseCase(repositories)
        val project = event.getData(DataKeys.PROJECT) as Project
        val notifier = Notifier(project)
        val addDependencyUseCase = AddDependencyUseCase(project, notifier)
        val intellijTools = IntellijTools(project)
        val presenter = OkGradleDialogPresenter(searchUseCase, addDependencyUseCase, intellijTools)

        val dialog = OkGradleDialog(presenter)
        dialog.show()
    }
}