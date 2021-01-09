package me.scana.okgradle.util

object Constants {
    const val BUILD_GRADLE_KTS = "build.gradle.kts"
    const val BUILD_GRADLE = "build.gradle"
    val BUILD_GRADLE_FILES = listOf(
        BUILD_GRADLE, BUILD_GRADLE_KTS
    )
    const val DOT_GRADLE = ".gradle"
    const val DOT_KTS = ".KTS"

    const val GRADLE_PROPERTIES = "gradle.properties"
    const val SETTINGS_GRADLE = "gradle.properties"
    const val GRADLE_PATH_SEPARATOR = ":"

}