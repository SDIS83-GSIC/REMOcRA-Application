package remocra

import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods
import jakarta.ws.rs.Path
import remocra.auth.Public
import remocra.auth.RequireDroits
import remocra.auth.RequireDroitsApi

@AnalyzeClasses(packages = ["remocra"])
class ArchitectureTest {
    /**
     * Les méthodes des Endpoint hors API doivent comporter un @RequireDroits ou un @Public
     */
    @ArchTest
    val publicOrRequireDroit: ArchRule =
        methods()
            .that()
            .areDeclaredInClassesThat().resideOutsideOfPackage("remocra.api.endpoint")
            .and()
            .areAnnotatedWith(Path::class.java)
            .should()
            .beAnnotatedWith(RequireDroits::class.java)
            .orShould()
            .beAnnotatedWith(Public::class.java)
}

/**
 * Les méthodes des Endpoint de l'API doivent comporter un @RequireDroitsApi ou un @Public
 */
@AnalyzeClasses(packages = ["remocra.api.endpoint"])
class ApiArchitectureTest {
    @ArchTest
    val publicOrRequireDroitApi: ArchRule =
        methods()
            .that()
            .areDeclaredInClassesThat().resideInAPackage("remocra.api.endpoint")
            .and()
            .areAnnotatedWith(Path::class.java)
            .should()
            .beAnnotatedWith(RequireDroitsApi::class.java)
            .orShould()
            .beAnnotatedWith(Public::class.java)
}
