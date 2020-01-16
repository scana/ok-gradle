package me.scana.okgradle.data.repository

import io.reactivex.Single
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Request
import javax.xml.stream.XMLInputFactory

class GoogleRepository(private val networkClient: NetworkClient) : ArtifactRepository {

    private val xmlInputFactory = XMLInputFactory.newInstance()

    override fun search(query: String): Single<SearchResult> {
        return Single.create {
            val result = when {
                query.isEmpty() -> SearchResult.Success()
                else -> findArtifacts(query)
            }
            it.onSuccess(result)
        }
    }

    private fun findArtifacts(query: String): SearchResult {
        val requestedArtifacts = ARTIFACT_NAMES.filter { it.contains(query) }
        requestedArtifacts.firstOrNull()?.let {
            val version = getLatestVersion(it)
            return SearchResult.Success(
                    requestedArtifacts.map {
                        val (groupId, name) = it.split(":".toRegex(), 2)
                        Artifact(groupId, name, version)
                    }
            )
        }
        return SearchResult.Success()
    }

    private fun getLatestVersion(artifactId: String): String {
        val path = artifactId
                .replace('.', '/')
                .replace(':', '/')

        val url = GOOGLE_MAVEN_URL.newBuilder()
                .addPathSegment(path)
                .addPathSegment(MAVEN_METADATA)
                .build()

        val request = Request.Builder()
                .url(url)
                .build()

        val response = networkClient.execute(request) {
            val xmlEventReader = xmlInputFactory.createXMLEventReader(this.charStream())
            while (xmlEventReader.hasNext()) {
                val event = xmlEventReader.nextEvent()
                if (event.isStartElement && event.asStartElement().name.localPart == MAVEN_METADATA_VERSION) {
                    return@execute xmlEventReader.elementText
                }
            }
            return@execute "+"
        }
        return when(response) {
            is NetworkResult.Failure -> "+"
            is NetworkResult.Success -> response.data
        }
    }

