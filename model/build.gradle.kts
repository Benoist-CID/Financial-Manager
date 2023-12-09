plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
dependencies {
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.amshove.kluent:kluent:1.73")
}
