/*
 * This file is generated by jOOQ.
 */
package remocra.db.jooq.historique.enums

import org.jooq.Catalog
import org.jooq.EnumType
import org.jooq.Schema
import remocra.db.jooq.historique.Historique
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
enum class TypeObjet(@get:JvmName("literal") public val literal: String) : EnumType {
    PEI("PEI"),
    VISITE("VISITE"),
    PARAMETRE("PARAMETRE"),
    GESTIONNAIRE("GESTIONNAIRE"),
    SITE("SITE"),
    PENA_ASPIRATION("PENA_ASPIRATION"),
    DOCUMENT_PEI("DOCUMENT_PEI"),
    TOURNEE("TOURNEE"),
    TOURNEE_PEI("TOURNEE_PEI"),
    ETUDE("ETUDE"),
    PEI_PROJET("PEI_PROJET"),
    DOCUMENT_ETUDE("DOCUMENT_ETUDE"),
    INDISPONIBILITE_TEMPORAIRE("INDISPONIBILITE_TEMPORAIRE"),
    DIAMETRE("DIAMETRE"),
    NATURE("NATURE"),
    ORGANISME("ORGANISME"),
    PROFIL_ORGANISME("PROFIL_ORGANISME"),
    PROFIL_UTILISATEUR("PROFIL_UTILISATEUR"),
    TYPE_ORGANISME("TYPE_ORGANISME"),
    DOMAINE("DOMAINE"),
    TYPE_CANALISATION("TYPE_CANALISATION"),
    TYPE_ETUDE("TYPE_ETUDE"),
    TYPE_PENA_ASPIRATION("TYPE_PENA_ASPIRATION"),
    MODELE_PIBI("MODELE_PIBI"),
    NATURE_DECI("NATURE_DECI"),
    MATERIAU("MATERIAU"),
    MARQUE_PIBI("MARQUE_PIBI"),
    TYPE_RESEAU("TYPE_RESEAU"),
    NIVEAU("NIVEAU"),
    CONTACT("CONTACT"),
    ROLE_CONTACT("ROLE_CONTACT"),
    FONCTION_CONTACT("FONCTION_CONTACT"),
    THEMATIQUE("THEMATIQUE"),
    BLOC_DOCUMENT("BLOC_DOCUMENT"),
    ;
    override fun getCatalog(): Catalog? = schema.catalog
    override fun getSchema(): Schema = Historique.HISTORIQUE
    override fun getName(): String = "type_objet"
    override fun getLiteral(): String = literal
}
