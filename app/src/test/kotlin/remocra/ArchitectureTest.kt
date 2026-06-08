package remocra

import com.google.inject.Inject
import com.google.inject.Module
import com.google.inject.Provider
import com.google.inject.Singleton
import com.google.inject.name.Named
import com.tngtech.archunit.base.DescribedPredicate.describe
import com.tngtech.archunit.base.DescribedPredicate.empty
import com.tngtech.archunit.base.DescribedPredicate.not
import com.tngtech.archunit.core.domain.JavaClass.Predicates.assignableTo
import com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage
import com.tngtech.archunit.core.domain.JavaClass.Predicates.type
import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.conditions.ArchConditions.beAssignableTo
import com.tngtech.archunit.lang.conditions.ArchConditions.have
import com.tngtech.archunit.lang.conditions.ArchPredicates.are
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.constructors
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods
import jakarta.ws.rs.Path
import remocra.auth.ApacheHopAuthenticationFilter
import remocra.auth.ApiAuthenticationFilter
import remocra.auth.ApiMobileAuthenticationFilter
import remocra.auth.Public
import remocra.auth.RequireDroits
import remocra.auth.RequireDroitsApi
import remocra.cli.Main
import remocra.db.AbstractRepository
import remocra.db.TransactionManager
import remocra.tasks.SimpleTask
import remocra.tasks.SynchronisationSIGTask
import remocra.usecase.AbstractCUDGeometrieUseCase
import remocra.usecase.AbstractCUDUseCase
import remocra.usecase.AbstractUseCase
import remocra.usecase.document.AbstractUpsertDocumentUseCase
import remocra.usecase.pei.AbstractCUDPeiUseCase
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
        .resideInAnyPackage("..usecase..", "..web..", "..auth..", "..endpoint..", "..eventbus..", "..tasks..", "..utils..")

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
        .and()
        .haveNameMatching(".*Endpoint")
        .should()
        .onlyHaveDependentClassesThat()
        .resideInAnyPackage("..web..", "..http..", "..endpoint..", "..auth..", "..json..", "..cli..", "..csv..")

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

    // Tous les Repository doivent hériter de AbstractRepository
    @ArchTest
    val inheritsRepository: ArchRule = classes()
        .that()
        .haveNameMatching(".*Repository")
        .and()
        .doNotHaveSimpleName("SigRepository")
        .and()
        .doNotHaveSimpleName("EntrepotSigRepository")
        .and()
        .doNotHaveSimpleName("NomenclatureRepository")
        .should()
        .beAssignableTo(AbstractRepository::class.java)

    // On n'utilise pas les annotations de Google Inject (Inject, Singleton, Provider, Named) mais celles de Jakarta Inject
    @ArchTest
    val `utiliser JakartaInject plutot que GoogleInject`: ArchRule? = classes()
        .that()
        .areNotAssignableTo(Module::class.java)
        .should()
        .onlyDependOnClassesThat()
        .doNotBelongToAnyOf(
            Inject::class.java,
            Singleton::class.java,
            Provider::class.java,
            Named::class.java,
        )

    @ArchTest
    val `@Inject ne doit pas être utilisé sur des champs sauf cas spécial et endpoint` =
        classes()
            .that(
                are(
                    not(
                        assignableTo(AbstractEndpoint::class.java)
                            .or(type(AbstractCUDUseCase::class.java))
                            .or(type(AbstractUseCase::class.java))
                            .or(type(AbstractCUDGeometrieUseCase::class.java))
                            .or(type(SimpleTask::class.java))
                            .or(type(AbstractCUDPeiUseCase::class.java))
                            .or(type(AbstractRepository::class.java))
                            .or(type(AbstractUpsertDocumentUseCase::class.java))
                            .or(type(ApiMobileAuthenticationFilter::class.java))
                            .or(type(ApiAuthenticationFilter::class.java))
                            .or(type(ApacheHopAuthenticationFilter::class.java)),
                    ),
                ),
            )
            .should(
                have(
                    describe("no field annotated @Inject") { javaClass ->
                        javaClass.fields.count {
                            it.isAnnotatedWith(jakarta.inject.Inject::class.java)
                        } == 0
                    },
                ),
            )

    // Pour les endpoints il faut utiliser @Inject sur les fields
    @ArchTest
    val `aucun constructeur pour les endpoints` =
        constructors()
            .that()
            .areDeclaredInClassesThat(assignableTo(AbstractEndpoint::class.java))
            .should()
            .haveRawParameterTypes(empty())

    @ArchTest
    val `SIG appartient a SIG` =
        classes()
            .that()
            .resideInAPackage("remocra.db.sig..")
            .should()
            .onlyHaveDependentClassesThat(
                resideInAPackage("remocra.db.sig..")
                    .or(type(SynchronisationSIGTask::class.java))
                    .or(type(Main::class.java)),
            )
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
