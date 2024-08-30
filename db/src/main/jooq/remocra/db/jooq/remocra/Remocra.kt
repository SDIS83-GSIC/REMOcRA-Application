/*
 * This file is generated by jOOQ.
 */
package remocra.db.jooq.remocra

import org.jooq.Catalog
import org.jooq.Table
import org.jooq.impl.SchemaImpl
import remocra.db.jooq.DefaultCatalog
import remocra.db.jooq.remocra.tables.Anomalie
import remocra.db.jooq.remocra.tables.AnomalieCategorie
import remocra.db.jooq.remocra.tables.Api
import remocra.db.jooq.remocra.tables.Commune
import remocra.db.jooq.remocra.tables.Contact
import remocra.db.jooq.remocra.tables.Diametre
import remocra.db.jooq.remocra.tables.Document
import remocra.db.jooq.remocra.tables.Domaine
import remocra.db.jooq.remocra.tables.Gestionnaire
import remocra.db.jooq.remocra.tables.Job
import remocra.db.jooq.remocra.tables.LCommuneCis
import remocra.db.jooq.remocra.tables.LContactGestionnaire
import remocra.db.jooq.remocra.tables.LContactOrganisme
import remocra.db.jooq.remocra.tables.LContactRole
import remocra.db.jooq.remocra.tables.LDiametreNature
import remocra.db.jooq.remocra.tables.LPeiAnomalie
import remocra.db.jooq.remocra.tables.LPeiDocument
import remocra.db.jooq.remocra.tables.LProfilUtilisateurOrganismeDroit
import remocra.db.jooq.remocra.tables.LTourneePei
import remocra.db.jooq.remocra.tables.LVisiteAnomalie
import remocra.db.jooq.remocra.tables.LieuDit
import remocra.db.jooq.remocra.tables.LogLine
import remocra.db.jooq.remocra.tables.MarquePibi
import remocra.db.jooq.remocra.tables.Materiau
import remocra.db.jooq.remocra.tables.ModeleCourrier
import remocra.db.jooq.remocra.tables.ModeleCourrierParametre
import remocra.db.jooq.remocra.tables.ModelePibi
import remocra.db.jooq.remocra.tables.Module
import remocra.db.jooq.remocra.tables.Nature
import remocra.db.jooq.remocra.tables.NatureDeci
import remocra.db.jooq.remocra.tables.Niveau
import remocra.db.jooq.remocra.tables.Organisme
import remocra.db.jooq.remocra.tables.Parametre
import remocra.db.jooq.remocra.tables.Pei
import remocra.db.jooq.remocra.tables.Pena
import remocra.db.jooq.remocra.tables.PenaAspiration
import remocra.db.jooq.remocra.tables.Pibi
import remocra.db.jooq.remocra.tables.PoidsAnomalie
import remocra.db.jooq.remocra.tables.ProfilDroit
import remocra.db.jooq.remocra.tables.ProfilOrganisme
import remocra.db.jooq.remocra.tables.ProfilUtilisateur
import remocra.db.jooq.remocra.tables.Reservoir
import remocra.db.jooq.remocra.tables.Role
import remocra.db.jooq.remocra.tables.Site
import remocra.db.jooq.remocra.tables.Task
import remocra.db.jooq.remocra.tables.Tournee
import remocra.db.jooq.remocra.tables.TypeCanalisation
import remocra.db.jooq.remocra.tables.TypeOrganisme
import remocra.db.jooq.remocra.tables.TypePenaAspiration
import remocra.db.jooq.remocra.tables.TypeReseau
import remocra.db.jooq.remocra.tables.Utilisateur
import remocra.db.jooq.remocra.tables.Visite
import remocra.db.jooq.remocra.tables.VisiteCtrlDebitPression
import remocra.db.jooq.remocra.tables.Voie
import remocra.db.jooq.remocra.tables.ZoneIntegration
import javax.annotation.processing.Generated
import kotlin.collections.List

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
open class Remocra : SchemaImpl("remocra", DefaultCatalog.DEFAULT_CATALOG) {
    public companion object {

        /**
         * The reference instance of <code>remocra</code>
         */
        val REMOCRA: Remocra = Remocra()
    }

