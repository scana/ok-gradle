package me.scana.okgradle

import com.google.gson.GsonBuilder
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.Project
import me.scana.okgradle.data.AddDependencyUseCase
import me.scana.okgradle.data.AddDependencyUseCaseFactory
import me.scana.okgradle.data.SearchArtifactsUseCase
import me.scana.okgradle.data.repository.*
import me.scana.okgradle.util.IntellijTools
import me.scana.okgradle.util.Notifier
import me.scana.okgradle.util.ToolsFactory
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
                "JitPack" to JitPackRepository(networkClient, gson),
                "Bintray" to BintrayRepository(networkClient, gson)
        )
        val searchUseCase = SearchArtifactsUseCase(repositories)
        val project = event.getData(CommonDataKeys.PROJECT)
        val notifier = Notifier(project)
        val addDependencyUseCase = AddDependencyUseCaseFactory.create(project, notifier)
        val intellijTools = ToolsFactory.intellijTools(project)
        val presenter = OkGradleDialogPresenter(searchUseCase, addDependencyUseCase, intellijTools)

        val dialog = OkGradleDialog(presenter)
        dialog.show()
    }
}