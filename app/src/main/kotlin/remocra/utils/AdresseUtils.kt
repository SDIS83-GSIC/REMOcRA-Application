package remocra.utils

import org.jooq.Field
import org.jooq.impl.DSL
import remocra.db.jooq.remocra.tables.Pei
import remocra.db.jooq.remocra.tables.references.VOIE

/**
 * Utilitaire pour faciliter la récupération des adresses, dans les requêtes où le [AdresseDecorator] ne peut pas être utilisé.
 */
class AdresseUtils {
    companion object {
        fun getDslConcatForAdresse(): Field<String?> {
            /**
             * L'utilisation du [AdresseDecorator] est impossible avec le filter + count, on redécoupe donc, mais ça doit rester en phase !
             * @see AdresseDecorator.decorateAdresse
             */
            val adresseField = DSL.concat(
                DSL.`when`(Pei.PEI.EN_FACE.isTrue, AdresseDecorator.FACE_A + " ").otherwise(""),
                DSL.`when`(Pei.PEI.NUMERO_VOIE.isNotNull, DSL.concat(Pei.PEI.NUMERO_VOIE, " ")).otherwise(""),
                DSL.`when`(Pei.PEI.SUFFIXE_VOIE.isNotNull, DSL.concat(Pei.PEI.SUFFIXE_VOIE, " ")).otherwise(""),
                DSL.`when`(Pei.PEI.VOIE_ID.isNotNull, DSL.concat(VOIE.LIBELLE, " ")).otherwise(Pei.PEI.VOIE_TEXTE),
            )
            return adresseField
        }
    }
}
