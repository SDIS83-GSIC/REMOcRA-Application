plugins {
    id("local.base")
    id("local.dependency-management")
    `lifecycle-base`
    idea
    alias(libs.plugins.forbiddenapis)
}
val deps by configurations.creating {
    isVisible = false
    isCanBeResolved = true
    isCanBeConsumed = false
}
dependencies {
    deps(projects.app)
}

tasks {
    val prepareForDocker by registering(Copy::class) {
        duplicatesStrategy = DuplicatesStrategy.FAIL
        includeEmptyDirs = false
        into(project.layout.buildDirectory.dir("docker"))
        // On prend les variantes "classes" et "resources" pour ne pas avoir de JAR pour les projets
        // ce qui nous permet de distinguer les dépendances tierces.
        from(
            files(
                deps.incoming.artifactView { attributes { attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named(LibraryElements.CLASSES)) } }.files,
                deps.incoming.artifactView { attributes { attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named(LibraryElements.RESOURCES)) } }.files,
            ),
        ) {
            eachFile {
                if (isDirectory) { exclude() }
                path = when (file.extension) {
                    "jar" -> "libs/$path"
                    "class" -> "classes/$path"
                    else -> "resources/$path"
                }
            }
        }
        doFirst {
            delete(destinationDir)
        }
    }
    assemble {
        dependsOn(prepareForDocker)
    }
}

idea {
    module {
        excludeDirs.addAll(arrayOf(file(".docker"), file("frontend/build"), file("frontend/node_modules")))
    }
}
