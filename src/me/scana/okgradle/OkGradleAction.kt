package me.scana.okgradle

import com.google.gson.GsonBuilder
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import me.scana.okgradle.data.GoogleRepository
import me.scana.okgradle.data.MavenRepository
import me.scana.okgradle.data.SearchArtifactInteractor
import me.scana.okgradle.data.Spellcheck
import me.scana.okgradle.data.json.SpellcheckDeserializer
import org.apache.http.impl.client.HttpClientBuilder

class OkGradleAction : AnAction() {

    override fun actionPerformed(event: AnActionEvent) {
        val httpClient = HttpClientBuilder.create().build()
        val gson = GsonBuilder()
                .registerTypeAdapter(Spellcheck::class.java, SpellcheckDeserializer())
                .create()
        val interactor =
                SearchArtifactInteractor(
                        listOf(
                                GoogleRepository(httpClient),
                                MavenRepository(httpClient, gson)
                        )
                )
        val dialog = OkGradleDialog(interactor)
        dialog.show()
    }
}
