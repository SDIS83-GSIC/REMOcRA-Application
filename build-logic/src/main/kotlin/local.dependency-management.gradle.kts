plugins {
    id("org.gradlex.jvm-dependency-conflict-resolution")
}

jvmDependencyConflicts {
    logging {
        enforceLog4J2()
    }
    patch {
        // Cause un conflit entre javax.activation et jakarta.activation dans Android Lint
        module("com.android.tools:repository") {
            removeDependency("com.sun.activation:javax.activation")
        }
    }
}
