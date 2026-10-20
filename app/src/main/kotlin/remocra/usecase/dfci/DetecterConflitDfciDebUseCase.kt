package remocra.usecase.dfci

import jakarta.inject.Inject
import remocra.auth.WrappedUserInfo
import remocra.data.DfciDebData
import remocra.data.enums.DfciDebColonne
import remocra.db.DfciDebRepository
import remocra.db.jooq.remocra.tables.pojos.DfciConflit
import remocra.usecase.AbstractUseCase
import java.util.UUID
import kotlin.reflect.full.memberProperties

class DetecterConflitDfciDebUseCase @Inject constructor(
    private val upsertDfciConflitUseCase: UpsertDfciConflitUseCase,
    private val dfciDebRepository: DfciDebRepository,
) : AbstractUseCase(), DetecterConflit<DfciDebData> {

    override fun detecterConflit(
        remocraElem: DfciDebData,
        sigElem: DfciDebData,
        userInfo: WrappedUserInfo,
    ): Boolean {
        var result = false
        if (remocraElem == sigElem) return false
        for (property in DfciDebData::class.memberProperties) {
            val valueSig = property.get(sigElem)?.toString()
            val valueRemocra = property.get(remocraElem)?.toString()
            if (property.name == DfciDebColonne.DFCI_DEB_GEOMETRIE.nomColonne) { // REMOcRA ne change pas la geometrie, on prend donc celle du SIG
                dfciDebRepository.updateDfciDebColumn(property.name, valueSig, remocraElem.id)
            } else {
                if (valueSig != valueRemocra) {
                    result = true
                    val conflit = DfciConflit(
                        UUID.randomUUID(),
                        "dfci_deb",
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
