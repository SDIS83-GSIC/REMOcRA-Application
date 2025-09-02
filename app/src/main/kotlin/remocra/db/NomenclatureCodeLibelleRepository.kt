package remocra.db

import jakarta.inject.Inject
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.SortField
import org.jooq.TableField
import org.jooq.impl.DSL
import org.jooq.impl.TableImpl
import remocra.data.NomenclatureCodeLibelleData
import remocra.data.Params
import remocra.data.enums.TypeNomenclatureCodeLibelle
import remocra.db.jooq.couverturehydraulique.tables.references.ETUDE
import remocra.db.jooq.couverturehydraulique.tables.references.PEI_PROJET
import remocra.db.jooq.couverturehydraulique.tables.references.TYPE_ETUDE
import remocra.db.jooq.incoming.tables.references.NEW_PEI
import remocra.db.jooq.remocra.tables.references.ADRESSE_SOUS_TYPE_ELEMENT
import remocra.db.jooq.remocra.tables.references.ADRESSE_TYPE_ANOMALIE
import remocra.db.jooq.remocra.tables.references.ADRESSE_TYPE_ELEMENT
import remocra.db.jooq.remocra.tables.references.ANOMALIE
import remocra.db.jooq.remocra.tables.references.ANOMALIE_CATEGORIE
import remocra.db.jooq.remocra.tables.references.CONTACT
import remocra.db.jooq.remocra.tables.references.CRISE
import remocra.db.jooq.remocra.tables.references.CRISE_CATEGORIE
import remocra.db.jooq.remocra.tables.references.DIAMETRE
import remocra.db.jooq.remocra.tables.references.DOMAINE
import remocra.db.jooq.remocra.tables.references.FONCTION_CONTACT
import remocra.db.jooq.remocra.tables.references.L_ADRESSE_ELEMENT_ADRESSE_TYPE_ANOMALIE
import remocra.db.jooq.remocra.tables.references.L_DASHBOARD_PROFIL
import remocra.db.jooq.remocra.tables.references.L_DIAMETRE_NATURE
import remocra.db.jooq.remocra.tables.references.L_PENA_TYPE_ENGIN
import remocra.db.jooq.remocra.tables.references.L_PROFIL_UTILISATEUR_ORGANISME_DROIT
import remocra.db.jooq.remocra.tables.references.L_THEMATIQUE_COURRIER
import remocra.db.jooq.remocra.tables.references.L_THEMATIQUE_DOCUMENT_HABILITABLE
import remocra.db.jooq.remocra.tables.references.L_THEMATIQUE_MODULE
import remocra.db.jooq.remocra.tables.references.L_TYPE_CRISE_CATEGORIE
import remocra.db.jooq.remocra.tables.references.MARQUE_PIBI
import remocra.db.jooq.remocra.tables.references.MATERIAU
import remocra.db.jooq.remocra.tables.references.MODELE_PIBI
import remocra.db.jooq.remocra.tables.references.NATURE_DECI
import remocra.db.jooq.remocra.tables.references.NIVEAU
import remocra.db.jooq.remocra.tables.references.ORGANISME
import remocra.db.jooq.remocra.tables.references.PEI
import remocra.db.jooq.remocra.tables.references.PENA
import remocra.db.jooq.remocra.tables.references.PENA_ASPIRATION
import remocra.db.jooq.remocra.tables.references.PIBI
import remocra.db.jooq.remocra.tables.references.PROFIL_ORGANISME
import remocra.db.jooq.remocra.tables.references.PROFIL_UTILISATEUR
import remocra.db.jooq.remocra.tables.references.ROLE_CONTACT
import remocra.db.jooq.remocra.tables.references.THEMATIQUE
import remocra.db.jooq.remocra.tables.references.TYPE_CANALISATION
import remocra.db.jooq.remocra.tables.references.TYPE_CRISE
import remocra.db.jooq.remocra.tables.references.TYPE_CRISE_CATEGORIE
import remocra.db.jooq.remocra.tables.references.TYPE_ENGIN
import remocra.db.jooq.remocra.tables.references.TYPE_ORGANISME
import remocra.db.jooq.remocra.tables.references.TYPE_PENA_ASPIRATION
import remocra.db.jooq.remocra.tables.references.TYPE_RESEAU
import remocra.db.jooq.remocra.tables.references.UTILISATEUR
import java.util.UUID
import kotlin.math.absoluteValue

/**
 * Repository "générique" des nomenclatures code-libellé-actif(-protected?).
 * Puisqu'on ne peut pas avoir un type hérité dans nos POJO générés par jooq, on feinte en créant autant d'accesseurs que nécessaire
 */
