package me.scana.okgradle.data.repository

data class MavenResult(val responseHeader: ResponseHeader, val response: Response, val spellcheck: Spellcheck)

data class ResponseHeader(val params: Params)
data class Params(val spellcheck: Boolean)

data class Response(val docs: List<Doc>)
data class Doc(val g: String, val a: String, val latestVersion: String)

data class Spellcheck(val suggestions: List<SpellcheckSuggestion>)
data class SpellcheckSuggestion(val suggestion: List<String>)