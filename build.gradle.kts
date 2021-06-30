import com.github.vlsi.gradle.properties.dsl.stringProperty
import org.gradle.api.publish.maven.internal.publication.MavenPublicationInternal

plugins {
    id("com.github.vlsi.gradle-extensions")
    id("maven-publish")
}

val String.v: String get() = rootProject.extra["$this.version"] as String
val projectVersion = project.name.v

group = "com.github.weisj"
version = projectVersion

val buildJNF by tasks.registering(Exec::class) {
    commandLine("sh", "build_jnf.sh")
}

val archiveJNF by tasks.registering(Zip::class) {
    dependsOn(buildJNF)
    archiveBaseName.set("JavaNativeFoundation")
    archiveExtension.set("framework.zip")
    destinationDirectory.set(project.buildDir.resolve("frameworks"))
    from("buildNative/Frameworks/JavaNativeFoundation.framework")
}

fun registerJNFConfiguration(architecture : String) = configurations.registering {
    isCanBeResolved = false
    isCanBeConsumed = true
    attributes {
        attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage::class.java, "${Usage.C_PLUS_PLUS_API}+${Usage.NATIVE_LINK}+${Usage.NATIVE_RUNTIME}"))
        attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named(LibraryElements::class.java, "framework-bundle"))
        attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category::class.java, Category.LIBRARY))
        attribute(Attribute.of("dev.nokee.operatingSystem", String::class.java), "macos")
        attribute(Attribute.of("dev.nokee.architecture", String::class.java), architecture)
    }
    outgoing.artifact(archiveJNF)
}

val jnfElementsArm by registerJNFConfiguration("arm64")
val jnfElementsX86 by registerJNFConfiguration("x86-64")

apply(from="jnf-component.gradle")

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/weisj/JavaNativeFoundation")
            credentials {
                username = project.stringProperty("gpr.user") ?: System.getenv("GITHUB_ACTOR")
                password = project.stringProperty("gpr.key") ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()
            from(components["jnf"])
            (this as MavenPublicationInternal).publishWithOriginalFileName()
        }
    }
}
