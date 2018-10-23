package me.scana.okgradle.data.json

import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import me.scana.okgradle.data.repository.Spellcheck
import me.scana.okgradle.data.repository.SpellcheckDeserializer
import org.junit.Assert.assertEquals
import org.junit.Test
import java.lang.reflect.Type

@Suppress("MemberVisibilityCanPrivate")
class SpellcheckDeserializerTest {

    val deserializer = SpellcheckDeserializer()
    val context = object : JsonDeserializationContext {
        override fun <T : Any?> deserialize(p0: JsonElement?, p1: Type?): T {
            return Gson().fromJson(p0, p1)
        }
    }

    @Test
    fun deserializes() {
        var json = """
            {
                 "suggestions": []
            }
            """.toJson()
        var result = deserializer.deserialize(json, Spellcheck::class.java, context)

        assertEquals(0, result.suggestions.size)

        json = """
            {
                "suggestions": [
                    "retrotif",
                    {
                      "suggestion": [
                        "retrofit",
                        "maybe_retrofit"
                      ]
                    },
                    "plz",
                    {
                      "suggestion": [
                        "please",
                        "por favor"
                      ]
                    }
                  ]
            }
            """.toJson()
        result = deserializer.deserialize(json, Spellcheck::class.java, context)

        assertEquals(2, result.suggestions.size)
        assertEquals(listOf("retrofit", "maybe_retrofit"), result.suggestions[0].suggestion)
        assertEquals(listOf("please", "por favor"), result.suggestions[1].suggestion)
    }


    private fun String.toJson(): JsonElement {
        return JsonParser().parse(this)
    }

}