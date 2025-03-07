package remocra.usecase.adresse

import jakarta.inject.Inject
import org.locationtech.jts.geom.Geometry
import remocra.auth.UserInfo
import remocra.data.AdresseData
import remocra.data.AuteurTracabiliteData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeSourceModification
import remocra.db.AdresseRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.enums.EtatAdresse
import remocra.db.jooq.remocra.tables.pojos.Adresse
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDGeometrieUseCase
import remocra.utils.calculerCentroide

class CreateAdresseUsecase @Inject constructor(
    private val createAdresseElementUsecase: CreateAdresseElementUsecase,
    private val adresseRepository: AdresseRepository,
) :
    AbstractCUDGeometrieUseCase<AdresseData>(TypeOperation.INSERT) {

    override fun getListGeometrie(element: AdresseData): Collection<Geometry> {
        return element.listAdresseElement.map { e -> e.geometry }
    }

    override fun ensureSrid(element: AdresseData): AdresseData {
        if (element.listAdresseElement.any { g -> g.geometry.srid != appSettings.srid }) {
            return element.copy(
                listAdresseElement = element.listAdresseElement.map {
                        adresse ->
                    adresse.copy(geometry = transform(adresse.geometry))
                },
            )
        }
        return element
    }

    override fun execute(userInfo: UserInfo?, element: AdresseData): AdresseData {
        adresseRepository.insertAdresse(
            Adresse(
                adresseDescription = element.description,
                adresseId = element.adresseId,
                adresseUtilisateur = userInfo!!.utilisateurId,
                adresseType = EtatAdresse.EN_COURS,
                adresseDateConstat = dateUtils.now(),
                adresseDateModification = null,
                adresseGeometrie = calculerCentroide(element.listAdresseElement.map { it.geometry })!!,
            ),
        )

        element.listAdresseElement.forEach { e ->
            e.apply { e.adresseElementAdresseId = element.adresseId }
            createAdresseElementUsecase.execute(userInfo, e, transactionManager)
        }

        return element
    }

    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.ADRESSES_C)) {
            throw RemocraResponseException(ErrorType.ADRESSE_FORBIDDEN_INSERT)
        }
    }

    override fun postEvent(element: AdresseData, userInfo: UserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.adresseId,
                typeOperation = typeOperation,
                typeObjet = TypeObjet.ADRESSE,
                auteurTracabilite = AuteurTracabiliteData(
                    idAuteur = userInfo.utilisateurId,
                    nom = userInfo.nom,
                    prenom = userInfo.prenom,
                    email = userInfo.email,
                    typeSourceModification = TypeSourceModification.REMOCRA_WEB,
                ),
                date = dateUtils.now(),
            ),
        )
    }

    override fun checkContraintes(userInfo: UserInfo?, element: AdresseData) {
        // no-op pas de contrainte
    }
}
