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
import remocra.db.jooq.couverturehydraulique.tables.references.TYPE_ETUDE
import remocra.db.jooq.remocra.tables.references.DIAMETRE
import remocra.db.jooq.remocra.tables.references.DOMAINE
import remocra.db.jooq.remocra.tables.references.MARQUE_PIBI
import remocra.db.jooq.remocra.tables.references.MATERIAU
import remocra.db.jooq.remocra.tables.references.MODELE_PIBI
import remocra.db.jooq.remocra.tables.references.NATURE_DECI
import remocra.db.jooq.remocra.tables.references.NIVEAU
import remocra.db.jooq.remocra.tables.references.PROFIL_ORGANISME
import remocra.db.jooq.remocra.tables.references.PROFIL_UTILISATEUR
import remocra.db.jooq.remocra.tables.references.ROLE_CONTACT
import remocra.db.jooq.remocra.tables.references.THEMATIQUE
import remocra.db.jooq.remocra.tables.references.TYPE_CANALISATION
import remocra.db.jooq.remocra.tables.references.TYPE_ORGANISME
import remocra.db.jooq.remocra.tables.references.TYPE_PENA_ASPIRATION
import remocra.db.jooq.remocra.tables.references.TYPE_RESEAU
import java.util.UUID

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
                TypeNomenclatureCodeLibelle.DIAMETRE -> DIAMETRE
                TypeNomenclatureCodeLibelle.DOMAINE -> DOMAINE
                TypeNomenclatureCodeLibelle.MARQUE_PIBI -> MARQUE_PIBI
                TypeNomenclatureCodeLibelle.MATERIAU -> MATERIAU
                TypeNomenclatureCodeLibelle.MODELE_PIBI -> MODELE_PIBI
                TypeNomenclatureCodeLibelle.NATURE_DECI -> NATURE_DECI
                TypeNomenclatureCodeLibelle.NIVEAU -> NIVEAU
                TypeNomenclatureCodeLibelle.TYPE_CANALISATION -> TYPE_CANALISATION
                TypeNomenclatureCodeLibelle.TYPE_RESEAU -> TYPE_RESEAU
                TypeNomenclatureCodeLibelle.PROFIL_ORGANISME -> PROFIL_ORGANISME
                TypeNomenclatureCodeLibelle.PROFIL_UTILISATEUR -> PROFIL_UTILISATEUR
                TypeNomenclatureCodeLibelle.ROLE_CONTACT -> ROLE_CONTACT
                TypeNomenclatureCodeLibelle.THEMATIQUE -> THEMATIQUE
                TypeNomenclatureCodeLibelle.TYPE_ETUDE -> TYPE_ETUDE
                TypeNomenclatureCodeLibelle.TYPE_ORGANISME -> TYPE_ORGANISME
                TypeNomenclatureCodeLibelle.TYPE_PENA_ASPIRATION -> TYPE_PENA_ASPIRATION
            }

        private fun getIdField(type: TypeNomenclatureCodeLibelle) =
            when (type) {
                TypeNomenclatureCodeLibelle.DIAMETRE -> DIAMETRE.ID
                TypeNomenclatureCodeLibelle.DOMAINE -> DOMAINE.ID
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
                TypeNomenclatureCodeLibelle.TYPE_ETUDE -> TYPE_ETUDE.ID
                TypeNomenclatureCodeLibelle.TYPE_ORGANISME -> TYPE_ORGANISME.ID
                TypeNomenclatureCodeLibelle.TYPE_PENA_ASPIRATION -> TYPE_PENA_ASPIRATION.ID
                TypeNomenclatureCodeLibelle.TYPE_RESEAU -> TYPE_RESEAU.ID
            }

        private fun getCodeField(type: TypeNomenclatureCodeLibelle) =
            when (type) {
                TypeNomenclatureCodeLibelle.DIAMETRE -> DIAMETRE.CODE
                TypeNomenclatureCodeLibelle.DOMAINE -> DOMAINE.CODE
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
                TypeNomenclatureCodeLibelle.TYPE_ETUDE -> TYPE_ETUDE.CODE
                TypeNomenclatureCodeLibelle.TYPE_ORGANISME -> TYPE_ORGANISME.CODE
                TypeNomenclatureCodeLibelle.TYPE_PENA_ASPIRATION -> TYPE_PENA_ASPIRATION.CODE
                TypeNomenclatureCodeLibelle.TYPE_RESEAU -> TYPE_RESEAU.CODE
            }

        private fun getLibelleField(type: TypeNomenclatureCodeLibelle) =
            when (type) {
                TypeNomenclatureCodeLibelle.DIAMETRE -> DIAMETRE.LIBELLE
                TypeNomenclatureCodeLibelle.DOMAINE -> DOMAINE.LIBELLE
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
                TypeNomenclatureCodeLibelle.TYPE_ETUDE -> TYPE_ETUDE.LIBELLE
                TypeNomenclatureCodeLibelle.TYPE_ORGANISME -> TYPE_ORGANISME.LIBELLE
                TypeNomenclatureCodeLibelle.TYPE_PENA_ASPIRATION -> TYPE_PENA_ASPIRATION.LIBELLE
                TypeNomenclatureCodeLibelle.TYPE_RESEAU -> TYPE_RESEAU.LIBELLE
            }

        private fun getActifField(type: TypeNomenclatureCodeLibelle) =
            when (type) {
                TypeNomenclatureCodeLibelle.DIAMETRE -> DIAMETRE.ACTIF
                TypeNomenclatureCodeLibelle.DOMAINE -> DOMAINE.ACTIF
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
                TypeNomenclatureCodeLibelle.TYPE_ETUDE -> TYPE_ETUDE.ACTIF
                TypeNomenclatureCodeLibelle.TYPE_ORGANISME -> TYPE_ORGANISME.ACTIF
                TypeNomenclatureCodeLibelle.TYPE_PENA_ASPIRATION -> TYPE_PENA_ASPIRATION.ACTIF
                TypeNomenclatureCodeLibelle.TYPE_RESEAU -> TYPE_RESEAU.ACTIF
            }

        private fun getProtectedField(type: TypeNomenclatureCodeLibelle) =
            when (type) {
                TypeNomenclatureCodeLibelle.DIAMETRE -> DIAMETRE.PROTECTED
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
    }

    data class InfosFk(
        val idFk: TableField<Record, UUID?>,
        val tableCible: TableImpl<Record>,
        val idCible: TableField<Record, UUID?>,
        val libelleCible: TableField<Record, String?>,
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
                code?.let { DSL.and(getCodeField(type!!).contains(code)) },
                libelle?.let { DSL.and(getLibelleField(type!!).contains(libelle)) },
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
        val libelleFk: String?,
        var type: TypeNomenclatureCodeLibelle?,
    ) {
        fun toCondition(): List<SortField<*>> = listOfNotNull(
            getCodeField(type!!).getSortField(code),
            getLibelleField(type!!).getSortField(libelle),
            getActifField(type!!).getSortField(actif),
            getProtectedField(type!!)?.getSortField(protected),
            getInfosFk(type!!)?.libelleCible?.getSortField(libelleFk),
        )
    }

    fun getAllForAdmin(type: TypeNomenclatureCodeLibelle, params: Params<Filter, Sort>): Collection<NomenclatureCodeLibelleData> =
        dsl.select(getIdField(type).`as`("id"), getCodeField(type).`as`("code"), getLibelleField(type).`as`("libelle"), getActifField(type).`as`("actif"), getProtectedField(type)?.`as`("protected"))
            .let {
                val infosFk = getInfosFk(type)
                if (infosFk != null) {
                    it.select(infosFk.idFk.`as`("idFk"), infosFk.libelleCible.`as`("libelleFk"))
                }
                it
            }
            .from(getTableFromType(type))
            .let {
                val infosFk = getInfosFk(type)
                if (infosFk != null) {
                    it.leftJoin(infosFk.tableCible).on(infosFk.idFk.eq(infosFk.idCible))
                }
                it
            }
            .where(params.filterBy?.takeIf { it.type != null }?.toCondition() ?: DSL.trueCondition())
            .orderBy(
                params.sortBy?.takeIf { it.type != null }?.toCondition()
                    ?: listOf(getCodeField(type)),
            ).limit(params.limit).offset(params.offset)
            .fetchInto()

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
                }
                it
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
                }
                it
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
                }
                it
            }
            .set(getActifField(type), nomenclatureCodeLibelleData.actif)
            .where(getIdField(type).eq(nomenclatureCodeLibelleData.id))
            .and(getProtectedField(type)?.isFalse ?: DSL.noCondition())
            .execute()

    fun delete(type: TypeNomenclatureCodeLibelle, id: UUID): Int =
        dsl.deleteFrom(getTableFromType(type))
            .where(getIdField(type).eq(id))
            .and(getProtectedField(type)?.isFalse ?: DSL.noCondition())
            .execute()
}
