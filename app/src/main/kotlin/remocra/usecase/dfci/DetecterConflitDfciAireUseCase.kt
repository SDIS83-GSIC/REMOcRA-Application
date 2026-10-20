package remocra.usecase.dfci

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.DfciAireData
import remocra.data.enums.DfciAireColonne
import remocra.db.DfciAiresRepository
import remocra.db.jooq.remocra.tables.pojos.DfciConflit
import remocra.usecase.AbstractUseCase
import java.util.UUID
import kotlin.reflect.full.memberProperties

class DetecterConflitDfciAireUseCase @Inject constructor(
    private val upsertDfciConflitUseCase: UpsertDfciConflitUseCase,
    private val dfciAiresRepository: DfciAiresRepository,
) : AbstractUseCase(), DetecterConflit<DfciAireData> {
    override fun detecterConflit(remocraElem: DfciAireData, sigElem: DfciAireData, userInfo: WrappedUserInfo): Boolean {
        var result = false
        if (remocraElem == sigElem) return false
        for (property in DfciAireData::class.memberProperties) {
            val valueSig = property.get(sigElem)?.toString()
            val valueRemocra = property.get(remocraElem)?.toString()
            if (property.name == DfciAireColonne.DFCI_AIRE_GEOMETRIE.nomColonne) { // REMOcRA ne change pas la geometrie, on prend donc celle du SIG
                dfciAiresRepository.updateDfciAireColumn(property.name, valueSig, remocraElem.id)
            } else {
                if (valueSig != valueRemocra) {
                    result = true
                    val conflit = DfciConflit(
                        UUID.randomUUID(),
                        "dfci_aire",
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