    /**
     * The table <code>remocra.anomalie</code>.
     */
    val ANOMALIE: Anomalie get() = Anomalie.ANOMALIE

    /**
     * The table <code>remocra.anomalie_categorie</code>.
     */
    val ANOMALIE_CATEGORIE: AnomalieCategorie get() = AnomalieCategorie.ANOMALIE_CATEGORIE

    /**
     * The table <code>remocra.api</code>.
     */
    val API: Api get() = Api.API

    /**
     * The table <code>remocra.commune</code>.
     */
    val COMMUNE: Commune get() = Commune.COMMUNE

    /**
     * The table <code>remocra.contact</code>.
     */
    val CONTACT: Contact get() = Contact.CONTACT

    /**
     * The table <code>remocra.diametre</code>.
     */
    val DIAMETRE: Diametre get() = Diametre.DIAMETRE

    /**
     * The table <code>remocra.document</code>.
     */
    val DOCUMENT: Document get() = Document.DOCUMENT

    /**
     * The table <code>remocra.domaine</code>.
     */
    val DOMAINE: Domaine get() = Domaine.DOMAINE

    /**
     * The table <code>remocra.gestionnaire</code>.
     */
    val GESTIONNAIRE: Gestionnaire get() = Gestionnaire.GESTIONNAIRE

    /**
     * The table <code>remocra.job</code>.
     */
    val JOB: Job get() = Job.JOB

    /**
     * The table <code>remocra.l_commune_cis</code>.
     */
    val L_COMMUNE_CIS: LCommuneCis get() = LCommuneCis.L_COMMUNE_CIS

    /**
     * The table <code>remocra.l_contact_gestionnaire</code>.
     */
    val L_CONTACT_GESTIONNAIRE: LContactGestionnaire get() = LContactGestionnaire.L_CONTACT_GESTIONNAIRE

    /**
     * The table <code>remocra.l_contact_organisme</code>.
     */
    val L_CONTACT_ORGANISME: LContactOrganisme get() = LContactOrganisme.L_CONTACT_ORGANISME

    /**
     * The table <code>remocra.l_contact_role</code>.
     */
    val L_CONTACT_ROLE: LContactRole get() = LContactRole.L_CONTACT_ROLE

    /**
     * The table <code>remocra.l_diametre_nature</code>.
     */
    val L_DIAMETRE_NATURE: LDiametreNature get() = LDiametreNature.L_DIAMETRE_NATURE

    /**
     * The table <code>remocra.l_pei_anomalie</code>.
     */
    val L_PEI_ANOMALIE: LPeiAnomalie get() = LPeiAnomalie.L_PEI_ANOMALIE

    /**
     * The table <code>remocra.l_pei_document</code>.
     */
    val L_PEI_DOCUMENT: LPeiDocument get() = LPeiDocument.L_PEI_DOCUMENT

    /**
     * The table <code>remocra.l_profil_utilisateur_organisme_droit</code>.
     */
    val L_PROFIL_UTILISATEUR_ORGANISME_DROIT: LProfilUtilisateurOrganismeDroit get() = LProfilUtilisateurOrganismeDroit.L_PROFIL_UTILISATEUR_ORGANISME_DROIT

    /**
     * The table <code>remocra.l_tournee_pei</code>.
     */
    val L_TOURNEE_PEI: LTourneePei get() = LTourneePei.L_TOURNEE_PEI

    /**
     * The table <code>remocra.l_visite_anomalie</code>.
     */
    val L_VISITE_ANOMALIE: LVisiteAnomalie get() = LVisiteAnomalie.L_VISITE_ANOMALIE

    /**
     * The table <code>remocra.lieu_dit</code>.
     */
    val LIEU_DIT: LieuDit get() = LieuDit.LIEU_DIT

    /**
     * The table <code>remocra.log_line</code>.
     */
    val LOG_LINE: LogLine get() = LogLine.LOG_LINE

