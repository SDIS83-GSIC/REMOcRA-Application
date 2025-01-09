package remocra.data.tracabilite

import remocra.data.enums.TypeSourceModification
import remocra.db.jooq.historique.enums.TypeObjet
import remocra.db.jooq.historique.enums.TypeOperation
import java.time.LocalDate

data class Search(
    val typeObjet: TypeObjet? = null,
    val typeOperation: TypeOperation? = null,
    var typeUtilisateur: TypeSourceModification? = null,
    val debut: LocalDate? = null,
    val fin: LocalDate? = null,
    var utilisateur: String? = null,
)
