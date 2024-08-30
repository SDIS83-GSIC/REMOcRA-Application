/*
 * This file is generated by jOOQ.
 */
package remocra.db.jooq.remocra.enums

import org.jooq.Catalog
import org.jooq.EnumType
import org.jooq.Schema
import remocra.db.jooq.remocra.Remocra
import javax.annotation.processing.Generated

/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = [
        "https://www.jooq.org",
        "jOOQ version:3.19.11",
    ],
    comments = "This class is generated by jOOQ",
)
@Suppress("UNCHECKED_CAST")
enum class TypeParametreCourrier(@get:JvmName("literal") public val literal: String) : EnumType {
    COMMUNE_ID("COMMUNE_ID"),
    GESTIONNAIRE_ID("GESTIONNAIRE_ID"),
    IS_ONLY_PUBLIC("IS_ONLY_PUBLIC"),
    IS_EPCI("IS_EPCI"),
    PROFIL_UTILISATEUR_ID("PROFIL_UTILISATEUR_ID"),
    ANNEE("ANNEE"),
    EXPEDITEUR_GRADE("EXPEDITEUR_GRADE"),
    EXPEDITEUR_STATUT("EXPEDITEUR_STATUT"),
    REFERENCE("REFERENCE"),
    CIS_ID("CIS_ID"),
    ;
    override fun getCatalog(): Catalog? = schema.catalog
    override fun getSchema(): Schema = Remocra.REMOCRA
    override fun getName(): String = "type_parametre_courrier"
    override fun getLiteral(): String = literal
}
