package me.scana.okgradle.data

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import me.scana.okgradle.data.repository.*
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class JitPackRepositoryTest {

    val mockOkHttpClient = MockOkHttpClient()

    val networkClient = NetworkClient(mockOkHttpClient.instance())

    val gson: Gson = GsonBuilder()
            .registerTypeAdapter(Spellcheck::class.java, SpellcheckDeserializer())
            .create()

    val repository = JitPackRepository(networkClient, gson)

    @Test
    fun `searches and parses results`() {
        mockOkHttpClient.returnsJson(
                """{
                    "com.andreabaccega:android-form-edittext" : [ "1.3.4", "1.3.3" ],
                    "com.github.alamops:materialedittext" : [ "2.1.5" ],
                    "com.github.alfredlibrary:text" : [ "1.2" ],
                    "com.github.anshulagarwal06:passwordedittext" : [ "v1.0" ],
                    "com.github.apache:commons-text" : [ "commons-text-1.1" ],
                    "com.github.Beni84:passwordedittext" : [ "0.1.0" ],
                    "com.github.BlackBoxVision:datetimepicker-edittext" : [ "v0.3.3", "v0.3.2", "v0.3.1", "v0.3.0", "v0.2.0", "v0.1.0", "v0.0.2", "v0.0.1" ],
                    "com.github.blackcat27:currencyedittext" : [ "2.0.1", "v1.4.4" ],
                    "com.github.Cielsk:clearable-edittext" : [ "0.0.3", "0.0.2", "v0.0.1-alpha03" ],
                    "com.github.DarrenWorks:havemaxbytesedittext" : [ "test0.1" ]
                    }
                """
        )
        val result = repository.search("query").blockingGet() as SearchResult.Success
        assertNull(result.suggestion)
        assertEquals(10, result.artifacts.size)
        val artifact = result.artifacts[6]
        assertEquals("v0.3.3", artifact.version)
        assertEquals("datetimepicker-edittext", artifact.name)
    }

    @Test
    fun `returns empty result on empty query`() {
        mockOkHttpClient.returnsJson(
                """{
                    "com.andreabaccega:android-form-edittext" : [ "1.3.4", "1.3.3" ],
                    "com.github.alamops:materialedittext" : [ "2.1.5" ],
                    "com.github.alfredlibrary:text" : [ "1.2" ],
                    "com.github.anshulagarwal06:passwordedittext" : [ "v1.0" ],
                    "com.github.apache:commons-text" : [ "commons-text-1.1" ],
                    "com.github.Beni84:passwordedittext" : [ "0.1.0" ],
                    "com.github.BlackBoxVision:datetimepicker-edittext" : [ "v0.3.3", "v0.3.2", "v0.3.1", "v0.3.0", "v0.2.0", "v0.1.0", "v0.0.2", "v0.0.1" ],
                    "com.github.blackcat27:currencyedittext" : [ "2.0.1", "v1.4.4" ],
                    "com.github.Cielsk:clearable-edittext" : [ "0.0.3", "0.0.2", "v0.0.1-alpha03" ],
                    "com.github.DarrenWorks:havemaxbytesedittext" : [ "test0.1" ]
                    }
                """
        )

        val result = repository.search("").blockingGet() as SearchResult.Success
        assertNull(result.suggestion)
        assertTrue(result.artifacts.isEmpty())
    }
}