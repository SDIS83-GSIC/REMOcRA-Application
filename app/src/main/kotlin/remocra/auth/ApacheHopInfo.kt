package remocra.auth

import remocra.data.enums.TypeSourceModification
import remocra.db.jooq.remocra.enums.Droit
import java.io.Serializable
import java.util.UUID

class ApacheHopInfo(
    val typeSourceModification: TypeSourceModification = TypeSourceModification.APACHE_HOP,
) : Serializable {
    // A compléter en fonction des droits qu'on peut autoriser pour un traitement apache hop
    val droits: Set<Droit> = setOf(Droit.COURRIER_C)

    val auteurId: UUID
        get() = UUID.fromString("00000000-0000-0000-0000-000000000000")

    val nom: String
        get() = "Apache"

    val prenom: String
        get() = "Hop"

    val email: String
        get() = "apache-hop@example.org"
}