    /**
     * The table <code>remocra.marque_pibi</code>.
     */
    val MARQUE_PIBI: MarquePibi get() = MarquePibi.MARQUE_PIBI

    /**
     * The table <code>remocra.materiau</code>.
     */
    val MATERIAU: Materiau get() = Materiau.MATERIAU

    /**
     * The table <code>remocra.modele_courrier</code>.
     */
    val MODELE_COURRIER: ModeleCourrier get() = ModeleCourrier.MODELE_COURRIER

    /**
     * The table <code>remocra.modele_courrier_parametre</code>.
     */
    val MODELE_COURRIER_PARAMETRE: ModeleCourrierParametre get() = ModeleCourrierParametre.MODELE_COURRIER_PARAMETRE

    /**
     * The table <code>remocra.modele_pibi</code>.
     */
    val MODELE_PIBI: ModelePibi get() = ModelePibi.MODELE_PIBI

    /**
     * The table <code>remocra.module</code>.
     */
    val MODULE: Module get() = Module.MODULE

    /**
     * The table <code>remocra.nature</code>.
     */
    val NATURE: Nature get() = Nature.NATURE

    /**
     * The table <code>remocra.nature_deci</code>.
     */
    val NATURE_DECI: NatureDeci get() = NatureDeci.NATURE_DECI

    /**
     * The table <code>remocra.niveau</code>.
     */
    val NIVEAU: Niveau get() = Niveau.NIVEAU

    /**
     * The table <code>remocra.organisme</code>.
     */
    val ORGANISME: Organisme get() = Organisme.ORGANISME

    /**
     * The table <code>remocra.parametre</code>.
     */
    val PARAMETRE: Parametre get() = Parametre.PARAMETRE

    /**
     * The table <code>remocra.pei</code>.
     */
    val PEI: Pei get() = Pei.PEI

    /**
     * The table <code>remocra.pena</code>.
     */
    val PENA: Pena get() = Pena.PENA

    /**
     * The table <code>remocra.pena_aspiration</code>.
     */
    val PENA_ASPIRATION: PenaAspiration get() = PenaAspiration.PENA_ASPIRATION

    /**
     * The table <code>remocra.pibi</code>.
     */
    val PIBI: Pibi get() = Pibi.PIBI

    /**
     * The table <code>remocra.poids_anomalie</code>.
     */
    val POIDS_ANOMALIE: PoidsAnomalie get() = PoidsAnomalie.POIDS_ANOMALIE

    /**
     * The table <code>remocra.profil_droit</code>.
     */
    val PROFIL_DROIT: ProfilDroit get() = ProfilDroit.PROFIL_DROIT

    /**
     * The table <code>remocra.profil_organisme</code>.
     */
    val PROFIL_ORGANISME: ProfilOrganisme get() = ProfilOrganisme.PROFIL_ORGANISME

    /**
     * The table <code>remocra.profil_utilisateur</code>.
     */
    val PROFIL_UTILISATEUR: ProfilUtilisateur get() = ProfilUtilisateur.PROFIL_UTILISATEUR

    /**
     * The table <code>remocra.reservoir</code>.
     */
    val RESERVOIR: Reservoir get() = Reservoir.RESERVOIR

    /**
     * The table <code>remocra.role</code>.
     */
    val ROLE: Role get() = Role.ROLE

    /**
     * The table <code>remocra.site</code>.
     */
    val SITE: Site get() = Site.SITE

    /**
     * The table <code>remocra.task</code>.
     */
    val TASK: Task get() = Task.TASK

    /**
     * The table <code>remocra.tournee</code>.
     */
    val TOURNEE: Tournee get() = Tournee.TOURNEE

    /**
     * The table <code>remocra.type_canalisation</code>.
     */
    val TYPE_CANALISATION: TypeCanalisation get() = TypeCanalisation.TYPE_CANALISATION

    /**
     * The table <code>remocra.type_organisme</code>.
     */
    val TYPE_ORGANISME: TypeOrganisme get() = TypeOrganisme.TYPE_ORGANISME

