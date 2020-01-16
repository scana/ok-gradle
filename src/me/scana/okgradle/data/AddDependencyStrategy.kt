package me.scana.okgradle.data

import me.scana.okgradle.internal.dsl.api.dependencies.ArtifactDependencySpec
import me.scana.okgradle.internal.dsl.api.dependencies.CommonConfigurationNames
import me.scana.okgradle.internal.dsl.api.dependencies.DependenciesModel


private const val ANNOTATION_PROCESSOR = "annotationProcessor"
private const val KAPT = "kapt"

interface AddDependencyStrategy {
    fun addDependency(dependencySpec: ArtifactDependencySpec, model: DependenciesModel): List<String>
    fun getDependencyStatements(dependencySpec: ArtifactDependencySpec): List<String>
}

object AddDependencyStrategyFactory {

    fun create(dependencySpec: ArtifactDependencySpec, withKotlinKaptSupport: Boolean) = when {
        dependencySpec.hasAnnotationProcessor() -> AnnotationProcessorDependencyStrategy(withKotlinKaptSupport)
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
class AnnotationProcessorDependencyStrategy(private val usesKotlinKapt: Boolean) : AddDependencyStrategy {
    override fun addDependency(dependencySpec: ArtifactDependencySpec, model: DependenciesModel): List<String> {
        val result = mutableListOf<String>()
        model.addArtifactCompat(CommonConfigurationNames.IMPLEMENTATION, dependencySpec)
        result.add(dependencySpec.compactNotation())
        val compilerName = dependencySpec.annotationProcessorName()
        compilerName?.let {
            val annotationProcessorSpec = ArtifactDependencySpec.create(it, dependencySpec.group, dependencySpec.version)
            val configurationName = if (usesKotlinKapt) {
                KAPT
            } else {
                ANNOTATION_PROCESSOR
            }
            model.addArtifactCompat(configurationName, annotationProcessorSpec)
            result.add(annotationProcessorSpec.compactNotation())
        }
        return result
    }

    override fun getDependencyStatements(dependencySpec: ArtifactDependencySpec): List<String> {
        val result = mutableListOf("${CommonConfigurationNames.IMPLEMENTATION} '${dependencySpec.compactNotation()}'")
        val compilerName = dependencySpec.annotationProcessorName()
        compilerName?.let {
            val annotationProcessorSpec = ArtifactDependencySpec.create(it, dependencySpec.group, dependencySpec.version)
            result.add("$ANNOTATION_PROCESSOR '${annotationProcessorSpec.compactNotation()}'")
        }
        return result
    }
}

private fun ArtifactDependencySpec.hasAnnotationProcessor(): Boolean {
    return "$group:$name" in ARTIFACTS_WITH_ANNOTATION_PROCESSORS
}

private fun ArtifactDependencySpec.annotationProcessorName(): String? {
    return ARTIFACTS_WITH_ANNOTATION_PROCESSORS["$group:$name"]
}

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
