package me.scana.okgradle.data.repository

data class Artifact(val groupId: String, val name: String, val version: String) {
    override fun toString(): String {
        return "$groupId:$name:$version"
    }
}