package me.scana.okgradle.data

import com.android.tools.idea.gradle.dsl.api.GradleBuildModel
import com.android.tools.idea.gradle.dsl.api.dependencies.ArtifactDependencySpec
import com.android.tools.idea.gradle.util.GradleUtil
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiManager
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.util.ui.TextTransferable
import me.scana.okgradle.data.repository.Artifact
import me.scana.okgradle.util.Notifier

const val JAVA_ANNOTATION_PROCESSOR = "annotationProcessor"
const val KOTLIN_ANNOTATION_PROCESSOR = "kapt"
const val KOTLIN_KAPT_PLUGIN = "kotlin-kapt"

class AddDependencyUseCase(
        private val project: Project,
        private val notifier: Notifier
) {

    fun addDependency(module: Module, artifact: Artifact) {
        val buildGradleFile = GradleUtil.getGradleBuildFile(module)
        buildGradleFile?.let { gradleFile ->
            val gradleBuildModel = GradleBuildModel.parseBuildFile(gradleFile, project, module.name)
            val dependencies = gradleBuildModel.dependencies()
            val dependencySpec = ArtifactDependencySpec.create(artifact.name, artifact.groupId, artifact.version)
            val dependencyStrategy = AddDependencyStrategyFactory(
                    if (hasKotlinKaptSupport(gradleBuildModel)) {
                        KOTLIN_ANNOTATION_PROCESSOR
                    } else {
                        JAVA_ANNOTATION_PROCESSOR
                    }
            ).create(dependencySpec)
            WriteCommandAction.runWriteCommandAction(project) {
                val addedDependencies = dependencyStrategy.addDependency(dependencySpec, dependencies)
                gradleBuildModel.applyChanges()
                val psiFile = PsiManager.getInstance(project).findFile(gradleFile)
                psiFile?.let {
                    CodeStyleManager.getInstance(project).adjustLineIndent(it, 0)
                }
                notifier.showDependenciesAddedMessage(module.name, addedDependencies)
            }
        }
    }

    fun copyToClipboard(artifact: Artifact) {
        val dependencySpec = ArtifactDependencySpec.create(artifact.name, artifact.groupId, artifact.version)
        val dependencyStrategy = AddDependencyStrategyFactory().create(dependencySpec)
        CopyPasteManager.getInstance().setContents(TextTransferable(dependencyStrategy.getDependencyStatements(dependencySpec).joinToString("\n")))
        notifier.showDependenciesStatementCopiedMessage()
    }

    private fun hasKotlinKaptSupport(gradleBuildModel: GradleBuildModel): Boolean =
            gradleBuildModel.appliedPlugins().any {
                it.value() == KOTLIN_KAPT_PLUGIN
            }

}
