package remocra.db

import org.jooq.Condition
import org.jooq.impl.DSL
import remocra.auth.WrappedUserInfo
import remocra.db.jooq.remocra.enums.Droit
import remocra.db.jooq.remocra.tables.references.CONTACT
import remocra.db.jooq.remocra.tables.references.COURRIER
import remocra.db.jooq.remocra.tables.references.L_COURRIER_CONTACT_GESTIONNAIRE
import remocra.db.jooq.remocra.tables.references.L_COURRIER_CONTACT_ORGANISME
import remocra.db.jooq.remocra.tables.references.L_COURRIER_ORGANISME
import remocra.db.jooq.remocra.tables.references.L_COURRIER_UTILISATEUR

class CourrierUtils {

    fun getAccessCondition(userInfo: WrappedUserInfo): Condition =
        if (userInfo.hasDroit(Droit.COURRIER_ADMIN_R)) {
            DSL.trueCondition()
        } else if (userInfo.hasDroit(Droit.COURRIER_ORGANISME_R)) {
            L_COURRIER_ORGANISME.ORGANISME_ID.`in`(userInfo.affiliatedOrganismeIds)
                .or(COURRIER.EXPEDITEUR.`in`(userInfo.affiliatedOrganismeIds))
        } else if (userInfo.hasDroit(Droit.COURRIER_UTILISATEUR_R)) {
            L_COURRIER_UTILISATEUR.UTILISATEUR_ID.eq(userInfo.utilisateurId)
                .or(
                    L_COURRIER_CONTACT_ORGANISME.COURRIER_ID.isNotNull.and(
                        DSL.exists(
                            DSL.select(CONTACT.ID)
                                .from(CONTACT)
                                .where(
                                    CONTACT.ID.eq(L_COURRIER_CONTACT_ORGANISME.CONTACT_ID)
                                        .and(CONTACT.EMAIL.eq(userInfo.utilisateur?.utilisateurEmail)),
                                ),
                        ),
                    ),
                )
                .or(
                    L_COURRIER_CONTACT_GESTIONNAIRE.COURRIER_ID.isNotNull.and(
                        DSL.exists(
                            DSL.select(CONTACT.ID)
                                .from(CONTACT)
                                .where(
                                    CONTACT.ID.eq(L_COURRIER_CONTACT_GESTIONNAIRE.CONTACT_ID)
                                        .and(CONTACT.EMAIL.eq(userInfo.utilisateur?.utilisateurEmail)),
                                ),
                        ),
                    ),
                )
        } else {
            DSL.falseCondition()
        }
}
