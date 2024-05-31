/*
 * This file is generated by jOOQ.
 */
package remocra.db.jooq.enums

import org.jooq.Catalog
import org.jooq.EnumType
import org.jooq.Schema
import remocra.db.jooq.Remocra
import javax.annotation.processing.Generated

/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = [
        "https://www.jooq.org",
        "jOOQ version:3.19.3",
    ],
    comments = "This class is generated by jOOQ",
)
@Suppress("UNCHECKED_CAST")
enum class TypeTask(@get:JvmName("literal") public val literal: String) : EnumType {
    BASCULE_AUTO_INDISPO_TEMP("BASCULE_AUTO_INDISPO_TEMP"),
    DEPARTS_DE_FEU("DEPARTS_DE_FEU"),
    ETAT_DES_HYDRANTS_PENA_INDISPONIBLES("ETAT_DES_HYDRANTS_PENA_INDISPONIBLES"),
    ETAT_DES_HYDRANTS_PENA("ETAT_DES_HYDRANTS_PENA"),
    ETAT_DES_HYDRANTS_PIBI_INDISPONIBLES("ETAT_DES_HYDRANTS_PIBI_INDISPONIBLES"),
    ETAT_DES_HYDRANTS_PIBI("ETAT_DES_HYDRANTS_PIBI"),
    ETAT_DES_PEI_NON_RECEPTIONNES("ETAT_DES_PEI_NON_RECEPTIONNES"),
    EXPORTER_CTP("EXPORTER_CTP"),
    EXPORTER_DONNEES_FROM_MODELE("EXPORTER_DONNEES_FROM_MODELE"),
    EXPORTER_REMOCRA_VERS_SIG("EXPORTER_REMOCRA_VERS_SIG"),
    GENERER_EXPORT_REQUETES_PERSO("GENERER_EXPORT_REQUETES_PERSO"),
    IMPORTER_FICHIER_SHP_COUV_HYDRAU("IMPORTER_FICHIER_SHP_COUV_HYDRAU"),
    IMPORTER_SIG("IMPORTER_SIG"),
    IMPRESSION_FICHE_OLDEB("IMPRESSION_FICHE_OLDEB"),
    INSDISPO_SUR_LE_CARRE_DES_9("INSDISPO_SUR_LE_CARRE_DES_9"),
    LISTE_DES_PERMIS("LISTE_DES_PERMIS"),
    MAJ_LES_POSITIONS_PEI("MAJ_LES_POSITIONS_PEI"),
    MAJ_ZONE_COMPETENCE_COMMUNE("MAJ_ZONE_COMPETENCE_COMMUNE"),
    MISE_EN_INDISPO_PEI__COURRIER_INFO("MISE_EN_INDISPO_PEI__COURRIER_INFO"),
    NB_ALERTES_PAR_UTILISATEUR_CASERNE("NB_ALERTES_PAR_UTILISATEUR_CASERNE"),
    NOTIFIER_BDECI_RECOP_DE_LA_JOURNEE("NOTIFIER_BDECI_RECOP_DE_LA_JOURNEE"),
    NOTIFIER_CHANGEMENTS_ETAT("NOTIFIER_CHANGEMENTS_ETAT"),
    NOTIFIER_CIS_ROI("NOTIFIER_CIS_ROI"),
    NOTIFIER_MAIRES_PEI_INDISPO("NOTIFIER_MAIRES_PEI_INDISPO"),
    NOTIFIER_MAIRES_ROI("NOTIFIER_MAIRES_ROI"),
    NOTIFIER_ROP("NOTIFIER_ROP"),
    NOTIFIER_UTILISATEURS("NOTIFIER_UTILISATEURS"),
    PEI_A_NUMEROTER("PEI_A_NUMEROTER"),
    PURGER("PURGER"),
    REFERENCER_MODELES("REFERENCER_MODELES"),
    ROP__COURRIER_DE_RAPPORT("ROP__COURRIER_DE_RAPPORT"),
    ROP__COURRIER_INFO_PREALABLE("ROP__COURRIER_INFO_PREALABLE"),
    SUPPR_FICHIER_KML_RISQUES_TECHNO("SUPPR_FICHIER_KML_RISQUES_TECHNO"),
    SYNCHRONISER_AVEC_PREVARISC("SYNCHRONISER_AVEC_PREVARISC"),
    TELECHARGEMENT_DES_FICHES_ATLAS("TELECHARGEMENT_DES_FICHES_ATLAS"),
    ;
    override fun getCatalog(): Catalog? = schema.catalog
    override fun getSchema(): Schema = Remocra.REMOCRA
    override fun getName(): String = "type_task"
    override fun getLiteral(): String = literal
}
