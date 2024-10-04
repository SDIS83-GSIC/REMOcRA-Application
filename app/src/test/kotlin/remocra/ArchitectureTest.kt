package remocra

import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods
import jakarta.ws.rs.Path
import remocra.auth.Public
import remocra.auth.RequireDroits
import remocra.auth.RequireDroitsApi
import remocra.db.TransactionManager
import remocra.usecase.AbstractUseCase
import remocra.web.AbstractEndpoint

@AnalyzeClasses(packages = ["remocra"], importOptions = [DoNotIncludeTests::class])
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

    @ArchTest
    val dontDependOnUseCases: ArchRule = classes()
        .that()
        .resideInAPackage("..usecase..")
        .should()
        .onlyHaveDependentClassesThat()
        .resideInAnyPackage("..usecase..", "..web..", "..auth..", "..endpoint..", "..eventbus..")

    @ArchTest
    val onlyUsecasesManageTransactions: ArchRule = classes()
        .that()
        .areAssignableTo(TransactionManager::class.java)
        .should()
        .onlyHaveDependentClassesThat()
        .resideInAnyPackage("..usecase..", "..eventbus..", "..tasks..")

    @ArchTest
    val dontDependOnWeb: ArchRule = classes()
        .that()
        .resideInAPackage("..web..")
        .should()
        .onlyHaveDependentClassesThat()
        .resideInAnyPackage("..web..", "..http..", "..endpoint..", "..auth..", "..json..", "..cli..")

    // Tous les Endpoint doivent hériter de AbstractEndpoint
    @ArchTest
    val inheritsEndpoint: ArchRule = classes()
        .that()
        .haveNameMatching(".*Endpoint")
        .and()
        // Celui-ci est particulier : public, pas les mêmes patterns
        .haveSimpleNameNotContaining("OpenApiEndpoint")
        .should()
        .beAssignableTo(AbstractEndpoint::class.java)

    // Tous les Usecase doivent hériter de AbstractUsecase
    @ArchTest
    val inheritsUseCase: ArchRule = classes()
        .that()
        .haveNameMatching(".*UseCase")
        .should()
        .beAssignableTo(AbstractUseCase::class.java)
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
