package me.scana.okgradle.data

import com.google.gson.GsonBuilder
import me.scana.okgradle.data.repository.*
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.test.assertNull
import kotlin.test.assertTrue


@Suppress("MemberVisibilityCanPrivate")
class BintrayRepositoryTest {

    val mockOkHttpClient = MockOkHttpClient()
    val networkClient = NetworkClient(mockOkHttpClient.instance())
    val gson = GsonBuilder().create()
    val repository = BintrayRepository(networkClient, gson)

    @Test
    fun `builds proper query`() {
        repository.search("my_awesome_query").blockingGet()

        val request = mockOkHttpClient.recentRequest()
        assertEquals(
                "GET",
                request?.method
        )
        assertEquals(
                "https://api.bintray.com/search/packages/maven?a=*my_awesome_query*&repo=jcenter&repo=bintray",
                request?.url?.toString()
        )
    }

    @Test
    fun `returns empty result on empty query`() {
        val result = repository.search("").blockingGet() as SearchResult.Success
        assertNull(result.suggestion)
        assertTrue(result.artifacts.isEmpty())
    }

    @Test
    fun `searches and parses results - artifact id with version`() {
        mockOkHttpClient.returnsJson(
                """
                    [{
                        "name": "commons-io:commons-io",
                        "repo": "jcenter",
                        "owner": "bintray",
                        "desc": null,
                        "system_ids": [
                            "commons-io:commons-io"
                        ],
                        "versions": [
                            "2.4",
                            "2.3",
                            "2.2",
                            "2.1",
                            "2.0.1",
                            "2.0",
                            "1.4-backport-IO-168",
                            "1.4",
                            "1.3.2",
                            "1.3.1",
                            "1.3",
                            "1.2",
                            "1.1",
                            "1.0",
                            "0.1",
                            "20030203.000550",
                            "2.6",
                            "2.5"
                        ],
                        "latest_version": "2.4"
                    },
                    {
                        "name": "org.carlspring.commons:commons-io",
                        "repo": "jcenter",
                        "owner": "bintray",
                        "desc": null,
                        "system_ids": [
                            "org.carlspring.commons:commons-io"
                        ],
                        "versions": [
                            "1.1",
                            "1.0"
                        ],
                        "latest_version": "1.1"
                    },
                    {
                        "name": "org.clojars.amit:commons-io",
                        "repo": "jcenter",
                        "owner": "bintray",
                        "desc": null,
                        "system_ids": [
                            "org.clojars.amit:commons-io"
                        ],
                        "versions": [
                            "1.4.0"
                        ],
                        "latest_version": "1.4.0"
                    },
                    {
                        "name": "org.kie.commons:kieora-commons-io",
                        "repo": "jcenter",
                        "owner": "bintray",
                        "desc": null,
                        "system_ids": [
                            "org.kie.commons:kieora-commons-io"
                        ],
                        "versions": [
                            "6.0.0.CR3",
                            "6.0.0.Beta4",
                            "6.0.0.Beta5",
                            "6.0.0.CR2",
                            "6.0.0.Beta2",
                            "6.0.0.Beta1",
                            "6.0.0.Alpha9",
                            "6.0.0.CR1",
                            "6.0.0.CR5",
                            "6.0.0.CR4-Pre1",
                            "6.0.0.Beta3",
                            "6.0.0.CR4"
                        ],
                        "latest_version": "6.0.0.CR3"
                    }]
                """.trimIndent()
        )

        val result = repository.search("commons-io").blockingGet() as SearchResult.Success
        assertNull(result.suggestion)
        assertEquals(4, result.artifacts.size)

        val artifact = result.artifacts[0]
        assertEquals("2.4", artifact.version)
        assertEquals("commons-io", artifact.groupId)
        assertEquals("commons-io", artifact.name)
    }

}