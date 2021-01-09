package me.scana.okgradle.util

import com.google.common.base.Splitter
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.rootManager
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import java.io.File
import java.util.stream.Collectors

object AndroidPluginUtils {

    @JvmStatic
    fun getBaseDirPath(project: Project): File {
        if (project.isDefault) return File("")
        val basePath = project.basePath
        return File(FileUtil.toCanonicalPath(basePath))
    }

    @JvmStatic
    fun getGradleBuildFile(module: Module): VirtualFile? {
        return module.rootManager.contentRoots.first().findChild(Constants.BUILD_GRADLE)
    }

    @JvmStatic
    fun getGradleBuildFile(dirPath: File): VirtualFile? {
        val gradleBuildFilePath = getGradleBuildFilePath(dirPath)
        return VfsUtil.findFileByIoFile(gradleBuildFilePath, true)
    }

    @JvmStatic
    fun getGradleBuildFilePath(dirPath: File): File {
        return File(dirPath, Constants.BUILD_GRADLE)
    }

    @JvmStatic
    fun getGradleSettingsFile(dirPath: File): VirtualFile? {
        val gradleSettingsFilePath = getGradleSettingsFilePath(dirPath)
        return VfsUtil.findFileByIoFile(gradleSettingsFilePath, true)
    }

    private fun getGradleSettingsFilePath(dirPath: File): File {
        return File(dirPath, Constants.SETTINGS_GRADLE)
    }

    @JvmStatic
    fun getPathSegments(gradlePath: String): List<String?> {
        return Splitter.on(Constants.GRADLE_PATH_SEPARATOR).omitEmptyStrings().splitToList(gradlePath)
    }

    @JvmStatic
    fun <T> join(list: Collection<T>, delimiter: String?): String? {
        return list.stream().map { obj: T -> obj.toString() }.collect(Collectors.joining(delimiter)) as String
    }

}