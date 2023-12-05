import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    kotlin("jvm") version "1.9.21"
    id("org.jetbrains.dokka") version "1.9.10"
    id("io.github.gradle-nexus.publish-plugin") version "1.3.0"
    `maven-publish`
    signing
}

group = "com.sschr15"
archivesName = "templates-kt"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks {
    withType<PublishToMavenRepository> {
        mustRunAfter(withType<Sign>())
    }

    test {
        useJUnitPlatform()
    }
}

val dokkaJar by tasks.creating(Jar::class) {
    from(tasks.dokkaHtml)
    archiveClassifier = "javadoc"
}

signing {
    val signingKey = System.getenv("SIGNING_KEY")
    val signingPassword = System.getenv("SIGNING_PASSWORD")
    if (signingKey != null && signingPassword != null) {
        useInMemoryPgpKeys(signingKey, signingPassword)
        sign(publishing.publications)
    } else if (System.getenv("CI") == null) {
        logger.info("Trying to defer to GPG agent for signing.")
        useGpgCmd()
    } else {
        logger.warn("Signing key or password not found, not signing artifacts.")
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            artifact(dokkaJar)
            artifact(tasks.kotlinSourcesJar)

            pom {
                name = "String Templates for Kotlin"
                description = "A library for using Java's string templates in Kotlin."
                url = "https://github.com/sschr15/templates-kt"

                licenses {
                    license {
                        name = "MIT License"
                        url = "https://opensource.org/licenses/MIT"
                    }
                }

                developers {
                    developer {
                        name = "sschr15"
                        email = "me@sschr15.com"
                        timezone = "America/Chicago"
                        url = "https://github.com/sschr15"
                    }
                }

                scm {
                    connection = this@pom.url.get().replace("https", "scm:git:git")
                    developerConnection = connection.get().replace("git://", "ssh://")
                    url = this@pom.url
                }
            }
        }
    }
}

if ("oss.sonatype.org" in (System.getenv("MAVEN_URL") ?: "")) {
    nexusPublishing {
        repositories {
            sonatype {
                nexusUrl = uri(System.getenv("MAVEN_URL"))
                username = System.getenv("MAVEN_USERNAME")
                password = System.getenv("MAVEN_PASSWORD")
            }
        }
    }
} else if (System.getenv("MAVEN_URL") != null) {
    publishing {
        repositories {
            maven(System.getenv("MAVEN_URL")) {
                credentials {
                    username = System.getenv("MAVEN_USERNAME")
                    password = System.getenv("MAVEN_PASSWORD")
                }
            }
        }
    }
} else {
    logger.info("No Maven URL found, not publishing artifacts.")
}
