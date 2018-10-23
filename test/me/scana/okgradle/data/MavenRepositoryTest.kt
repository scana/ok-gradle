package me.scana.okgradle.data

import com.google.gson.GsonBuilder
import me.scana.okgradle.data.repository.*
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.test.assertNull
import kotlin.test.assertTrue


@Suppress("MemberVisibilityCanPrivate")
class MavenRepositoryTest {

    val mockOkHttpClient = MockOkHttpClient()

    val networkClient = NetworkClient(mockOkHttpClient.instance())

    val gson = GsonBuilder()
            .registerTypeAdapter(Spellcheck::class.java, SpellcheckDeserializer())
            .create()

    val repository = MavenRepository(networkClient, gson)

    @Test
    fun `builds proper query`() {
        repository.search("my_awesome_query").blockingGet()

        val request = mockOkHttpClient.recentRequest()
        assertEquals(
                "GET",
                request?.method()
        )
        assertEquals(
                "http://search.maven.org/solrsearch/select?q=my_awesome_query",
                request?.url()?.toString()
        )
    }

    @Test
    fun `returns empty result on empty query`() {
        val result = repository.search("").blockingGet() as SearchResult.Success
        assertNull(result.suggestion)
        assertTrue(result.artifacts.isEmpty())
    }

    @Test
    fun `searches and parses results - suggestions`() {
        mockOkHttpClient.returnsJson(
                """{
                  "responseHeader": {
                    "status": 0,
                    "QTime": 2,
                    "params": {
                      "spellcheck": "true",
                      "fl": "id,g,a,latestVersion,p,ec,repositoryId,text,timestamp,versionCount",
                      "sort": "score desc,timestamp desc,g asc,a asc",
                      "indent": "off",
                      "q": "retrfit",
                      "qf": "text^20 g^5 a^10",
                      "spellcheck.count": "5",
                      "wt": "json",
                      "version": "2.2",
                      "defType": "dismax"
                    }
                  },
                  "response": {
                    "numFound": 0,
                    "start": 0,
                    "docs": []
                  },
                  "spellcheck": {
                    "suggestions": [
                      "retrfit",
                      {
                        "numFound": 5,
                        "startOffset": 0,
                        "endOffset": 7,
                        "suggestion": [
                          "retrofit",
                          "jretrofit",
                          "retrofitex",
                          "retrofitor",
                          "retrying"
                        ]
                      }
                    ]
                  }
                }
                """)

        val result = repository.search("retrfit").blockingGet() as SearchResult.Success
        assertTrue(result.artifacts.isEmpty())
        assertEquals("retrofit", result.suggestion)
    }

    @Test
    fun `searches and parses results - artifact id with version`() {
        mockOkHttpClient.returnsJson(
                """{
                      "responseHeader": {
                        "status": 0,
                        "QTime": 1,
                        "params": {
                          "spellcheck": "true",
                          "fl": "id,g,a,latestVersion,p,ec,repositoryId,text,timestamp,versionCount",
                          "sort": "score desc,timestamp desc,g asc,a asc",
                          "indent": "off",
                          "q": "retrofit",
                          "qf": "text^20 g^5 a^10",
                          "spellcheck.count": "5",
                          "wt": "json",
                          "version": "2.2",
                          "defType": "dismax"
                        }
                      },
                      "response": {
                        "numFound": 90,
                        "start": 0,
                        "docs": [
                          {
                            "id": "com.squareup.retrofit2:retrofit",
                            "g": "com.squareup.retrofit2",
                            "a": "retrofit",
                            "latestVersion": "2.3.0",
                            "repositoryId": "central",
                            "p": "jar",
                            "timestamp": 1494719326000,
                            "versionCount": 8,
                            "text": [
                              "com.squareup.retrofit2",
                              "retrofit",
                              "-javadoc.jar",
                              "-sources.jar",
                              ".jar",
                              ".pom"
                            ],
                            "ec": [
                              "-javadoc.jar",
                              "-sources.jar",
                              ".jar",
                              ".pom"
                            ]
                          },
                          {
                            "id": "com.squareup.retrofit:retrofit",
                            "g": "com.squareup.retrofit",
                            "a": "retrofit",
                            "latestVersion": "2.0.0-beta2",
                            "repositoryId": "central",
                            "p": "jar",
                            "timestamp": 1443453385000,
                            "versionCount": 21,
                            "text": [
                              "com.squareup.retrofit",
                              "retrofit",
                              "-sources.jar",
                              "-javadoc.jar",
                              ".jar",
                              ".pom"
                            ],
                            "ec": [
                              "-sources.jar",
                              "-javadoc.jar",
                              ".jar",
                              ".pom"
                            ]
                          },
                          {
                            "id": "com.hannesdorfmann.mosby:retrofit",
                            "g": "com.hannesdorfmann.mosby",
                            "a": "retrofit",
                            "latestVersion": "1.3.1",
                            "repositoryId": "central",
                            "p": "aar",
                            "timestamp": 1441109774000,
                            "versionCount": 6,
                            "text": [
                              "com.hannesdorfmann.mosby",
                              "retrofit",
                              "-sources.jar",
                              "-javadoc.jar",
                              ".aar",
                              ".pom"
                            ],
                            "ec": [
                              "-sources.jar",
                              "-javadoc.jar",
                              ".aar",
                              ".pom"
                            ]
                          },
                          {
                            "id": "com.infstory:retrofit",
                            "g": "com.infstory",
                            "a": "retrofit",
                            "latestVersion": "2.0.0",
                            "repositoryId": "central",
                            "p": "jar",
                            "timestamp": 1438367606000,
                            "versionCount": 1,
                            "text": [
                              "com.infstory",
                              "retrofit",
                              "-sources.jar",
                              "-javadoc.jar",
                              ".jar",
                              ".pom"
                            ],
                            "ec": [
                              "-sources.jar",
                              "-javadoc.jar",
                              ".jar",
                              ".pom"
                            ]
                          }
                        ]
                      },
                      "spellcheck": {
                        "suggestions": []
                      }
                    }
                """
        )

        val result = repository.search("retrfit").blockingGet() as SearchResult.Success
        assertNull(result.suggestion)
        assertEquals(4, result.artifacts.size)

        val artifact = result.artifacts[1]
        assertEquals("2.0.0-beta2", artifact.version)
        assertEquals("com.squareup.retrofit", artifact.groupId)
        assertEquals("retrofit", artifact.name)
    }

}