/*
 * This file is generated by jOOQ.
 */
package remocra.db.jooq.remocra.tables.references

import remocra.db.jooq.remocra.tables.Anomalie
import remocra.db.jooq.remocra.tables.AnomalieCategorie
import remocra.db.jooq.remocra.tables.Api
import remocra.db.jooq.remocra.tables.Commune
import remocra.db.jooq.remocra.tables.Diametre
import remocra.db.jooq.remocra.tables.Domaine
import remocra.db.jooq.remocra.tables.Gestionnaire
import remocra.db.jooq.remocra.tables.Job
import remocra.db.jooq.remocra.tables.LDiametreNature
import remocra.db.jooq.remocra.tables.LPeiAnomalie
import remocra.db.jooq.remocra.tables.LVisiteAnomalie
import remocra.db.jooq.remocra.tables.LieuDit
import remocra.db.jooq.remocra.tables.LogLine
import remocra.db.jooq.remocra.tables.MarquePibi
import remocra.db.jooq.remocra.tables.Materiau
import remocra.db.jooq.remocra.tables.ModelePibi
import remocra.db.jooq.remocra.tables.Nature
import remocra.db.jooq.remocra.tables.NatureDeci
import remocra.db.jooq.remocra.tables.Niveau
import remocra.db.jooq.remocra.tables.Organisme
import remocra.db.jooq.remocra.tables.Parametre
import remocra.db.jooq.remocra.tables.Pei
import remocra.db.jooq.remocra.tables.Pena
import remocra.db.jooq.remocra.tables.Pibi
import remocra.db.jooq.remocra.tables.PoidsAnomalie
import remocra.db.jooq.remocra.tables.ProfilOrganisme
import remocra.db.jooq.remocra.tables.Reservoir
import remocra.db.jooq.remocra.tables.Site
import remocra.db.jooq.remocra.tables.Task
import remocra.db.jooq.remocra.tables.TypeCanalisation
import remocra.db.jooq.remocra.tables.TypeOrganisme
import remocra.db.jooq.remocra.tables.TypeReseau
import remocra.db.jooq.remocra.tables.Utilisateur
import remocra.db.jooq.remocra.tables.Visite
import remocra.db.jooq.remocra.tables.VisiteCtrlDebitPression
import remocra.db.jooq.remocra.tables.Voie
import remocra.db.jooq.remocra.tables.ZoneIntegration

/**
 * The table <code>remocra.anomalie</code>.
 */
val ANOMALIE: Anomalie = Anomalie.ANOMALIE

/**
 * The table <code>remocra.anomalie_categorie</code>.
 */
val ANOMALIE_CATEGORIE: AnomalieCategorie = AnomalieCategorie.ANOMALIE_CATEGORIE

/**
 * The table <code>remocra.api</code>.
 */
val API: Api = Api.API

/**
 * The table <code>remocra.commune</code>.
 */
val COMMUNE: Commune = Commune.COMMUNE

/**
 * The table <code>remocra.diametre</code>.
 */
val DIAMETRE: Diametre = Diametre.DIAMETRE

/**
 * The table <code>remocra.domaine</code>.
 */
val DOMAINE: Domaine = Domaine.DOMAINE

/**
 * The table <code>remocra.gestionnaire</code>.
 */
val GESTIONNAIRE: Gestionnaire = Gestionnaire.GESTIONNAIRE

/**
 * The table <code>remocra.job</code>.
 */
val JOB: Job = Job.JOB

/**
 * The table <code>remocra.l_diametre_nature</code>.
 */
val L_DIAMETRE_NATURE: LDiametreNature = LDiametreNature.L_DIAMETRE_NATURE

/**
 * The table <code>remocra.l_pei_anomalie</code>.
 */
val L_PEI_ANOMALIE: LPeiAnomalie = LPeiAnomalie.L_PEI_ANOMALIE

/**
 * The table <code>remocra.l_visite_anomalie</code>.
 */
