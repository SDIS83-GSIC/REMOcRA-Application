package remocra.data.tracabilite

import remocra.data.enums.TypeSourceModification
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import java.time.LocalDateTime

data class Search(
    val typeObjet: TypeObjet? = null,
    val typeOperation: TypeOperation? = null,
    var typeUtilisateur: TypeSourceModification? = null,
    val debut: LocalDateTime? = null,
    val fin: LocalDateTime? = null,
    var utilisateur: String? = null,
)
