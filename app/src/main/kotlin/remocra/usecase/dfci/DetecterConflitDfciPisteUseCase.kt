package remocra.usecase.dfci

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.DfciPisteData
import remocra.data.enums.DfciPisteColonne
import remocra.db.DfciPistesRepository
import remocra.db.jooq.remocra.tables.pojos.DfciConflit
import remocra.usecase.AbstractUseCase
import java.util.UUID
import kotlin.reflect.full.memberProperties

class DetecterConflitDfciPisteUseCase @Inject constructor(
    private val upsertDfciConflitUseCase: UpsertDfciConflitUseCase,
    private val dfciPistesRepository: DfciPistesRepository,
) : AbstractUseCase(), DetecterConflit<DfciPisteData> {
    override fun detecterConflit(remocraElem: DfciPisteData, sigElem: DfciPisteData, userInfo: WrappedUserInfo): Boolean {
        var result = false
        if (remocraElem == sigElem) return false
        for (property in DfciPisteData::class.memberProperties) {
            val valueSig = property.get(sigElem)?.toString()
            val valueRemocra = property.get(remocraElem)?.toString()
            if (property.name == DfciPisteColonne.DFCI_PISTE_GEOMETRIE.nomColonne) { // REMOcRA ne change pas la geometrie, on prend donc celle du SIG
                dfciPistesRepository.updateDfciPisteColumn(property.name, valueSig, remocraElem.id)
            } else {
                if (valueSig != valueRemocra) {
                    result = true
                    val conflit = DfciConflit(
                        UUID.randomUUID(),
                        "dfci_piste",
                        remocraElem.id,
                        property.name,
                        valueRemocra,
                        valueSig,
                        dateUtils.now(),
                    )
                    upsertDfciConflitUseCase.execute(remocraElem.id, conflit, userInfo)
                }
            }
        }
        return result
    }
}
