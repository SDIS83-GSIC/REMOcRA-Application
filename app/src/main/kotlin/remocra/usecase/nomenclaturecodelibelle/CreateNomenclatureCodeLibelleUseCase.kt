package remocra.usecase.nomenclaturecodelibelle

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.NomenclatureCodeLibelleData
import remocra.data.enums.ErrorType
import remocra.data.enums.TypeDataCache
import remocra.data.enums.TypeNomenclatureCodeLibelle
import remocra.db.NomenclatureCodeLibelleRepository
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import remocra.db.jooq.remocra.enums.Droit
import remocra.eventbus.datacache.DataCacheModifiedEvent
import remocra.eventbus.tracabilite.TracabiliteEvent
import remocra.exception.RemocraResponseException
import remocra.usecase.AbstractCUDUseCase

class CreateNomenclatureCodeLibelleUseCase @Inject constructor(private val nomenclatureCodeLibelleDataRepository: NomenclatureCodeLibelleRepository) :
    AbstractCUDUseCase<NomenclatureCodeLibelleData>(TypeOperation.INSERT) {

    private lateinit var typeNomenclatureCodeLibelle: TypeNomenclatureCodeLibelle
    override fun checkDroits(userInfo: WrappedUserInfo) {
        if (!userInfo.hasDroit(droitWeb = Droit.ADMIN_NOMENCLATURE)) {
            throw RemocraResponseException(ErrorType.ADMIN_NATURE_FORBIDDEN_INSERT)
        }
    }

    override fun postEvent(element: NomenclatureCodeLibelleData, userInfo: WrappedUserInfo) {
        eventBus.post(
            TracabiliteEvent(
                pojo = element,
                pojoId = element.id,
                typeOperation = typeOperation,
                // Les noms sont communs entre les 2 enum
                typeObjet = TypeObjet.valueOf(typeNomenclatureCodeLibelle.name),
                auteurTracabilite = userInfo.getInfosTracabilite(),
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

    override fun execute(userInfo: WrappedUserInfo, element: NomenclatureCodeLibelleData): NomenclatureCodeLibelleData {
        nomenclatureCodeLibelleDataRepository.create(typeNomenclatureCodeLibelle, element)
        return element
    }

    override fun checkContraintes(userInfo: WrappedUserInfo, element: NomenclatureCodeLibelleData) {
        if (nomenclatureCodeLibelleDataRepository.checkCodeExists(typeNomenclatureCodeLibelle, element.code, null)) {
            throw RemocraResponseException(ErrorType.ADMIN_NOMENC_CODE_EXISTS)
        }

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
