package me.scana.okgradle.data

data class MavenResult(val responseHeader: ResponseHeader, val response: Response, val spellcheck: Spellcheck)

data class ResponseHeader(val params: Params)
data class Params(val spellcheck: Boolean)

data class Response(val docs: List<Doc>)
data class Doc(val id: String, val latestVersion: String)

data class Spellcheck(val suggestions: List<SpellcheckSuggestion>)
data class SpellcheckSuggestion(val suggestion: List<String>)

data class JitPackResult(val artifacts: List<Artifact> = listOf())
data class Artifact(val id: String = "", val versions: List<String> = listOf())