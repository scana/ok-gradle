package me.scana.okgradle.data

import com.android.tools.idea.gradle.dsl.api.dependencies.ArtifactDependencySpec
import com.android.tools.idea.gradle.dsl.api.dependencies.CommonConfigurationNames
import com.android.tools.idea.gradle.dsl.api.dependencies.DependenciesModel

interface AddDependencyStrategy {
    fun addDependency(dependencySpec: ArtifactDependencySpec, model: DependenciesModel): List<String>
    fun getDependencyStatements(dependencySpec: ArtifactDependencySpec): List<String>
}

class AddDependencyStrategyFactory(private val processor: String = JAVA_ANNOTATION_PROCESSOR) {

    fun create(dependencySpec: ArtifactDependencySpec) = when {
        dependencySpec.hasAnnotationProcessor() -> AnnotationProcessorDependencyStrategy(processor)
        else -> RegularAddDependencyStrategy()
    }
}

class RegularAddDependencyStrategy : AddDependencyStrategy {
    override fun addDependency(dependencySpec: ArtifactDependencySpec, model: DependenciesModel): List<String> {
        model.addArtifactCompat(CommonConfigurationNames.IMPLEMENTATION, dependencySpec)
        return listOf(dependencySpec.compactNotation())
    }

    override fun getDependencyStatements(dependencySpec: ArtifactDependencySpec): List<String> {
        return listOf("${CommonConfigurationNames.IMPLEMENTATION} '${dependencySpec.compactNotation()}'")
    }
}
class AnnotationProcessorDependencyStrategy(private val processor: String) : AddDependencyStrategy {
    override fun addDependency(dependencySpec: ArtifactDependencySpec, model: DependenciesModel): List<String> {
        val result = mutableListOf<String>()
        model.addArtifactCompat(CommonConfigurationNames.IMPLEMENTATION, dependencySpec)
        result.add(dependencySpec.compactNotation())
        val compilerName = dependencySpec.annotationProcessorName()
        compilerName?.let {
            val annotationProcessorSpec = ArtifactDependencySpec.create(it, dependencySpec.group, dependencySpec.version)
            model.addArtifactCompat(processor, annotationProcessorSpec)
            result.add(annotationProcessorSpec.compactNotation())
        }
        return result
    }

    override fun getDependencyStatements(dependencySpec: ArtifactDependencySpec): List<String> {
        val result = mutableListOf("${CommonConfigurationNames.IMPLEMENTATION} '${dependencySpec.compactNotation()}'")
        val compilerName = dependencySpec.annotationProcessorName()
        compilerName?.let {
            val annotationProcessorSpec = ArtifactDependencySpec.create(it, dependencySpec.group, dependencySpec.version)
            result.add("$processor '${annotationProcessorSpec.compactNotation()}'")
        }
        return result
    }
}

private fun ArtifactDependencySpec.hasAnnotationProcessor(): Boolean = "$group:$name" in ARTIFACTS_WITH_ANNOTATION_PROCESSORS

private fun ArtifactDependencySpec.annotationProcessorName(): String? = ARTIFACTS_WITH_ANNOTATION_PROCESSORS["$group:$name"]

private val ARTIFACTS_WITH_ANNOTATION_PROCESSORS = mapOf(
        "com.google.dagger:dagger" to "dagger-compiler",
        "com.jakewharton:butterknife" to "butterknife-compiler",
        "com.google.auto.value:auto-value" to "auto-value"
)

private fun DependenciesModel.addArtifactCompat(configurationName: String, dependencySpec: ArtifactDependencySpec) {
    try {
        addArtifact(configurationName, dependencySpec)
    } catch (e: NoSuchMethodError) {
        val method = DependenciesModel::class.java.declaredMethods
                .filter { it.name == "addArtifact" }
                .filter { it.parameterCount == 2 }
                .firstOrNull { it.parameterTypes.contains(String::class.java) && it.parameterTypes.contains(ArtifactDependencySpec::class.java) }
        method?.invoke(this, configurationName, dependencySpec)
    }
}
