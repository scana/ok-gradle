package me.scana.okgradle.data

import com.android.tools.idea.gradle.util.GradleUtil
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.command.impl.DummyProject
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.util.ui.TextTransferable
import me.scana.okgradle.data.repository.Artifact
import me.scana.okgradle.util.Notifier

object AddDependencyUseCaseFactory {
    fun create(project: Project?, notifier: Notifier): AddDependencyUseCase {
        return if (project != null) {
            AddDependencyUseCaseImpl(project, notifier)
        } else {
            val copyImpl = AddDependencyUseCaseImpl(DummyProject.getInstance(), notifier)
            CopyOnlyDependencyUseCase(copyImpl)
        }
    }
}

interface AddDependencyUseCase {
    fun addDependency(module: Module, artifact: Artifact)
    fun copyToClipboard(artifact: Artifact)
}

private const val FN_BUILD_GRADLE_KTS = "build.gradle.kts"

class AddDependencyUseCaseImpl(
        private val project: Project,
        private val notifier: Notifier
) : AddDependencyUseCase {

    override fun addDependency(module: Module, artifact: Artifact) {
        val buildGradleFile = findGradleFile(module) ?: return //TODO: throw an exception here with more info about Module
        val strategy = AddDependencyStrategyFactory.create(project, buildGradleFile, artifact)
        val psiFile = PsiManager.getInstance(project).findFile(buildGradleFile)
        runAddDependencyWriteCommand(psiFile) {
            val addedDependencies = strategy.add()
            notifier.showDependenciesAddedMessage(module.name, addedDependencies)
        }
    }

    override fun copyToClipboard(artifact: Artifact) {
        val dependencyStatements = CopyDependencyStrategy.getDependencyStatements(artifact)
        CopyPasteManager.getInstance().setContents(TextTransferable(dependencyStatements.joinToString("\n") as String?))
        notifier.showDependenciesStatementCopiedMessage()
    }

    private fun findGradleFile(module: Module): VirtualFile? {
        val buildGradleFile = GradleUtil.getGradleBuildFile(module)
        return buildGradleFile ?: module.moduleFile?.parent?.findChild(FN_BUILD_GRADLE_KTS)
    }

    private fun runAddDependencyWriteCommand(psiFile: PsiFile?, command: () -> Unit) {
        WriteCommandAction.runWriteCommandAction(project, "Add dependency", null, Runnable { command() }, psiFile)
    }
}

class CopyOnlyDependencyUseCase(
        private val addDependencyUseCase: AddDependencyUseCase
) : AddDependencyUseCase by addDependencyUseCase {

    override fun addDependency(module: Module, artifact: Artifact) {
        // just a stub
    }
}

