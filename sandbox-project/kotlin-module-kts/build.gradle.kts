plugins {
    `java-library`
}

repositories {
    jcenter()
}

dependencies {
    implementation("com.squareup.retrofit2:retrofit:2.7.1")
    implementation("com.google.dagger:dagger:2.26")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
