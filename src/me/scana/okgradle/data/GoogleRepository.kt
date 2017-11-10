package me.scana.okgradle.data

import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.util.EntityUtils
import javax.xml.stream.XMLInputFactory

class GoogleRepository(val httpClient: HttpClient) : ArtifactRepository {

    private val xmlInputFactory = XMLInputFactory.newInstance()

    override fun search(query: String): SearchResult {
        if (query.isEmpty()) {
            return SearchResult()
        }
        val requestedArtifact = ARTIFACT_NAMES.find { it.contains(query) }
        requestedArtifact?.let {
            val version: String?
            version = try {
                getLatestVersion(requestedArtifact)
            } catch (e: Exception) {
                "+"
            }
            return SearchResult(artifact = "$requestedArtifact:$version")

        }
        return SearchResult()
    }

    private fun getLatestVersion(artifactId: String): String {
        val path = artifactId
                .replace('.', '/')
                .replace(':', '/')
        val request = HttpGet("$GOOGLE_MAVEN_URL$path/$MAVEN_METADATA")
        val response = httpClient.execute(request)
        val xmlEventReader = xmlInputFactory.createXMLEventReader(response.entity.content)
        var result = ""
        while (xmlEventReader.hasNext()) {
            val event = xmlEventReader.nextEvent()
            if (event.isStartElement && event.asStartElement().name.localPart == MAVEN_METADATA_VERSION) {
                result = xmlEventReader.elementText
                break
            }
        }
        EntityUtils.consumeQuietly(response.entity)
        return result
    }

    companion object {
        const val GOOGLE_MAVEN_URL = "https://dl.google.com/dl/android/maven2/"

        const val MAVEN_METADATA = "maven-metadata.xml"
        const val MAVEN_METADATA_VERSION = "release"

        val ARTIFACT_NAMES = listOf(
                "com.android.support:support-compat",
                "com.android.support:support-core-utils",
                "com.android.support:support-core-ui",
                "com.android.support:support-media-compat",
                "com.android.support:support-fragment",
                "com.android.support:multidex",
                "com.android.support:appcompat-v7",
                "com.android.support:cardview-v7",
                "com.android.support:gridlayout-v7",
                "com.android.support:mediarouter-v7",
                "com.android.support:palette-v7",
                "com.android.support:recyclerview-v7",
                "com.android.support:preference-v7",
                "com.android.support:support-v13",
                "com.android.support:preference-v14",
                "com.android.support:preference-leanback-v17",
                "com.android.support:leanback-v17",
                "com.android.support:support-vector-drawable",
                "com.android.support:animated-vector-drawable",
                "com.android.support:support-annotations",
                "com.android.support:design",
                "com.android.support:customtabs",
                "com.android.support:percent",
                "com.android.support:exifinterface",
                "com.android.support:recommendation",
                "com.android.support:wear",
                "com.google.android.gms:play-services-plus",
                "com.google.android.gms:play-services-auth",
                "com.google.android.gms:play-services-base",
                "com.google.android.gms:play-services-identity",
                "com.google.android.gms:play-services-analytics",
                "com.google.android.gms:play-services-awareness",
                "com.google.android.gms:play-services-cast",
                "com.google.android.gms:play-services-gcm",
                "com.google.android.gms:play-services-drive",
                "com.google.android.gms:play-services-fitness",
                "com.google.android.gms:play-services-location",
                "com.google.android.gms:play-services-maps",
                "com.google.android.gms:play-services-ads",
                "com.google.android.gms:play-services-places",
                "com.google.android.gms:play-services-vision",
                "com.google.android.gms:play-services-nearby",
                "com.google.android.gms:play-services-panorama",
                "com.google.android.gms:play-services-games",
                "com.google.android.gms:play-services-safetynet",
                "com.google.android.gms:play-services-wallet",
                "com.google.android.gms:play-services-wearable"
        )
    }

}