class NomenclatureCodeLibelleRepository @Inject constructor(private val dsl: DSLContext) : AbstractRepository() {

    /**
     * Besoin du contexte "statique" pour pouvoir appeler ces méthodes dans les classes Data (sort + filterBy)
     */
    companion object {
        private fun getTableFromType(type: TypeNomenclatureCodeLibelle) =
            when (type) {
                TypeNomenclatureCodeLibelle.ADRESSE_TYPE_ANOMALIE -> ADRESSE_TYPE_ANOMALIE
                TypeNomenclatureCodeLibelle.ADRESSE_TYPE_ELEMENT -> ADRESSE_TYPE_ELEMENT
                TypeNomenclatureCodeLibelle.ANOMALIE_CATEGORIE -> ANOMALIE_CATEGORIE
                TypeNomenclatureCodeLibelle.CRISE_CATEGORIE -> CRISE_CATEGORIE
                TypeNomenclatureCodeLibelle.DIAMETRE -> DIAMETRE
                TypeNomenclatureCodeLibelle.DOMAINE -> DOMAINE
                TypeNomenclatureCodeLibelle.FONCTION_CONTACT -> FONCTION_CONTACT
                TypeNomenclatureCodeLibelle.MARQUE_PIBI -> MARQUE_PIBI
                TypeNomenclatureCodeLibelle.MATERIAU -> MATERIAU
                TypeNomenclatureCodeLibelle.MODELE_PIBI -> MODELE_PIBI
                TypeNomenclatureCodeLibelle.NATURE_DECI -> NATURE_DECI
                TypeNomenclatureCodeLibelle.NIVEAU -> NIVEAU
                TypeNomenclatureCodeLibelle.PROFIL_ORGANISME -> PROFIL_ORGANISME
                TypeNomenclatureCodeLibelle.PROFIL_UTILISATEUR -> PROFIL_UTILISATEUR
                TypeNomenclatureCodeLibelle.ROLE_CONTACT -> ROLE_CONTACT
                TypeNomenclatureCodeLibelle.THEMATIQUE -> THEMATIQUE
                TypeNomenclatureCodeLibelle.TYPE_CANALISATION -> TYPE_CANALISATION
                TypeNomenclatureCodeLibelle.TYPE_CRISE -> TYPE_CRISE
                TypeNomenclatureCodeLibelle.TYPE_ENGIN -> TYPE_ENGIN
                TypeNomenclatureCodeLibelle.TYPE_ETUDE -> TYPE_ETUDE
                TypeNomenclatureCodeLibelle.TYPE_ORGANISME -> TYPE_ORGANISME
                TypeNomenclatureCodeLibelle.TYPE_PENA_ASPIRATION -> TYPE_PENA_ASPIRATION
                TypeNomenclatureCodeLibelle.TYPE_RESEAU -> TYPE_RESEAU
            }

        private fun getIdField(type: TypeNomenclatureCodeLibelle) =
            when (type) {
                TypeNomenclatureCodeLibelle.ADRESSE_TYPE_ANOMALIE -> ADRESSE_TYPE_ANOMALIE.ID
                TypeNomenclatureCodeLibelle.ADRESSE_TYPE_ELEMENT -> ADRESSE_TYPE_ELEMENT.ID
                TypeNomenclatureCodeLibelle.ANOMALIE_CATEGORIE -> ANOMALIE_CATEGORIE.ID
                TypeNomenclatureCodeLibelle.CRISE_CATEGORIE -> CRISE_CATEGORIE.ID
                TypeNomenclatureCodeLibelle.DIAMETRE -> DIAMETRE.ID
                TypeNomenclatureCodeLibelle.DOMAINE -> DOMAINE.ID
                TypeNomenclatureCodeLibelle.FONCTION_CONTACT -> FONCTION_CONTACT.ID
                TypeNomenclatureCodeLibelle.MARQUE_PIBI -> MARQUE_PIBI.ID
                TypeNomenclatureCodeLibelle.MATERIAU -> MATERIAU.ID
                TypeNomenclatureCodeLibelle.MODELE_PIBI -> MODELE_PIBI.ID
                TypeNomenclatureCodeLibelle.NATURE_DECI -> NATURE_DECI.ID
                TypeNomenclatureCodeLibelle.NIVEAU -> NIVEAU.ID
                TypeNomenclatureCodeLibelle.PROFIL_ORGANISME -> PROFIL_ORGANISME.ID
                TypeNomenclatureCodeLibelle.PROFIL_UTILISATEUR -> PROFIL_UTILISATEUR.ID
                TypeNomenclatureCodeLibelle.ROLE_CONTACT -> ROLE_CONTACT.ID
                TypeNomenclatureCodeLibelle.THEMATIQUE -> THEMATIQUE.ID
                TypeNomenclatureCodeLibelle.TYPE_CANALISATION -> TYPE_CANALISATION.ID
                TypeNomenclatureCodeLibelle.TYPE_CRISE -> TYPE_CRISE.ID
                TypeNomenclatureCodeLibelle.TYPE_ENGIN -> TYPE_ENGIN.ID
                TypeNomenclatureCodeLibelle.TYPE_ETUDE -> TYPE_ETUDE.ID
                TypeNomenclatureCodeLibelle.TYPE_ORGANISME -> TYPE_ORGANISME.ID
                TypeNomenclatureCodeLibelle.TYPE_PENA_ASPIRATION -> TYPE_PENA_ASPIRATION.ID
                TypeNomenclatureCodeLibelle.TYPE_RESEAU -> TYPE_RESEAU.ID
            }

        private fun getCodeField(type: TypeNomenclatureCodeLibelle) =
            when (type) {
                TypeNomenclatureCodeLibelle.ADRESSE_TYPE_ANOMALIE -> ADRESSE_TYPE_ANOMALIE.CODE
                TypeNomenclatureCodeLibelle.ADRESSE_TYPE_ELEMENT -> ADRESSE_TYPE_ELEMENT.CODE
                TypeNomenclatureCodeLibelle.ANOMALIE_CATEGORIE -> ANOMALIE_CATEGORIE.CODE
                TypeNomenclatureCodeLibelle.CRISE_CATEGORIE -> CRISE_CATEGORIE.CODE
                TypeNomenclatureCodeLibelle.DIAMETRE -> DIAMETRE.CODE
                TypeNomenclatureCodeLibelle.DOMAINE -> DOMAINE.CODE
                TypeNomenclatureCodeLibelle.FONCTION_CONTACT -> FONCTION_CONTACT.CODE
                TypeNomenclatureCodeLibelle.MARQUE_PIBI -> MARQUE_PIBI.CODE
                TypeNomenclatureCodeLibelle.MATERIAU -> MATERIAU.CODE
                TypeNomenclatureCodeLibelle.MODELE_PIBI -> MODELE_PIBI.CODE
                TypeNomenclatureCodeLibelle.NATURE_DECI -> NATURE_DECI.CODE
                TypeNomenclatureCodeLibelle.NIVEAU -> NIVEAU.CODE
                TypeNomenclatureCodeLibelle.PROFIL_ORGANISME -> PROFIL_ORGANISME.CODE
                TypeNomenclatureCodeLibelle.PROFIL_UTILISATEUR -> PROFIL_UTILISATEUR.CODE
                TypeNomenclatureCodeLibelle.ROLE_CONTACT -> ROLE_CONTACT.CODE
                TypeNomenclatureCodeLibelle.THEMATIQUE -> THEMATIQUE.CODE
                TypeNomenclatureCodeLibelle.TYPE_CANALISATION -> TYPE_CANALISATION.CODE
                TypeNomenclatureCodeLibelle.TYPE_CRISE -> TYPE_CRISE.CODE
                TypeNomenclatureCodeLibelle.TYPE_ENGIN -> TYPE_ENGIN.CODE
                TypeNomenclatureCodeLibelle.TYPE_ETUDE -> TYPE_ETUDE.CODE
                TypeNomenclatureCodeLibelle.TYPE_ORGANISME -> TYPE_ORGANISME.CODE
                TypeNomenclatureCodeLibelle.TYPE_PENA_ASPIRATION -> TYPE_PENA_ASPIRATION.CODE
                TypeNomenclatureCodeLibelle.TYPE_RESEAU -> TYPE_RESEAU.CODE
            }

        private fun getLibelleField(type: TypeNomenclatureCodeLibelle) =
            when (type) {
                TypeNomenclatureCodeLibelle.ADRESSE_TYPE_ANOMALIE -> ADRESSE_TYPE_ANOMALIE.LIBELLE
                TypeNomenclatureCodeLibelle.ADRESSE_TYPE_ELEMENT -> ADRESSE_TYPE_ELEMENT.LIBELLE
                TypeNomenclatureCodeLibelle.ANOMALIE_CATEGORIE -> ANOMALIE_CATEGORIE.LIBELLE
                TypeNomenclatureCodeLibelle.CRISE_CATEGORIE -> CRISE_CATEGORIE.LIBELLE
                TypeNomenclatureCodeLibelle.DIAMETRE -> DIAMETRE.LIBELLE
                TypeNomenclatureCodeLibelle.DOMAINE -> DOMAINE.LIBELLE
                TypeNomenclatureCodeLibelle.FONCTION_CONTACT -> FONCTION_CONTACT.LIBELLE
                TypeNomenclatureCodeLibelle.MARQUE_PIBI -> MARQUE_PIBI.LIBELLE
                TypeNomenclatureCodeLibelle.MATERIAU -> MATERIAU.LIBELLE
                TypeNomenclatureCodeLibelle.MODELE_PIBI -> MODELE_PIBI.LIBELLE
                TypeNomenclatureCodeLibelle.NATURE_DECI -> NATURE_DECI.LIBELLE
                TypeNomenclatureCodeLibelle.NIVEAU -> NIVEAU.LIBELLE
                TypeNomenclatureCodeLibelle.PROFIL_ORGANISME -> PROFIL_ORGANISME.LIBELLE
                TypeNomenclatureCodeLibelle.PROFIL_UTILISATEUR -> PROFIL_UTILISATEUR.LIBELLE
                TypeNomenclatureCodeLibelle.ROLE_CONTACT -> ROLE_CONTACT.LIBELLE
                TypeNomenclatureCodeLibelle.THEMATIQUE -> THEMATIQUE.LIBELLE
                TypeNomenclatureCodeLibelle.TYPE_CANALISATION -> TYPE_CANALISATION.LIBELLE
                TypeNomenclatureCodeLibelle.TYPE_CRISE -> TYPE_CRISE.LIBELLE
                TypeNomenclatureCodeLibelle.TYPE_ETUDE -> TYPE_ETUDE.LIBELLE
                TypeNomenclatureCodeLibelle.TYPE_ENGIN -> TYPE_ENGIN.LIBELLE
                TypeNomenclatureCodeLibelle.TYPE_ORGANISME -> TYPE_ORGANISME.LIBELLE
                TypeNomenclatureCodeLibelle.TYPE_PENA_ASPIRATION -> TYPE_PENA_ASPIRATION.LIBELLE
                TypeNomenclatureCodeLibelle.TYPE_RESEAU -> TYPE_RESEAU.LIBELLE
            }

        private fun getActifField(type: TypeNomenclatureCodeLibelle) =
            when (type) {
                TypeNomenclatureCodeLibelle.ADRESSE_TYPE_ANOMALIE -> ADRESSE_TYPE_ANOMALIE.ACTIF
                TypeNomenclatureCodeLibelle.ADRESSE_TYPE_ELEMENT -> ADRESSE_TYPE_ELEMENT.ACTIF
                TypeNomenclatureCodeLibelle.ANOMALIE_CATEGORIE -> ANOMALIE_CATEGORIE.ACTIF
                TypeNomenclatureCodeLibelle.CRISE_CATEGORIE -> CRISE_CATEGORIE.ACTIF
                TypeNomenclatureCodeLibelle.DIAMETRE -> DIAMETRE.ACTIF
                TypeNomenclatureCodeLibelle.DOMAINE -> DOMAINE.ACTIF
                TypeNomenclatureCodeLibelle.FONCTION_CONTACT -> FONCTION_CONTACT.ACTIF
                TypeNomenclatureCodeLibelle.MARQUE_PIBI -> MARQUE_PIBI.ACTIF
                TypeNomenclatureCodeLibelle.MATERIAU -> MATERIAU.ACTIF
                TypeNomenclatureCodeLibelle.MODELE_PIBI -> MODELE_PIBI.ACTIF
                TypeNomenclatureCodeLibelle.NATURE_DECI -> NATURE_DECI.ACTIF
                TypeNomenclatureCodeLibelle.NIVEAU -> NIVEAU.ACTIF
                TypeNomenclatureCodeLibelle.PROFIL_ORGANISME -> PROFIL_ORGANISME.ACTIF
                TypeNomenclatureCodeLibelle.PROFIL_UTILISATEUR -> PROFIL_UTILISATEUR.ACTIF
                TypeNomenclatureCodeLibelle.ROLE_CONTACT -> ROLE_CONTACT.ACTIF
                TypeNomenclatureCodeLibelle.THEMATIQUE -> THEMATIQUE.ACTIF
                TypeNomenclatureCodeLibelle.TYPE_CANALISATION -> TYPE_CANALISATION.ACTIF
                TypeNomenclatureCodeLibelle.TYPE_CRISE -> TYPE_CRISE.ACTIF
                TypeNomenclatureCodeLibelle.TYPE_ENGIN -> TYPE_ENGIN.ACTIF
                TypeNomenclatureCodeLibelle.TYPE_ETUDE -> TYPE_ETUDE.ACTIF
                TypeNomenclatureCodeLibelle.TYPE_ORGANISME -> TYPE_ORGANISME.ACTIF
                TypeNomenclatureCodeLibelle.TYPE_PENA_ASPIRATION -> TYPE_PENA_ASPIRATION.ACTIF
                TypeNomenclatureCodeLibelle.TYPE_RESEAU -> TYPE_RESEAU.ACTIF
            }

        private fun getProtectedField(type: TypeNomenclatureCodeLibelle) =
            when (type) {
                TypeNomenclatureCodeLibelle.ANOMALIE_CATEGORIE -> ANOMALIE_CATEGORIE.PROTECTED
                TypeNomenclatureCodeLibelle.DIAMETRE -> DIAMETRE.PROTECTED
                TypeNomenclatureCodeLibelle.FONCTION_CONTACT -> FONCTION_CONTACT.PROTECTED
                TypeNomenclatureCodeLibelle.NATURE_DECI -> NATURE_DECI.PROTECTED
                TypeNomenclatureCodeLibelle.TYPE_ORGANISME -> TYPE_ORGANISME.PROTECTED
                TypeNomenclatureCodeLibelle.ROLE_CONTACT -> ROLE_CONTACT.PROTECTED
                TypeNomenclatureCodeLibelle.THEMATIQUE -> THEMATIQUE.PROTECTED
                else -> null
            }

        /**
         * Retourne les infos relatives à la FK : le champ concerné, la table cible, et les champs ID et libellé de la table cible
         */
        private fun getInfosFk(type: TypeNomenclatureCodeLibelle): InfosFk? =
            when (type) {
                TypeNomenclatureCodeLibelle.MODELE_PIBI -> InfosFk(MODELE_PIBI.MARQUE_ID, MARQUE_PIBI, MARQUE_PIBI.ID, MARQUE_PIBI.LIBELLE)
                TypeNomenclatureCodeLibelle.PROFIL_ORGANISME -> InfosFk(PROFIL_ORGANISME.TYPE_ORGANISME_ID, TYPE_ORGANISME, TYPE_ORGANISME.ID, TYPE_ORGANISME.LIBELLE)
                TypeNomenclatureCodeLibelle.PROFIL_UTILISATEUR -> InfosFk(
                    PROFIL_UTILISATEUR.TYPE_ORGANISME_ID,
                    TYPE_ORGANISME,
                    TYPE_ORGANISME.ID,
                    TYPE_ORGANISME.LIBELLE,
                )
                TypeNomenclatureCodeLibelle.TYPE_ORGANISME ->
                    InfosFk(
                        TYPE_ORGANISME.PARENT_ID,
                        TYPE_ORGANISME.`as`("typeOrganismeFk"),
                        TYPE_ORGANISME.`as`("typeOrganismeFk").ID,
                        TYPE_ORGANISME.`as`("typeOrganismeFk").LIBELLE,
                    )
                else -> null
            }

        /**
         * Retourne une liste des (tables + nom du champ) à requêter pour vérifier la supprimabilité d'un élément
         */
        private fun getInfosFkCible(type: TypeNomenclatureCodeLibelle) =
            when (type) {
                TypeNomenclatureCodeLibelle.ADRESSE_TYPE_ANOMALIE -> setOf(InfosFkCible(L_ADRESSE_ELEMENT_ADRESSE_TYPE_ANOMALIE, L_ADRESSE_ELEMENT_ADRESSE_TYPE_ANOMALIE.ADRESSE_TYPE_ANOMALIE_ID))
                TypeNomenclatureCodeLibelle.ADRESSE_TYPE_ELEMENT -> setOf(InfosFkCible(ADRESSE_SOUS_TYPE_ELEMENT, ADRESSE_SOUS_TYPE_ELEMENT.TYPE_ELEMENT))
                TypeNomenclatureCodeLibelle.ANOMALIE_CATEGORIE -> setOf(InfosFkCible(ANOMALIE, ANOMALIE.ANOMALIE_CATEGORIE_ID))
                TypeNomenclatureCodeLibelle.CRISE_CATEGORIE -> setOf(
                    InfosFkCible(L_TYPE_CRISE_CATEGORIE, L_TYPE_CRISE_CATEGORIE.CRISE_CATEGORIE_ID),
                    InfosFkCible(TYPE_CRISE_CATEGORIE, TYPE_CRISE_CATEGORIE.CRISE_CATEGORIE_ID),
                )
                TypeNomenclatureCodeLibelle.DIAMETRE -> setOf(InfosFkCible(PIBI, PIBI.DIAMETRE_ID), InfosFkCible(L_DIAMETRE_NATURE, L_DIAMETRE_NATURE.DIAMETRE_ID), InfosFkCible(PEI_PROJET, PEI_PROJET.DIAMETRE_ID))
                TypeNomenclatureCodeLibelle.DOMAINE -> setOf(InfosFkCible(PEI, PEI.DOMAINE_ID), InfosFkCible(NEW_PEI, NEW_PEI.DOMAINE_ID))
                TypeNomenclatureCodeLibelle.FONCTION_CONTACT -> setOf(
                    InfosFkCible(CONTACT, CONTACT.FONCTION_CONTACT_ID),
                    InfosFkCible(remocra.db.jooq.incoming.tables.references.CONTACT, remocra.db.jooq.incoming.tables.references.CONTACT.FONCTION_CONTACT_ID),
                )
                TypeNomenclatureCodeLibelle.MARQUE_PIBI -> setOf(InfosFkCible(MODELE_PIBI, MODELE_PIBI.MARQUE_ID), InfosFkCible(PIBI, PIBI.MARQUE_PIBI_ID))
                TypeNomenclatureCodeLibelle.MATERIAU -> setOf(InfosFkCible(PENA, PENA.MATERIAU_ID))
                TypeNomenclatureCodeLibelle.MODELE_PIBI -> setOf(InfosFkCible(PIBI, PIBI.MODELE_PIBI_ID))
                TypeNomenclatureCodeLibelle.NATURE_DECI -> setOf(InfosFkCible(PEI, PEI.NATURE_DECI_ID), InfosFkCible(NEW_PEI, NEW_PEI.NATURE_DECI_ID), InfosFkCible(PEI_PROJET, PEI_PROJET.NATURE_DECI_ID))
                TypeNomenclatureCodeLibelle.NIVEAU -> setOf(InfosFkCible(PEI, PEI.NIVEAU_ID))
                TypeNomenclatureCodeLibelle.TYPE_CANALISATION -> setOf(InfosFkCible(PIBI, PIBI.TYPE_CANALISATION_ID))
                TypeNomenclatureCodeLibelle.TYPE_RESEAU -> setOf(InfosFkCible(PIBI, PIBI.TYPE_RESEAU_ID))
                TypeNomenclatureCodeLibelle.PROFIL_ORGANISME -> setOf(InfosFkCible(ORGANISME, ORGANISME.PROFIL_ORGANISME_ID), InfosFkCible(L_PROFIL_UTILISATEUR_ORGANISME_DROIT, L_PROFIL_UTILISATEUR_ORGANISME_DROIT.PROFIL_ORGANISME_ID))
                TypeNomenclatureCodeLibelle.PROFIL_UTILISATEUR -> setOf(
                    InfosFkCible(L_DASHBOARD_PROFIL, L_DASHBOARD_PROFIL.PROFIL_UTILISATEUR_ID),
                    InfosFkCible(L_PROFIL_UTILISATEUR_ORGANISME_DROIT, L_PROFIL_UTILISATEUR_ORGANISME_DROIT.PROFIL_UTILISATEUR_ID),
                    InfosFkCible(UTILISATEUR, UTILISATEUR.PROFIL_UTILISATEUR_ID),
                )
                TypeNomenclatureCodeLibelle.ROLE_CONTACT -> setOf()
                TypeNomenclatureCodeLibelle.THEMATIQUE -> setOf(
                    InfosFkCible(L_THEMATIQUE_COURRIER, L_THEMATIQUE_COURRIER.THEMATIQUE_ID),
                    InfosFkCible(L_THEMATIQUE_MODULE, L_THEMATIQUE_MODULE.THEMATIQUE_ID),
                    InfosFkCible(L_THEMATIQUE_DOCUMENT_HABILITABLE, L_THEMATIQUE_DOCUMENT_HABILITABLE.THEMATIQUE_ID),
                )
                TypeNomenclatureCodeLibelle.TYPE_ENGIN -> setOf(InfosFkCible(L_PENA_TYPE_ENGIN, L_PENA_TYPE_ENGIN.TYPE_ENGIN_ID))
                TypeNomenclatureCodeLibelle.TYPE_ETUDE -> setOf(InfosFkCible(ETUDE, ETUDE.TYPE_ETUDE_ID))
                TypeNomenclatureCodeLibelle.TYPE_ORGANISME -> setOf(
                    InfosFkCible(PROFIL_UTILISATEUR, PROFIL_UTILISATEUR.TYPE_ORGANISME_ID),
                    InfosFkCible(PROFIL_ORGANISME, PROFIL_ORGANISME.TYPE_ORGANISME_ID),
                    InfosFkCible(ORGANISME, ORGANISME.TYPE_ORGANISME_ID),
                )
                TypeNomenclatureCodeLibelle.TYPE_CRISE -> setOf(
                    InfosFkCible(CRISE, CRISE.TYPE_CRISE_ID),
                    InfosFkCible(L_TYPE_CRISE_CATEGORIE, L_TYPE_CRISE_CATEGORIE.TYPE_CRISE_ID),
                )
                TypeNomenclatureCodeLibelle.TYPE_PENA_ASPIRATION -> setOf(InfosFkCible(PENA_ASPIRATION, PENA_ASPIRATION.TYPE_PENA_ASPIRATION_ID))
            }
    }

