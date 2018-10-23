package me.scana.okgradle.util

import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.module.impl.scopes.ModuleWithDependenciesScope
import com.intellij.openapi.project.Project

class IntellijTools(private val project: Project) {

    fun getModules(): List<Module> {
        return ModuleManager.getInstance(project)
                .modules
                .toList()
                .withSourceOnly()
    }

    private fun List<Module>.withSourceOnly(): List<Module> {
        return this.filter {
            val scope = it.moduleScope
            return@filter scope is ModuleWithDependenciesScope && scope.roots.isNotEmpty()
        }
    }

}