    companion object {
        val GOOGLE_MAVEN_URL = "https://dl.google.com/dl/android/maven2/".toHttpUrl()

        const val MAVEN_METADATA = "maven-metadata.xml"
        const val MAVEN_METADATA_VERSION = "release"

        val ARTIFACT_NAMES = listOf(
                "com.android.support.constraint:constraint-layout-solver",
                "com.android.support.constraint:constraint-layout",
                "com.android.databinding:library",
                "com.android.databinding:adapters",
                "com.android.databinding:compiler",
                "com.android.databinding:compilerCommon",
                "com.android.databinding:baseLibrary",
                "com.android.support:support-compat",
                "com.android.support:leanback-v17",
                "com.android.support:recommendation",
                "com.android.support:support-tv-provider",
                "com.android.support:support-vector-drawable",
                "com.android.support:recyclerview-v7",
                "com.android.support:preference-leanback-v17",
                "com.android.support:preference-v14",
                "com.android.support:percent",
                "com.android.support:support-media-compat",
                "com.android.support:cardview-v7",
                "com.android.support:wearable",
                "com.android.support:exifinterface",
                "com.android.support:support-annotations",
                "com.android.support:appcompat-v7",
                "com.android.support:palette-v7",
                "com.android.support:multidex-instrumentation",
                "com.android.support:multidex",
                "com.android.support:mediarouter-v7",
                "com.android.support:preference-v7",
                "com.android.support:support-dynamic-animation",
                "com.android.support:support-fragment",
                "com.android.support:design",
                "com.android.support:transition",
                "com.android.support:customtabs",
                "com.android.support:support-core-ui",
                "com.android.support:gridlayout-v7",
                "com.android.support:animated-vector-drawable",
                "com.android.support:support-core-utils",
                "com.android.support:support-v13",
                "com.android.support:instantvideo",
                "com.android.support:support-v4",
                "com.android.support:support-emoji",
                "com.android.support:wear",
                "com.android.support:support-emoji-appcompat",
                "com.android.support:support-emoji-bundled",
                "com.android.support:support-content",
                "com.android.support.test:runner",
                "com.android.support.test:rules",
                "com.android.support.test:exposed-instrumentation-api-publish",
                "com.android.support.test:testing-support-lib",
                "com.android.support.test:orchestrator",
                "com.android.support.test:monitor",
                "com.android.support.test.janktesthelper:janktesthelper-v23",
                "com.android.support.test.uiautomator:uiautomator-v18",
                "com.android.support.test.espresso:espresso-core",
                "com.android.support.test.espresso:espresso-web",
                "com.android.support.test.espresso:espresso-intents",
                "com.android.support.test.espresso:espresso-contrib",
                "com.android.support.test.espresso:espresso-idling-resource",
                "com.android.support.test.espresso:espresso-accessibility",
                "android.arch.persistence.room:compiler",
                "android.arch.persistence.room:support-db-impl",
                "android.arch.persistence.room:runtime",
                "android.arch.persistence.room:support-db",
                "android.arch.persistence.room:migration",
                "android.arch.persistence.room:rxjava2",
                "android.arch.persistence.room:testing",
                "android.arch.persistence.room:common",
                "android.arch.persistence.room:db",
                "android.arch.persistence.room:db-impl",
                "android.arch.persistence.room:guava",
                "android.arch.lifecycle:compiler",
                "android.arch.lifecycle:runtime",
                "android.arch.lifecycle:extensions",
                "android.arch.lifecycle:reactivestreams",
                "android.arch.lifecycle:common",
                "android.arch.lifecycle:common-java8",
                "android.arch.lifecycle:viewmodel",
                "android.arch.lifecycle:livedata-core",
                "android.arch.lifecycle:livedata",
                "android.arch.core:core-testing",
                "android.arch.core:core",
                "android.arch.core:runtime",
                "android.arch.core:common",
                "com.google.android.instantapps:instantapps",
                "com.google.android.instantapps.thirdpartycompat:volleycompat",
                "com.android.java.tools.build:java-lib-model",
                "com.android.java.tools.build:java-lib-model-builder",
                "com.android.tools:dvlib",
                "com.android.tools:sdklib",
                "com.android.tools:repository",
                "com.android.tools:annotations",
                "com.android.tools:devicelib",
                "com.android.tools:sdk-common",
                "com.android.tools:testutils",
                "com.android.tools:common",
                "com.android.tools:r8",
                "com.android.tools.layoutlib:layoutlib-api",
                "com.android.tools.ddms:ddmlib",
                "com.android.tools.external.com-intellij:uast",
                "com.android.tools.external.com-intellij:intellij-core",
                "com.android.tools.external.com-intellij:kotlin-compiler",
                "com.android.tools.build:gradle-experimental",
                "com.android.tools.build:manifest-merger",
                "com.android.tools.build:gradle-api",
                "com.android.tools.build:transform-api",
                "com.android.tools.build:apksig",
                "com.android.tools.build:builder-test-api",
                "com.android.tools.build:gradle",
                "com.android.tools.build:builder",
                "com.android.tools.build:builder-model",
                "com.android.tools.build:gradle-core",
                "com.android.tools.build:aapt2-proto",
                "com.android.tools.build:bundletool",
                "com.android.tools.build:apkzlib",
                "com.android.tools.analytics-library:tracker",
                "com.android.tools.analytics-library:protos",
                "com.android.tools.analytics-library:inspector",
                "com.android.tools.analytics-library:shared",
                "com.android.tools.analytics-library:publisher",
                "com.android.tools.internal.build.test:devicepool",
                "com.android.tools.lint:lint-tests",
                "com.android.tools.lint:lint-api",
                "com.android.tools.lint:lint-checks",
                "com.android.tools.lint:lint",
                "com.android.tools.lint:lint-gradle",
                "com.android.tools.lint:lint-gradle-api",
                "com.android.tools.lint:lint-kotlin",
                "com.android.tools.external.org-jetbrains:uast",
                "com.android.support.test.espresso.idling:idling-net",
                "com.android.support.test.espresso.idling:idling-concurrent",
                "com.android.support.test.services:test-services",
                "com.google.firebase:firebase-dynamic-links",
                "com.google.firebase:firebase-crash",
                "com.google.firebase:firebase-ads",
                "com.google.firebase:firebase-analytics",
                "com.google.firebase:firebase-common",
                "com.google.firebase:firebase-auth",
                "com.google.firebase:firebase-appindexing",
                "com.google.firebase:firebase-auth-common",
                "com.google.firebase:firebase-invites",
                "com.google.firebase:firebase-config",
                "com.google.firebase:firebase-analytics-impl",
                "com.google.firebase:firebase-storage",
                "com.google.firebase:firebase-messaging",
                "com.google.firebase:firebase-auth-module",
                "com.google.firebase:firebase-auth-impl",
                "com.google.firebase:firebase-database-connection",
                "com.google.firebase:firebase-storage-common",
                "com.google.firebase:firebase-core",
                "com.google.firebase:firebase-database",
                "com.google.firebase:firebase-perf",
                "com.google.firebase:firebase-iid",
                "com.google.firebase:firebase-iid-license",
                "com.google.firebase:firebase-firestore",
                "com.google.firebase:firebase-database-license",
                "com.google.firebase:firebase-appindexing-license",
                "com.google.firebase:firebase-analytics-impl-license",
                "com.google.firebase:firebase-storage-common-license",
                "com.google.firebase:firebase-analytics-license",
                "com.google.firebase:firebase-storage-license",
                "com.google.firebase:firebase-auth-license",
                "com.google.firebase:firebase-database-connection-license",
                "com.google.firebase:firebase-perf-license",
                "com.google.firebase:firebase-messaging-license",
                "com.google.firebase:firebase-config-license",
                "com.google.firebase:firebase-common-license",
                "com.google.firebase:firebase-dynamic-links-license",
                "com.google.firebase:firebase-crash-license",
                "com.google.firebase:testlab-instr-lib",
                "com.google.android.gms:play-services-vision",
                "com.google.android.gms:play-services-tagmanager",
                "com.google.android.gms:play-services-vision-common",
                "com.google.android.gms:play-services-all-wear",
                "com.google.android.gms:play-services-instantapps",
                "com.google.android.gms:play-services-tasks",
                "com.google.android.gms:play-services-ads-lite",
                "com.google.android.gms:play-services-analytics-impl",
                "com.google.android.gms:play-services-maps",
                "com.google.android.gms:play-services-appindexing",
                "com.google.android.gms:play-services-wearable",
                "com.google.android.gms:play-services-awareness",
                "com.google.android.gms:play-services",
                "com.google.android.gms:play-services-nearby",
                "com.google.android.gms:play-services-basement",
                "com.google.android.gms:play-services-tagmanager-api",
                "com.google.android.gms:play-services-tagmanager-v4-impl",
                "com.google.android.gms:play-services-safetynet",
                "com.google.android.gms:play-services-plus",
                "com.google.android.gms:play-services-base",
                "com.google.android.gms:play-services-iid",
                "com.google.android.gms:play-services-panorama",
                "com.google.android.gms:play-services-contextmanager",
                "com.google.android.gms:play-services-games",
                "com.google.android.gms:play-services-cast",
                "com.google.android.gms:play-services-location",
                "com.google.android.gms:play-services-places",
                "com.google.android.gms:play-services-wallet",
                "com.google.android.gms:play-services-identity",
                "com.google.android.gms:play-services-analytics",
                "com.google.android.gms:play-services-appinvite",
                "com.google.android.gms:play-services-auth-base",
                "com.google.android.gms:play-services-auth-api-phone",
                "com.google.android.gms:play-services-gass",
                "com.google.android.gms:play-services-appstate",
                "com.google.android.gms:play-services-fitness",
                "com.google.android.gms:play-services-drive",
                "com.google.android.gms:play-services-measurement",
                "com.google.android.gms:play-services-ads",
                "com.google.android.gms:play-services-clearcut",
                "com.google.android.gms:play-services-gcm",
                "com.google.android.gms:play-services-oss-licenses",
                "com.google.android.gms:play-services-auth",
                "com.google.android.gms:play-services-cast-framework",
                "com.google.android.gms:play-services-fido",
                "com.google.android.gms:play-services-plus-license",
                "com.google.android.gms:play-services-panorama-license",
                "com.google.android.gms:play-services-auth-base-license",
                "com.google.android.gms:play-services-maps-license",
                "com.google.android.gms:play-services-places-license",
                "com.google.android.gms:play-services-nearby-license",
                "com.google.android.gms:play-services-games-license",
                "com.google.android.gms:play-services-safetynet-license",
                "com.google.android.gms:play-services-vision-license",
                "com.google.android.gms:play-services-fido-license",
                "com.google.android.gms:play-services-drive-license",
                "com.google.android.gms:play-services-auth-api-phone-license",
                "com.google.android.gms:play-services-oss-licenses-license",
                "com.google.android.gms:play-services-identity-license",
                "com.google.android.gms:play-services-fitness-license",
                "com.google.android.gms:play-services-wearable-license",
                "com.google.android.gms:play-services-awareness-license",
                "com.google.android.gms:play-services-cast-framework-license",
                "com.google.android.gms:play-services-analytics-impl-license",
                "com.google.android.gms:play-services-vision-common-license",
                "com.google.android.gms:play-services-tasks-license",
                "com.google.android.gms:play-services-auth-license",
                "com.google.android.gms:play-services-iid-license",
                "com.google.android.gms:play-services-appinvite-license",
                "com.google.android.gms:play-services-gcm-license",
                "com.google.android.gms:play-services-ads-lite-license",
                "com.google.android.gms:play-services-ads-license",
                "com.google.android.gms:play-services-tagmanager-license",
                "com.google.android.gms:play-services-cast-license",
                "com.google.android.gms:play-services-wallet-license",
                "com.google.android.gms:play-services-analytics-license",
                "com.google.android.gms:play-services-instantapps-license",
                "com.google.android.gms:play-services-tagmanager-api-license",
                "com.google.android.gms:play-services-gass-license",
                "com.google.android.gms:play-services-base-license",
                "com.google.android.gms:play-services-location-license",
                "com.google.android.gms:play-services-basement-license",
                "com.google.android.gms:play-services-tagmanager-v4-impl-license",
                "com.google.android.gms:auth-api-impl",
                "com.google.gms:oss-licenses",
                "android.arch.paging:runtime",
                "android.arch.paging:common",
                "com.crashlytics.sdk.android:answers",
                "com.crashlytics.sdk.android:beta",
                "com.crashlytics.sdk.android:crashlytics-core",
                "com.crashlytics.sdk.android:crashlytics",
                "com.crashlytics.sdk.android:crashlytics-ndk",
                "io.fabric.sdk.android:fabric",
                "android.arch.persistence:db-framework",
                "android.arch.persistence:db",
                "com.google.android.wearable:wearable",
                "com.google.android.support:wearable",
                "com.android.installreferrer:installreferrer",
                "com.google.ar:core",
                "androidx.core:core-ktx",
                "com.google.android.things:androidthings"
        )
    }

}