    data class InfosFk(
        val idFk: TableField<Record, UUID?>,
        val tableCible: TableImpl<Record>,
        val idCible: TableField<Record, UUID?>,
        val libelleCible: TableField<Record, String?>,
    )

    /**
     * Pour vérifier la supprimabilité de l'élément, on doit stocker les éléments qui le référencent : table source, nom du field FK dans cette table
     */
    data class InfosFkCible(
        val tableSource: TableImpl<Record>,
        val idFkSource: TableField<Record, UUID?>,
    )

    data class Filter(
        val code: String?,
        val libelle: String?,
        val actif: Boolean?,
        val protected: Boolean?,
        val idFk: UUID?,
        var type: TypeNomenclatureCodeLibelle?,
    ) {
        fun toCondition(): Condition = DSL.and(
            listOfNotNull(
                actif?.let { DSL.and(getActifField(type!!).eq(actif)) },
                code?.let { DSL.and(getCodeField(type!!).containsIgnoreCaseUnaccent(code)) },
                libelle?.let { DSL.and(getLibelleField(type!!).containsIgnoreCaseUnaccent(libelle)) },
                protected?.let { DSL.and(getProtectedField(type!!)?.eq(protected) ?: DSL.noCondition()) },
                idFk?.let { DSL.and(getInfosFk(type!!)?.idFk?.eq(idFk) ?: DSL.noCondition()) },
            ),

        )
    }

