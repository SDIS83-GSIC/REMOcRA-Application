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

class UpdateNomenclatureCodeLibelleUseCase @Inject constructor(private val nomenclatureCodeLibelleRepository: NomenclatureCodeLibelleRepository) :
    AbstractCUDUseCase<NomenclatureCodeLibelleData>(TypeOperation.UPDATE) {

    private lateinit var typeNomenclatureCodeLibelle: TypeNomenclatureCodeLibelle

    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.ADMIN_DROITS)) {
            throw RemocraResponseException(ErrorType.ADMIN_NOMENC_FORBIDDEN_INSERT)
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

    override fun execute(userInfo: UserInfo?, element: NomenclatureCodeLibelleData): NomenclatureCodeLibelleData {
        nomenclatureCodeLibelleRepository.update(typeNomenclatureCodeLibelle, element)
        return element
    }

    override fun checkContraintes(userInfo: UserInfo?, element: NomenclatureCodeLibelleData) {
        if (nomenclatureCodeLibelleRepository.checkCodeExists(typeNomenclatureCodeLibelle, element.code, element.id)) {
            throw RemocraResponseException(ErrorType.ADMIN_NOMENC_CODE_EXISTS)
        }

        if (element.protected) {
            // On va récupérer l'objet courant pour vérifier que les propriétés n'ont pas été changées
            val persistedElement = nomenclatureCodeLibelleRepository.getById(typeNomenclatureCodeLibelle, element.id)
            if (persistedElement!!.code != element.code) {
                throw RemocraResponseException(ErrorType.ADMIN_NOMENC_IS_PROTECTED)
            }
        }
    }

    fun setType(typeNomenclatureCodeLibelle: TypeNomenclatureCodeLibelle) {
        this.typeNomenclatureCodeLibelle = typeNomenclatureCodeLibelle
    }
}
