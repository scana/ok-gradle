package me.scana.okgradle.data

import com.google.gson.GsonBuilder
import kotlinx.coroutines.experimental.runBlocking
import me.scana.okgradle.data.json.SpellcheckDeserializer
import me.scana.okgradle.data.util.TestHttpClient
import org.junit.Assert.*
import org.junit.Test
import java.io.IOException


@Suppress("MemberVisibilityCanPrivate")
class MavenRepositoryTest {

    val httpClient = TestHttpClient()
    val gson = GsonBuilder()
            .registerTypeAdapter(Spellcheck::class.java, SpellcheckDeserializer())
            .create()
    val repository = MavenRepository(httpClient, gson)

    @Test
    fun `builds proper query`() {
        runBlocking {
            repository.search("my_awesome_query")
            assertEquals(
                    "GET",
                    httpClient.recentRequest?.method
            )
            assertEquals(
                    "http://search.maven.org/solrsearch/select?q=my_awesome_query",
                    httpClient.recentRequest?.uri.toString()
            )
        }
    }

    @Test
    fun `handles network exceptions`() {
        httpClient.throwExceptionOnRequest(IOException())
        runBlocking {
            val result = repository.search("something")
            assertNull(result.artifact)
            assertNull(result.suggestion)
            assertTrue(result.error is IOException)
        }
    }

    @Test
    fun `handles invalid http status codes`() {
        httpClient.returnBadRequestStatus()
        runBlocking {
            val result = repository.search("something")
            assertNull(result.artifact)
            assertNull(result.suggestion)
            assertNotNull(result.error)
            assertEquals(
                    "Could not acquire results (400)",
                    result.error?.message
            )
        }
    }

    @Test
    fun `returns empty result on empty query`() {
        runBlocking {
            val result = repository.search("")
            assertNull(result.artifact)
            assertNull(result.suggestion)
            assertNull(result.error)
        }
    }

    @Test
    fun `searches and parses results - suggestions`() {
        httpClient.returnsJson(
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
        runBlocking {
            val result = repository.search("retrfit")
            assertNull(result.error)
            assertNull(result.artifact)
            assertEquals("retrofit", result.suggestion)
        }
    }

    @Test
    fun `searches and parses results - artifact id with version`() {
        httpClient.returnsJson(
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
        runBlocking {
            val result = repository.search("retrofit")
            assertNull(result.error)
            assertNull(result.suggestion)
            assertEquals("com.squareup.retrofit2:retrofit:2.3.0", result.artifact)
        }
    }

}