    data class Sort(
        val code: Int?,
        val libelle: Int?,
        val actif: Int?,
        val protected: Int?,
        val libelleFk: Int?,
        var type: TypeNomenclatureCodeLibelle?,
    ) {
        fun getPairsToSort(): List<Pair<String, Int>> = listOfNotNull(
            code?.let { "code" to it },
            libelle?.let { "libelle" to it },
            actif?.let { "actif" to it },
            protected?.let { "protected" to it },
            libelleFk?.let { "libelleFk" to it },
        )

        fun toCondition(): List<SortField<*>> = getPairsToSort().sortedBy { it.second.absoluteValue }.mapNotNull { pair ->
            when (pair.first) {
                "code" -> getCodeField(type!!).getSortField(pair.second)
                "libelle" -> getLibelleField(type!!).getSortField(pair.second)
                "actif" -> getActifField(type!!).getSortField(pair.second)
                "protected" -> getProtectedField(type!!)?.getSortField(pair.second)
                "libelleFk" -> getInfosFk(type!!)?.libelleCible?.getSortField(pair.second)
                else -> null
            }
        }
    }

    fun getAllForAdmin(type: TypeNomenclatureCodeLibelle, params: Params<Filter, Sort>): Collection<NomenclatureCodeLibelleData> =
        dsl.select(getIdField(type).`as`("id"), getCodeField(type).`as`("code"), getLibelleField(type).`as`("libelle"), getActifField(type).`as`("actif"), getProtectedField(type)?.`as`("protected"))
            .let {
                val infosFk = getInfosFk(type)
                if (infosFk != null) {
                    it.select(infosFk.idFk.`as`("idFk"), infosFk.libelleCible.`as`("libelleFk"))
                } else {
                    it
                }
            }
            .from(getTableFromType(type))
            .let {
                val infosFk = getInfosFk(type)
                if (infosFk != null) {
                    it.leftJoin(infosFk.tableCible).on(infosFk.idFk.eq(infosFk.idCible))
                } else {
                    it
                }
            }
            .where(params.filterBy?.takeIf { it.type != null }?.toCondition() ?: DSL.trueCondition())
            .orderBy(
                params.sortBy?.takeIf { it.type != null }?.toCondition().takeIf { !it.isNullOrEmpty() } ?: listOf(getCodeField(type)),
            ).limit(params.limit).offset(params.offset)
            .fetchInto<NomenclatureCodeLibelleData>().map { nomenc ->
                nomenc.copy(tablesDependantes = canDelete(type, nomenc.id))
            }

