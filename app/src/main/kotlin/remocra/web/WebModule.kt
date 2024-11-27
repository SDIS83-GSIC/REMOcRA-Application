package remocra.web

import com.google.inject.Binder
import com.google.inject.TypeLiteral
import com.google.inject.multibindings.Multibinder
import remocra.RemocraModule
import remocra.api.endpoint.ApiModule
import remocra.log.LogManagerFactory
import remocra.log.LogManagerFactoryImpl
import remocra.resteasy.MultipartFormAnnotationReader
import remocra.resteasy.ParamConverterProvider
import remocra.resteasy.UUIDMessageBodyReader
import remocra.resteasy.UnhandledExceptionMapper
import remocra.security.CsrfFeature
import remocra.web.admin.AdminModule
import remocra.web.anomalie.AnomalieModule
import remocra.web.appsettings.AppSettingsModule
import remocra.web.commune.CommuneModule
import remocra.web.courrier.CourrierModule
import remocra.web.couverturehydraulique.CouvertureHydrauliqueModule
import remocra.web.debitsimultane.DebitSimultaneModule
import remocra.web.documents.DocumentModule
import remocra.web.ficheresume.FicheResumeModule
import remocra.web.gestionnaire.GestionnaireModule
import remocra.web.image.ImageModule
import remocra.web.indisponibilitetemporaire.IndisponibiliteTemporaireModule
import remocra.web.lieudit.LieuDitModule
import remocra.web.marque.MarquePibiModule
import remocra.web.module.ModuleModule
import remocra.web.nature.NatureModule
import remocra.web.naturedeci.NatureDeciModule
import remocra.web.nomenclatures.NomenclatureModule
import remocra.web.organisme.OrganismeModule
import remocra.web.parametres.ParametreModule
import remocra.web.pei.PeiModule
import remocra.web.profildroit.ProfilDroitModule
import remocra.web.profilorganisme.ProfilOrganismeModule
import remocra.web.profilutilisateur.ProfilUtilisateurModule
import remocra.web.rapportpersonnalise.RapportPersonnaliseModule
import remocra.web.thematique.ThematiqueModule
import remocra.web.typeorganisme.TypeOrganismeModule
import remocra.web.utilisateur.UtilisateurModule
import remocra.web.visite.VisiteModule
import remocra.web.voie.VoieModule
import remocra.web.zoneintegration.ZoneIntegrationModule
import kotlin.reflect.KClass

object WebModule : RemocraModule() {

    override fun configure() {
        install(PeiModule)
        install(CommuneModule)
        install(NatureModule)
        install(OrganismeModule)
        install(NatureDeciModule)
        install(ApiModule)
        install(VisiteModule)
        install(AnomalieModule)
        install(NomenclatureModule)
        install(AppSettingsModule)
        install(DocumentModule)
        install(AdminModule)
        install(CourrierModule)
        install(CouvertureHydrauliqueModule)
        install(IndisponibiliteTemporaireModule)
        install(ModuleModule)
        install(ParametreModule)
        install(FicheResumeModule)
        install(ProfilDroitModule)
        install(ProfilUtilisateurModule)
        install(ProfilOrganismeModule)
        install(TypeOrganismeModule)
        install(ZoneIntegrationModule)
        install(ImageModule)
        install(MarquePibiModule)
        install(GestionnaireModule)
        install(VoieModule)
        install(LieuDitModule)
        install(ThematiqueModule)
        install(UtilisateurModule)
        install(DebitSimultaneModule)
        install(RapportPersonnaliseModule)
        bind(LogManagerFactory::class.java).to(LogManagerFactoryImpl::class.java)

        registerResource<CsrfFeature>()
        registerResource<MultipartFormAnnotationReader>()
        registerResource<UUIDMessageBodyReader>()
        registerResource<ParamConverterProvider>()
        registerResource<UnhandledExceptionMapper>()
    }

    private inline fun <reified T> registerResource() {
        binder().registerResource<T>()
    }
}

inline fun <reified T> Binder.registerResource() = registerResource(T::class)

fun Binder.registerResource(clazz: KClass<*>) =
    Multibinder.newSetBinder(this, object : TypeLiteral<Class<*>>() {})
        .addBinding()
        .toInstance(clazz.java)

fun Binder.registerResources(vararg classes: KClass<*>) =
    Multibinder.newSetBinder(this, object : TypeLiteral<Class<*>>() {}).run {
        classes.forEach { addBinding().toInstance(it.java) }
    }
