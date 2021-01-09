package me.scana.okgradle.data

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.command.impl.DummyProject
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.rootManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.util.ui.TextTransferable
import me.scana.okgradle.Copys
import me.scana.okgradle.data.repository.Artifact
import me.scana.okgradle.util.Constants
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
        val buildGradle = module.rootManager.contentRoots.first().findChild(Constants.BUILD_GRADLE)
        val buildGradleKts = module.rootManager.contentRoots.first().findChild(Constants.BUILD_GRADLE_KTS)
        return buildGradle ?: buildGradleKts
    }

    private fun runAddDependencyWriteCommand(psiFile: PsiFile?, command: () -> Unit) {
        WriteCommandAction.runWriteCommandAction(project, Copys.ADD_DEPENDENCY, null, Runnable { command() }, psiFile)
    }
}

class CopyOnlyDependencyUseCase(
        private val addDependencyUseCase: AddDependencyUseCase
) : AddDependencyUseCase by addDependencyUseCase {

    override fun addDependency(module: Module, artifact: Artifact) {
        // just a stub
    }
}

