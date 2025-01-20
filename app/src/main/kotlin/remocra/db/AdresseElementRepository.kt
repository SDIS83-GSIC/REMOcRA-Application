package remocra.db

import com.google.inject.Inject
import org.jooq.DSLContext
import remocra.db.jooq.remocra.tables.pojos.AdresseElement
import remocra.db.jooq.remocra.tables.references.ADRESSE_ELEMENT
import remocra.db.jooq.remocra.tables.references.ADRESSE_TYPE_ANOMALIE
import remocra.db.jooq.remocra.tables.references.L_ADRESSE_ELEMENT_ADRESSE_TYPE_ANOMALIE
import java.util.UUID

class AdresseElementRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    fun insertAdresseElement(adresseElement: AdresseElement) =
        dsl.insertInto(ADRESSE_ELEMENT).set(dsl.newRecord(ADRESSE_ELEMENT, adresseElement)).execute()

    fun insertLiaisonAnomalie(adresseElementId: UUID, listeAnomalie: Collection<String>) {
        listeAnomalie.forEach { anomalie ->
            dsl.insertInto(L_ADRESSE_ELEMENT_ADRESSE_TYPE_ANOMALIE)
                .set(L_ADRESSE_ELEMENT_ADRESSE_TYPE_ANOMALIE.ELEMENT_ID, adresseElementId)
                .set(
                    L_ADRESSE_ELEMENT_ADRESSE_TYPE_ANOMALIE.ADRESSE_TYPE_ANOMALIE_ID,
                    dsl.select(ADRESSE_TYPE_ANOMALIE.ID).from(
                        ADRESSE_TYPE_ANOMALIE,
                    ).where(ADRESSE_TYPE_ANOMALIE.CODE.eq(anomalie)),
                )
                .execute()
        }
    }
}
