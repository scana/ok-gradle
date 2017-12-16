package me.scana.okgradle.data

import com.google.gson.GsonBuilder
import kotlinx.coroutines.experimental.runBlocking
import me.scana.okgradle.data.json.SpellcheckDeserializer
import me.scana.okgradle.data.util.TestHttpClient
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test

class JitPackRepositoryTest {

    private val httpClient = TestHttpClient()
    private val gson = GsonBuilder()
            .registerTypeAdapter(Spellcheck::class.java, SpellcheckDeserializer())
            .create()

    private val repository = JitPackRepository(httpClient, gson)

    @Test
    fun `searches and parses results`() {
        httpClient.returnsJson(
                """{
                    "com.andreabaccega:android-form-edittext" : [ "1.3.4", "1.3.3" ],
                    "com.github.alamops:materialedittext" : [ "2.1.5" ],
                    "com.github.alfredlibrary:text" : [ ],
                    "com.github.anshulagarwal06:passwordedittext" : [ "v1.0" ],
                    "com.github.apache:commons-text" : [ "commons-text-1.1" ],
                    "com.github.Beni84:passwordedittext" : [ "0.1.0" ],
                    "com.github.BlackBoxVision:datetimepicker-edittext" : [ "v0.3.3", "v0.3.2", "v0.3.1", "v0.3.0", "v0.2.0", "v0.1.0", "v0.0.2", "v0.0.1" ],
                    "com.github.blackcat27:currencyedittext" : [ "2.0.1", "v1.4.4" ],
                    "com.github.Cielsk:clearable-edittext" : [ "0.0.3", "0.0.2", "v0.0.1-alpha03" ],
                    "com.github.DarrenWorks:havemaxbytesedittext" : [ "test0.1" ]
                    }
                """)
        runBlocking {
            val result = repository.search("text")
            assertEquals("com.andreabaccega:android-form-edittext:1.3.4", result.artifact)
        }
    }

    @Test
    fun `returns empty result on empty query`() {
        runBlocking {
            val result = repository.search("")
            Assert.assertNull(result.artifact)
            Assert.assertNull(result.suggestion)
            Assert.assertNull(result.error)
        }
    }
}