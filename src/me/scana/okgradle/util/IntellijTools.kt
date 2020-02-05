package me.scana.okgradle.util

import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.module.impl.scopes.ModuleWithDependenciesScope
import com.intellij.openapi.project.Project

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

class IntellijToolsImpl(private val project: Project) : IntellijTools{

    override fun getModules(): List<Module> {
        return ModuleManager.getInstance(project)
                .modules
                .toList()
                .withSourceOnly()
                .withoutKotlinScriptModules()
    }

    private fun List<Module>.withoutKotlinScriptModules(): List<Module> {
        return this.filter {
            it.moduleFile?.parent?.findChild("build.gradle") != null
        }
    }

    private fun List<Module>.withSourceOnly(): List<Module> {
        return this.filter {
            val scope = it.getModuleWithDependenciesAndLibrariesScope(false)
            return@filter scope is ModuleWithDependenciesScope && scope.roots.isNotEmpty()
        }
    }
}

class DummyTools : IntellijTools {
    override fun getModules() = emptyList<Module>()
}
