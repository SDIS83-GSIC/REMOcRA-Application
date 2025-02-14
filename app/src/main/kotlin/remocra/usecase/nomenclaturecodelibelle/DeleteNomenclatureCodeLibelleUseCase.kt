package remocra.usecase.nomenclaturecodelibelle

import jakarta.inject.Inject
import remocra.auth.UserInfo
import remocra.data.AuteurTracabiliteData
import remocra.data.NomenclatureCodeLibelleData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeDataCache
import remocra.data.enums.TypeNomenclatureCodeLibelle
import remocra.data.enums.TypeSourceModification
import remocra.db.NomenclatureCodeLibelleRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.eventbus.datacache.DataCacheModifiedEvent
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class DeleteNomenclatureCodeLibelleUseCase @Inject constructor(private val nomenclatureCodeLibelleRepository: NomenclatureCodeLibelleRepository) :
    AbstractCUDUseCase<NomenclatureCodeLibelleData>(TypeOperation.DELETE) {

    private lateinit var typeNomenclatureCodeLibelle: TypeNomenclatureCodeLibelle

    override fun checkDroits(userInfo: UserInfo) {
        if (!userInfo.droits.contains(Droit.ADMIN_DROITS)) {
            throw RemocraResponseException(ErrorType.ADMIN_NOMENC_FORBIDDEN_REMOVAL)
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
        // Si la nomenclature modifiée fait partie du DataCache
        // Alors MiseAJour du Cache en question
        val foundEntry = TypeDataCache.entries.find { it.toString() == typeNomenclatureCodeLibelle.name }
        if (foundEntry != null) {
            eventBus.post(DataCacheModifiedEvent(foundEntry))
        }
    }

    override fun execute(userInfo: UserInfo?, element: NomenclatureCodeLibelleData): NomenclatureCodeLibelleData {
        nomenclatureCodeLibelleRepository.delete(typeNomenclatureCodeLibelle, element.id)
        return element
    }

    override fun checkContraintes(userInfo: UserInfo?, element: NomenclatureCodeLibelleData) {
        if (element.protected) {
            throw RemocraResponseException(ErrorType.ADMIN_NOMENC_IS_PROTECTED)
        }
    }

    fun setType(typeNomenclatureCodeLibelle: TypeNomenclatureCodeLibelle) {
        this.typeNomenclatureCodeLibelle = typeNomenclatureCodeLibelle
    }
}