    /**
     * The table <code>remocra.type_pena_aspiration</code>.
     */
    val TYPE_PENA_ASPIRATION: TypePenaAspiration get() = TypePenaAspiration.TYPE_PENA_ASPIRATION

    /**
     * The table <code>remocra.type_reseau</code>.
     */
    val TYPE_RESEAU: TypeReseau get() = TypeReseau.TYPE_RESEAU

    /**
     * The table <code>remocra.utilisateur</code>.
     */
    val UTILISATEUR: Utilisateur get() = Utilisateur.UTILISATEUR

    /**
     * The table <code>remocra.visite</code>.
     */
    val VISITE: Visite get() = Visite.VISITE

    /**
     * The table <code>remocra.visite_ctrl_debit_pression</code>.
     */
    val VISITE_CTRL_DEBIT_PRESSION: VisiteCtrlDebitPression get() = VisiteCtrlDebitPression.VISITE_CTRL_DEBIT_PRESSION

    /**
     * The table <code>remocra.voie</code>.
     */
    val VOIE: Voie get() = Voie.VOIE

    /**
     * The table <code>remocra.zone_integration</code>.
     */
    val ZONE_INTEGRATION: ZoneIntegration get() = ZoneIntegration.ZONE_INTEGRATION

    override fun getCatalog(): Catalog = DefaultCatalog.DEFAULT_CATALOG

    override fun getTables(): List<Table<*>> = listOf(
        Anomalie.ANOMALIE,
        AnomalieCategorie.ANOMALIE_CATEGORIE,
        Api.API,
        Commune.COMMUNE,
        Contact.CONTACT,
        Diametre.DIAMETRE,
        Document.DOCUMENT,
        Domaine.DOMAINE,
        Gestionnaire.GESTIONNAIRE,
        Job.JOB,
        LCommuneCis.L_COMMUNE_CIS,
        LContactGestionnaire.L_CONTACT_GESTIONNAIRE,
        LContactOrganisme.L_CONTACT_ORGANISME,
        LContactRole.L_CONTACT_ROLE,
        LDiametreNature.L_DIAMETRE_NATURE,
        LPeiAnomalie.L_PEI_ANOMALIE,
        LPeiDocument.L_PEI_DOCUMENT,
        LProfilUtilisateurOrganismeDroit.L_PROFIL_UTILISATEUR_ORGANISME_DROIT,
        LTourneePei.L_TOURNEE_PEI,
        LVisiteAnomalie.L_VISITE_ANOMALIE,
        LieuDit.LIEU_DIT,
        LogLine.LOG_LINE,
        MarquePibi.MARQUE_PIBI,
        Materiau.MATERIAU,
        ModeleCourrier.MODELE_COURRIER,
        ModeleCourrierParametre.MODELE_COURRIER_PARAMETRE,
        ModelePibi.MODELE_PIBI,
        Module.MODULE,
        Nature.NATURE,
        NatureDeci.NATURE_DECI,
        Niveau.NIVEAU,
        Organisme.ORGANISME,
        Parametre.PARAMETRE,
        Pei.PEI,
        Pena.PENA,
        PenaAspiration.PENA_ASPIRATION,
        Pibi.PIBI,
        PoidsAnomalie.POIDS_ANOMALIE,
        ProfilDroit.PROFIL_DROIT,
        ProfilOrganisme.PROFIL_ORGANISME,
        ProfilUtilisateur.PROFIL_UTILISATEUR,
        Reservoir.RESERVOIR,
        Role.ROLE,
        Site.SITE,
        Task.TASK,
        Tournee.TOURNEE,
        TypeCanalisation.TYPE_CANALISATION,
        TypeOrganisme.TYPE_ORGANISME,
        TypePenaAspiration.TYPE_PENA_ASPIRATION,
        TypeReseau.TYPE_RESEAU,
        Utilisateur.UTILISATEUR,
        Visite.VISITE,
        VisiteCtrlDebitPression.VISITE_CTRL_DEBIT_PRESSION,
        Voie.VOIE,
        ZoneIntegration.ZONE_INTEGRATION,
    )
}
