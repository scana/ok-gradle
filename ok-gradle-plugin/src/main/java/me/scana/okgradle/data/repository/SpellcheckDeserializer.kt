package me.scana.okgradle.data.repository

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class SpellcheckDeserializer : JsonDeserializer<Spellcheck> {
    override fun deserialize(json: JsonElement, p1: Type?, p2: JsonDeserializationContext): Spellcheck {
        val suggestions = json.asJsonObject["suggestions"].asJsonArray
        if (suggestions.size() == 0) {
            return Spellcheck(emptyList())
        }
        val actualSuggestions =
                suggestions
                        .filter { it.isJsonObject }
                        .map { p2.deserialize<SpellcheckSuggestion>(it, SpellcheckSuggestion::class.java) }
                        .toList()
        return Spellcheck(actualSuggestions)
    }
}