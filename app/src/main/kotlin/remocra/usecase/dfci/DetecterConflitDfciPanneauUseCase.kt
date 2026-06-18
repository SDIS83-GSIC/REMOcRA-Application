package remocra.usecase.dfci

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.DfciPanneauData
import remocra.data.enums.DfciPanneauColonne
import remocra.db.DfciPanneauRepository
import remocra.db.jooq.remocra.tables.pojos.DfciConflit
import remocra.usecase.AbstractUseCase
import java.util.UUID
import kotlin.reflect.full.memberProperties

class DetecterConflitDfciPanneauUseCase @Inject constructor(
    private val upsertDfciConflitUseCase: UpsertDfciConflitUseCase,
    private val dfciPanneauRepository: DfciPanneauRepository,
) : AbstractUseCase(), DetecterConflit<DfciPanneauData> {

    override fun detecterConflit(
        remocraElem: DfciPanneauData,
        sigElem: DfciPanneauData,
        userInfo: WrappedUserInfo,
    ): Boolean {
        var result = false
        if (remocraElem == sigElem) return false
        for (property in DfciPanneauData::class.memberProperties) {
            val valueSig = property.get(sigElem)?.toString()
            val valueRemocra = property.get(remocraElem)?.toString()
            if (property.name == DfciPanneauColonne.DFCI_PANNEAU_GEOMETRIE.nomColonne) { // REMOcRA ne change pas la geometrie, on prend donc celle du SIG
                dfciPanneauRepository.updateDfciPanneauColumn(property.name, valueSig, remocraElem.id)
            } else {
                if (valueSig != valueRemocra) {
                    result = true
                    val conflit = DfciConflit(
                        UUID.randomUUID(),
                        "dfci_panneau",
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