    fun getCountForAdmin(type: TypeNomenclatureCodeLibelle, params: Params<Filter, Sort>): Int =
        dsl.selectCount().from(getTableFromType(type)).where(
            params.filterBy?.toCondition()
                ?: DSL.trueCondition(),
        ).fetchSingleInto()

    fun getById(type: TypeNomenclatureCodeLibelle, id: UUID): NomenclatureCodeLibelleData? =
        dsl.select(
            getIdField(type).`as`("id"),
            getCodeField(type).`as`("code"),
            getLibelleField(type).`as`("libelle"),
            getActifField(type).`as`("actif"),
            getProtectedField(type)?.`as`("protected"),
        )
            .let {
                val infosFk = getInfosFk(type)
                if (infosFk != null) {
                    it.select(infosFk.idFk.`as`("idFk"))
                } else {
                    it
                }
            }
            .from(getTableFromType(type)).where(getIdField(type).eq(id)).fetchOneInto()

    fun create(type: TypeNomenclatureCodeLibelle, nomenclatureCodeLibelleData: NomenclatureCodeLibelleData): Int =
        dsl.insertInto(
            getTableFromType(type),
        )
            .set(getIdField(type), nomenclatureCodeLibelleData.id)
            .set(getCodeField(type), nomenclatureCodeLibelleData.code)
            .set(getLibelleField(type), nomenclatureCodeLibelleData.libelle)
            .set(getActifField(type), nomenclatureCodeLibelleData.actif)
            .let {
                if (getProtectedField(type) != null) {
                    it.set(getProtectedField(type), nomenclatureCodeLibelleData.protected)
                } else {
                    it
                }
            }
            .let {
                if (getInfosFk(type) != null) {
                    it.set(getInfosFk(type)!!.idFk, nomenclatureCodeLibelleData.idFk)
                } else {
                    it
                }
            }
            .execute()