val L_VISITE_ANOMALIE: LVisiteAnomalie = LVisiteAnomalie.L_VISITE_ANOMALIE

/**
 * The table <code>remocra.lieu_dit</code>.
 */
val LIEU_DIT: LieuDit = LieuDit.LIEU_DIT

/**
 * The table <code>remocra.log_line</code>.
 */
val LOG_LINE: LogLine = LogLine.LOG_LINE

/**
 * The table <code>remocra.marque_pibi</code>.
 */
val MARQUE_PIBI: MarquePibi = MarquePibi.MARQUE_PIBI

/**
 * The table <code>remocra.materiau</code>.
 */
val MATERIAU: Materiau = Materiau.MATERIAU

/**
 * The table <code>remocra.modele_pibi</code>.
 */
val MODELE_PIBI: ModelePibi = ModelePibi.MODELE_PIBI

/**
 * The table <code>remocra.nature</code>.
 */
val NATURE: Nature = Nature.NATURE

/**
 * The table <code>remocra.nature_deci</code>.
 */
val NATURE_DECI: NatureDeci = NatureDeci.NATURE_DECI

/**
 * The table <code>remocra.niveau</code>.
 */
val NIVEAU: Niveau = Niveau.NIVEAU

/**
 * The table <code>remocra.organisme</code>.
 */
val ORGANISME: Organisme = Organisme.ORGANISME

/**
 * The table <code>remocra.parametre</code>.
 */
val PARAMETRE: Parametre = Parametre.PARAMETRE

/**
 * The table <code>remocra.pei</code>.
 */
val PEI: Pei = Pei.PEI

/**
 * The table <code>remocra.pena</code>.
 */
val PENA: Pena = Pena.PENA

/**
 * The table <code>remocra.pibi</code>.
 */
val PIBI: Pibi = Pibi.PIBI

/**
 * The table <code>remocra.poids_anomalie</code>.
 */
val POIDS_ANOMALIE: PoidsAnomalie = PoidsAnomalie.POIDS_ANOMALIE

/**
 * The table <code>remocra.profil_organisme</code>.
 */
val PROFIL_ORGANISME: ProfilOrganisme = ProfilOrganisme.PROFIL_ORGANISME

/**
 * The table <code>remocra.reservoir</code>.
 */
val RESERVOIR: Reservoir = Reservoir.RESERVOIR

/**
 * The table <code>remocra.site</code>.
 */
val SITE: Site = Site.SITE

/**
 * The table <code>remocra.task</code>.
 */
val TASK: Task = Task.TASK

/**
 * The table <code>remocra.type_canalisation</code>.
 */
val TYPE_CANALISATION: TypeCanalisation = TypeCanalisation.TYPE_CANALISATION

/**
 * The table <code>remocra.type_organisme</code>.
 */
val TYPE_ORGANISME: TypeOrganisme = TypeOrganisme.TYPE_ORGANISME

/**
 * The table <code>remocra.type_reseau</code>.
 */
val TYPE_RESEAU: TypeReseau = TypeReseau.TYPE_RESEAU

/**
 * The table <code>remocra.utilisateur</code>.
 */
val UTILISATEUR: Utilisateur = Utilisateur.UTILISATEUR

/**
 * The table <code>remocra.visite</code>.
 */
val VISITE: Visite = Visite.VISITE

/**
 * The table <code>remocra.visite_ctrl_debit_pression</code>.
 */
val VISITE_CTRL_DEBIT_PRESSION: VisiteCtrlDebitPression = VisiteCtrlDebitPression.VISITE_CTRL_DEBIT_PRESSION

/**
 * The table <code>remocra.voie</code>.
 */
val VOIE: Voie = Voie.VOIE

/**
 * The table <code>remocra.zone_integration</code>.
 */
val ZONE_INTEGRATION: ZoneIntegration = ZoneIntegration.ZONE_INTEGRATION
