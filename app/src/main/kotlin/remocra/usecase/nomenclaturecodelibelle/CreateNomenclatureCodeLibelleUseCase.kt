package remocra.usecase.nomenclaturecodelibelle

import jakarta.inject.Inject
import remocra.auth.UserInfo
import remocra.data.AuteurTracabiliteData
import remocra.data.NomenclatureCodeLibelleData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeNomenclatureCodeLibelle
import remocra.data.enums.TypeSourceModification
import remocra.db.NomenclatureCodeLibelleRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class CreateNomenclatureCodeLibelleUseCase @Inject constructor(private val nomenclatureCodeLibelleDataRepository: NomenclatureCodeLibelleRepository) :
    AbstractCUDUseCase<NomenclatureCodeLibelleData>(TypeOperation.INSERT) {

    private lateinit var typeNomenclatureCodeLibelle: TypeNomenclatureCodeLibelle
    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.ADMIN_DROITS)) {
            throw RemocraResponseException(ErrorType.ADMIN_NATURE_FORBIDDEN_INSERT)
        }
    }

    override fun postEvent(element: NomenclatureCodeLibelleData, userInfo: UserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.id,
                typeOperation = typeOperation,
                // Les noms sont communs entre les 2 enum
                typeObjet = TypeObjet.valueOf(typeNomenclatureCodeLibelle.name),
                auteurTracabilite = AuteurTracabiliteData(idAuteur = userInfo.utilisateurId, nom = userInfo.nom, prenom = userInfo.prenom, email = userInfo.email, typeSourceModification = TypeSourceModification.REMOCRA_WEB),
                date = dateUtils.now(),
            ),
        )
    }

    override fun execute(userInfo: UserInfo?, element: NomenclatureCodeLibelleData): NomenclatureCodeLibelleData {
        nomenclatureCodeLibelleDataRepository.create(typeNomenclatureCodeLibelle, element)
        return element
    }

    override fun checkContraintes(userInfo: UserInfo?, element: NomenclatureCodeLibelleData) {
        // Pour le(s) type(s) concerné(s) par de l'auto-jointure (hiérarchie) : vérification que la FK de même type n'est pas l'objet lui-même
        // Ca n'a aucun sens pour les autres cas, mais ça n'est pas néfaste non plus puisque les UUID sont tous uniques
        if (element.idFk != null && element.id == element.idFk) {
            throw RemocraResponseException(ErrorType.ADMIN_NOMENC_SAME_ELEMENT)
        }
    }

    fun setType(typeNomenclatureCodeLibelle: TypeNomenclatureCodeLibelle) {
        this.typeNomenclatureCodeLibelle = typeNomenclatureCodeLibelle
    }
}