    fun update(type: TypeNomenclatureCodeLibelle, nomenclatureCodeLibelleData: NomenclatureCodeLibelleData): Int =
        dsl.update(getTableFromType(type))
            .set(getCodeField(type), nomenclatureCodeLibelleData.code)
            .set(getLibelleField(type), nomenclatureCodeLibelleData.libelle)
            .set(getActifField(type), nomenclatureCodeLibelleData.actif)
            .let {
                val infosFk = getInfosFk(type)
                if (infosFk != null) {
                    it.set(infosFk.idFk, nomenclatureCodeLibelleData.idFk)
                } else {
                    it
                }
            }
            .where(getIdField(type).eq(nomenclatureCodeLibelleData.id))
            .execute()

    fun delete(type: TypeNomenclatureCodeLibelle, id: UUID): Int =
        dsl.deleteFrom(getTableFromType(type))
            .where(getIdField(type).eq(id))
            .and(getProtectedField(type)?.isFalse ?: DSL.noCondition())
            .execute()

    /**
     * Vérifie s'il existe déjà un élément avec ce *code*. En modification, on regarde si le code existe pour un autre élément que lui-même
     */
    fun checkCodeExists(type: TypeNomenclatureCodeLibelle, code: String, id: UUID?) = dsl.fetchExists(
        dsl.select(getIdField(type))
            .from(getTableFromType(type))
            .where(getCodeField(type).equalIgnoreCase(code))
            .and(id?.let { DSL.and(getIdField(type).notEqual(id)) }),
    )

    /**
     * Vérifie si l'élément est utilisé comme FK, et retourne la liste des tables concernées le cas échéant, ou un conteneur vide sinon
     */
    private fun canDelete(type: TypeNomenclatureCodeLibelle, id: UUID): Set<String> {
        val setRetour: MutableSet<String> = mutableSetOf()
        getInfosFkCible(type).forEach { infoFkCible ->
            // On construit le fetchExists dans chaque table
            dsl.fetchExists(infoFkCible.tableSource, DSL.condition(infoFkCible.idFkSource.eq(id))).let { if (it) setRetour.add(infoFkCible.tableSource.name) }
        }

        return setRetour
    }
}
