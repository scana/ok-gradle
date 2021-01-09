package me.scana.okgradle.data

import com.android.SdkConstants.DOT_GRADLE
import com.android.SdkConstants.DOT_KTS
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.psi.codeStyle.CodeStyleManager
import me.scana.okgradle.data.repository.Artifact
import me.scana.okgradle.internal.dsl.api.GradleBuildModel
import me.scana.okgradle.internal.dsl.api.ProjectBuildModel
import me.scana.okgradle.internal.dsl.api.dependencies.ArtifactDependencySpec
import me.scana.okgradle.internal.dsl.api.dependencies.CommonConfigurationNames
import me.scana.okgradle.internal.dsl.api.dependencies.DependenciesModel
import org.jetbrains.kotlin.psi.KtPsiFactory


private const val ANNOTATION_PROCESSOR = "annotationProcessor"
private const val KAPT = "kapt"
private const val KAPT_PLUGIN = "kotlin-kapt"

interface AddDependencyStrategy {
    fun add(): List<String>
}

object CopyDependencyStrategy {
    fun getDependencyStatements(artifact: Artifact): List<String> {
        val dependencySpec = ArtifactDependencySpec.create(artifact.name, artifact.groupId, artifact.version)
        if (dependencySpec.hasAnnotationProcessor()) {
            val result = mutableListOf("${CommonConfigurationNames.IMPLEMENTATION} '${dependencySpec.compactNotation()}'")
            val compilerName = dependencySpec.annotationProcessorName()
            compilerName?.let {
                val annotationProcessorSpec = ArtifactDependencySpec.create(it, dependencySpec.group, dependencySpec.version)
                result.add("$ANNOTATION_PROCESSOR '${annotationProcessorSpec.compactNotation()}'")
            }
            return result
        }
        return listOf("${CommonConfigurationNames.IMPLEMENTATION} '${dependencySpec.compactNotation()}'")
    }
}

object AddDependencyStrategyFactory {

    fun create(project: Project, gradleFile: VirtualFile, artifact: Artifact): AddDependencyStrategy {
        return if (GradleUtilCompat.isKtsFile(gradleFile)) {
            GradleKtsAddDependencyStrategy(project, gradleFile, artifact)
        } else {
            GradleAddDependencyStrategy(project, gradleFile, artifact)
        }
    }
}

class GradleAddDependencyStrategy(
        private val project: Project,
        private val gradleFile: VirtualFile,
        private val artifact: Artifact
) : AddDependencyStrategy {

    override fun add(): List<String> {
        val gradleBuildModel = ProjectBuildModel.get(project).getModuleBuildModel(gradleFile)
        val dependencies = gradleBuildModel.dependencies()
        val dependencySpec = ArtifactDependencySpec.create(artifact.name, artifact.groupId, artifact.version)
        val result = mutableListOf<String>()
        result.add(dependencySpec.compactNotation())
        if (dependencySpec.hasAnnotationProcessor()) {
            dependencies.addArtifactCompat(CommonConfigurationNames.IMPLEMENTATION, dependencySpec)
            val compilerName = dependencySpec.annotationProcessorName()
            compilerName?.let {
                val annotationProcessorSpec = ArtifactDependencySpec.create(it, dependencySpec.group, dependencySpec.version)
                val configurationName = if (gradleBuildModel.usesKotlinKapt) {
                    KAPT
                } else {
                    ANNOTATION_PROCESSOR
                }
                dependencies.addArtifactCompat(configurationName, annotationProcessorSpec)
                result.add(annotationProcessorSpec.compactNotation())
            }
        } else {
            dependencies.addArtifactCompat(CommonConfigurationNames.IMPLEMENTATION, dependencySpec)
        }
        gradleBuildModel.applyChanges()
        val psiFile = PsiManager.getInstance(project).findFile(gradleFile)
        psiFile?.let {
            CodeStyleManager.getInstance(project).adjustLineIndent(it, 0)
        }
        return result
    }
}

class GradleKtsAddDependencyStrategy(
        private val project: Project,
        private val gradleFile: VirtualFile,
        private val artifact: Artifact
) : AddDependencyStrategy {
    override fun add(): List<String> {
        val psiFile = PsiManager.getInstance(project).findFile(gradleFile)
        val kotlinDependenciesPsi = psiFile?.children
                ?.mapNotNull { it.children.getOrNull(0) }
                ?.flatMap { it.children.toList() }
                ?.mapNotNull { it.children.getOrNull(0)?.children?.getOrNull(0) }
                ?.find { it.text == "dependencies" }

        val artifactId = "${artifact.groupId}:${artifact.name}:${artifact.version}"
        val expression = "${CommonConfigurationNames.IMPLEMENTATION}(\"$artifactId\")"
        kotlinDependenciesPsi?.let {
            val psiFactory = KtPsiFactory(project, false)
            val block = psiFactory.createExpression(expression)
            val dependenciesBlock = it.parent.children[1].children[0].children[0].children[0]
            dependenciesBlock.add(psiFactory.createNewLine())
            dependenciesBlock.add(block)
            psiFile.subtreeChanged()
            gradleFile.refresh(true, false)
        }
        return listOf(expression)
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

private val GradleBuildModel.usesKotlinKapt: Boolean
    get() = plugins().any { it.name().forceString() == KAPT_PLUGIN }

private object GradleUtilCompat {

    fun isKtsFile(file: VirtualFile?): Boolean {
        if (file == null) {
            return false
        }
        val result: HashSet<String> = HashSet()
        addBuildFileType(result, file)
        return result.contains(DOT_KTS)
    }

    private fun addBuildFileType(result: HashSet<String>, buildFile: VirtualFile?) {
        if (buildFile != null) {
            var buildFileExtension = buildFile.extension ?: return
            buildFileExtension = ".$buildFileExtension"
            if (buildFileExtension.equals(DOT_GRADLE, ignoreCase = true)) {
                result.add(DOT_GRADLE)
            } else if (buildFileExtension.equals(DOT_KTS, ignoreCase = true)) {
                result.add(DOT_KTS)
            }
        }
    }
}
