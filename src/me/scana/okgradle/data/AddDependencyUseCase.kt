package me.scana.okgradle.data

import com.android.SdkConstants
import com.android.tools.idea.gradle.dsl.api.ProjectBuildModel
import com.android.tools.idea.gradle.dsl.api.dependencies.ArtifactDependencySpec
import com.android.tools.idea.gradle.util.GradleUtil
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.util.ui.TextTransferable
import me.scana.okgradle.data.repository.Artifact
import me.scana.okgradle.util.Notifier

class AddDependencyUseCase(
        private val project: Project,
        private val notifier: Notifier
) {

    fun addDependency(module: Module, artifact: Artifact) {
        val buildGradleFile = findGradleFile(module)
        buildGradleFile?.let { gradleFile ->
            val gradleBuildModel = ProjectBuildModel.get(project).getModuleBuildModel(gradleFile)
            val dependencies = gradleBuildModel.dependencies()
            val dependencySpec = ArtifactDependencySpec.create(artifact.name, artifact.groupId, artifact.version)
            val dependencyStrategy = AddDependencyStrategyFactory().create(dependencySpec)
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
        CopyPasteManager.getInstance().setContents(TextTransferable(dependencyStrategy.getDependencyStatements(dependencySpec).joinToString("\n") as CharSequence))
        notifier.showDependenciesStatementCopiedMessage()
    }

    private fun findGradleFile(module: Module): VirtualFile? {
        val buildGradleFile = GradleUtil.getGradleBuildFile(module)
        return buildGradleFile ?: module.moduleFile?.parent?.findChild(SdkConstants.FN_BUILD_GRADLE_KTS)
    }

}
