package remocra.usecases.pei

import com.google.inject.Inject
import remocra.authn.UserInfo
import remocra.data.PeiData
import remocra.db.PeiRepository
import remocra.db.jooq.historique.enums.TypeOperation

class UpdatePeiUseCase : AbstractCUDPeiUseCase(typeOperation = TypeOperation.UPDATE) {

    @Inject lateinit var peiRepository: PeiRepository

    override fun executeSpecific(element: PeiData) {
        TODO("Not yet implemented")
    }

    override fun checkDroits(userInfo: UserInfo) {
        TODO("Not yet implemented")
    }
    override fun checkContraintes(element: PeiData) {
        TODO("Not yet implemented")
    }
}
