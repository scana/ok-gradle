package me.scana.okgradle.util

import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.module.impl.scopes.ModuleWithDependenciesScope
import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.idea.util.rootManager
import org.jetbrains.kotlin.idea.util.sourceRoots

object ToolsFactory {
    fun intellijTools(project: Project?): IntellijTools {
        return if (project != null) {
            IntellijToolsImpl(project)
        } else {
           DummyTools()
        }
    }
}

interface IntellijTools {
    fun getModules(): List<Module>
}

class IntellijToolsImpl(private val project: Project) : IntellijTools {

    override fun getModules(): List<Module> {
        return ModuleManager.getInstance(project)
            .modules
            .toList()
            .withSourcesOnly()
            .withGradleFilesOnly()
    }

    private fun List<Module>.withSourcesOnly(): List<Module> {
        return filter { it.sourceRoots.isNotEmpty() }
    }

    private fun List<Module>.withGradleFilesOnly(): List<Module> {
        return filter { module ->
            module.rootManager.contentRoots.any { root ->
                root.children.any { child ->
                    child.name == "build.gradle" || child.name == "build.gradle.kts"
                }
            }
        }
    }
}

class DummyTools : IntellijTools {
    override fun getModules() = emptyList<Module>()
}
