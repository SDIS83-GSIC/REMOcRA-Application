/*
 * This file is generated by jOOQ.
 */
package remocra.db.jooq.remocra.tables.references

import remocra.db.jooq.remocra.tables.Anomalie
import remocra.db.jooq.remocra.tables.AnomalieCategorie
import remocra.db.jooq.remocra.tables.Api
import remocra.db.jooq.remocra.tables.CadastreParcelle
import remocra.db.jooq.remocra.tables.CadastreSection
import remocra.db.jooq.remocra.tables.Commune
import remocra.db.jooq.remocra.tables.Contact
import remocra.db.jooq.remocra.tables.Couche
import remocra.db.jooq.remocra.tables.Courrier
import remocra.db.jooq.remocra.tables.Dashboard
import remocra.db.jooq.remocra.tables.DashboardComponent
import remocra.db.jooq.remocra.tables.DashboardConfig
import remocra.db.jooq.remocra.tables.DashboardQuery
import remocra.db.jooq.remocra.tables.DebitSimultane
import remocra.db.jooq.remocra.tables.DebitSimultaneMesure
import remocra.db.jooq.remocra.tables.Diametre
import remocra.db.jooq.remocra.tables.Document
import remocra.db.jooq.remocra.tables.DocumentHabilitable
import remocra.db.jooq.remocra.tables.Domaine
import remocra.db.jooq.remocra.tables.FicheResumeBloc
import remocra.db.jooq.remocra.tables.FonctionContact
import remocra.db.jooq.remocra.tables.Gestionnaire
import remocra.db.jooq.remocra.tables.GroupeCouche
import remocra.db.jooq.remocra.tables.IndisponibiliteTemporaire
import remocra.db.jooq.remocra.tables.Job
import remocra.db.jooq.remocra.tables.LCommuneCis
import remocra.db.jooq.remocra.tables.LContactGestionnaire
import remocra.db.jooq.remocra.tables.LContactOrganisme
import remocra.db.jooq.remocra.tables.LContactRole
import remocra.db.jooq.remocra.tables.LCoucheDroit
import remocra.db.jooq.remocra.tables.LCoucheModule
import remocra.db.jooq.remocra.tables.LCourrierUtilisateur
import remocra.db.jooq.remocra.tables.LDashboardProfil
import remocra.db.jooq.remocra.tables.LDebitSimultaneMesurePei
import remocra.db.jooq.remocra.tables.LDiametreNature
import remocra.db.jooq.remocra.tables.LIndisponibiliteTemporairePei
import remocra.db.jooq.remocra.tables.LModeleCourrierProfilDroit
import remocra.db.jooq.remocra.tables.LPeiAnomalie
import remocra.db.jooq.remocra.tables.LPeiDocument
import remocra.db.jooq.remocra.tables.LProfilDroitDocumentHabilitable
import remocra.db.jooq.remocra.tables.LProfilUtilisateurOrganismeDroit
import remocra.db.jooq.remocra.tables.LRapportPersonnaliseProfilDroit
import remocra.db.jooq.remocra.tables.LThematiqueCourrier
import remocra.db.jooq.remocra.tables.LThematiqueDocumentHabilitable
import remocra.db.jooq.remocra.tables.LThematiqueModule
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
import remocra.db.jooq.remocra.tables.Oldeb
import remocra.db.jooq.remocra.tables.OldebCaracteristique
import remocra.db.jooq.remocra.tables.OldebLocataire
import remocra.db.jooq.remocra.tables.OldebProprietaire
import remocra.db.jooq.remocra.tables.OldebPropriete
import remocra.db.jooq.remocra.tables.OldebTypeAcces
import remocra.db.jooq.remocra.tables.OldebTypeAction
import remocra.db.jooq.remocra.tables.OldebTypeAnomalie
import remocra.db.jooq.remocra.tables.OldebTypeAvis
import remocra.db.jooq.remocra.tables.OldebTypeCaracteristique
import remocra.db.jooq.remocra.tables.OldebTypeCategorieAnomalie
import remocra.db.jooq.remocra.tables.OldebTypeCategorieCaracteristique
import remocra.db.jooq.remocra.tables.OldebTypeDebroussaillement
import remocra.db.jooq.remocra.tables.OldebTypeResidence
import remocra.db.jooq.remocra.tables.OldebTypeSuite
import remocra.db.jooq.remocra.tables.OldebTypeZoneUrbanisme
import remocra.db.jooq.remocra.tables.OldebVisite
import remocra.db.jooq.remocra.tables.OldebVisiteAnomalie
import remocra.db.jooq.remocra.tables.OldebVisiteDocument
import remocra.db.jooq.remocra.tables.OldebVisiteSuite
import remocra.db.jooq.remocra.tables.Organisme
import remocra.db.jooq.remocra.tables.Parametre
import remocra.db.jooq.remocra.tables.Pei
import remocra.db.jooq.remocra.tables.PeiPrescrit
import remocra.db.jooq.remocra.tables.Pena
import remocra.db.jooq.remocra.tables.PenaAspiration
import remocra.db.jooq.remocra.tables.Pibi
import remocra.db.jooq.remocra.tables.PoidsAnomalie
import remocra.db.jooq.remocra.tables.ProfilDroit
import remocra.db.jooq.remocra.tables.ProfilOrganisme
import remocra.db.jooq.remocra.tables.ProfilUtilisateur
import remocra.db.jooq.remocra.tables.RapportPersonnalise
import remocra.db.jooq.remocra.tables.RapportPersonnaliseParametre
import remocra.db.jooq.remocra.tables.Reservoir
import remocra.db.jooq.remocra.tables.RoleContact
import remocra.db.jooq.remocra.tables.Site
import remocra.db.jooq.remocra.tables.Task
import remocra.db.jooq.remocra.tables.Thematique
import remocra.db.jooq.remocra.tables.Tournee
import remocra.db.jooq.remocra.tables.TypeCanalisation
import remocra.db.jooq.remocra.tables.TypeOrganisme
import remocra.db.jooq.remocra.tables.TypePenaAspiration
import remocra.db.jooq.remocra.tables.TypeReseau
import remocra.db.jooq.remocra.tables.Utilisateur
import remocra.db.jooq.remocra.tables.VPeiVisiteDate
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
 * The table <code>remocra.cadastre_parcelle</code>.
 */
val CADASTRE_PARCELLE: CadastreParcelle = CadastreParcelle.CADASTRE_PARCELLE

/**
 * The table <code>remocra.cadastre_section</code>.
 */
val CADASTRE_SECTION: CadastreSection = CadastreSection.CADASTRE_SECTION

/**
 * The table <code>remocra.commune</code>.
 */
val COMMUNE: Commune = Commune.COMMUNE

/**
 * The table <code>remocra.contact</code>.
 */
val CONTACT: Contact = Contact.CONTACT

/**
 * The table <code>remocra.couche</code>.
 */
val COUCHE: Couche = Couche.COUCHE

/**
 * The table <code>remocra.courrier</code>.
 */
val COURRIER: Courrier = Courrier.COURRIER

/**
 * The table <code>remocra.dashboard</code>.
 */
val DASHBOARD: Dashboard = Dashboard.DASHBOARD

/**
 * The table <code>remocra.dashboard_component</code>.
 */
val DASHBOARD_COMPONENT: DashboardComponent = DashboardComponent.DASHBOARD_COMPONENT

/**
 * The table <code>remocra.dashboard_config</code>.
 */
val DASHBOARD_CONFIG: DashboardConfig = DashboardConfig.DASHBOARD_CONFIG

/**
 * The table <code>remocra.dashboard_query</code>.
 */
val DASHBOARD_QUERY: DashboardQuery = DashboardQuery.DASHBOARD_QUERY

/**
 * The table <code>remocra.debit_simultane</code>.
 */
val DEBIT_SIMULTANE: DebitSimultane = DebitSimultane.DEBIT_SIMULTANE

/**
 * The table <code>remocra.debit_simultane_mesure</code>.
 */
val DEBIT_SIMULTANE_MESURE: DebitSimultaneMesure = DebitSimultaneMesure.DEBIT_SIMULTANE_MESURE

/**
 * The table <code>remocra.diametre</code>.
 */
val DIAMETRE: Diametre = Diametre.DIAMETRE

/**
 * The table <code>remocra.document</code>.
 */
val DOCUMENT: Document = Document.DOCUMENT

/**
 * The table <code>remocra.document_habilitable</code>.
 */
val DOCUMENT_HABILITABLE: DocumentHabilitable = DocumentHabilitable.DOCUMENT_HABILITABLE

/**
 * The table <code>remocra.domaine</code>.
 */
val DOMAINE: Domaine = Domaine.DOMAINE

/**
 * The table <code>remocra.fiche_resume_bloc</code>.
 */
val FICHE_RESUME_BLOC: FicheResumeBloc = FicheResumeBloc.FICHE_RESUME_BLOC

/**
 * The table <code>remocra.fonction_contact</code>.
 */
val FONCTION_CONTACT: FonctionContact = FonctionContact.FONCTION_CONTACT

/**
 * The table <code>remocra.gestionnaire</code>.
 */
val GESTIONNAIRE: Gestionnaire = Gestionnaire.GESTIONNAIRE

/**
 * The table <code>remocra.groupe_couche</code>.
 */
val GROUPE_COUCHE: GroupeCouche = GroupeCouche.GROUPE_COUCHE

/**
 * The table <code>remocra.indisponibilite_temporaire</code>.
 */
val INDISPONIBILITE_TEMPORAIRE: IndisponibiliteTemporaire = IndisponibiliteTemporaire.INDISPONIBILITE_TEMPORAIRE

/**
 * The table <code>remocra.job</code>.
 */
val JOB: Job = Job.JOB

/**
 * The table <code>remocra.l_commune_cis</code>.
 */
val L_COMMUNE_CIS: LCommuneCis = LCommuneCis.L_COMMUNE_CIS

/**
 * The table <code>remocra.l_contact_gestionnaire</code>.
 */
val L_CONTACT_GESTIONNAIRE: LContactGestionnaire = LContactGestionnaire.L_CONTACT_GESTIONNAIRE

/**
 * The table <code>remocra.l_contact_organisme</code>.
 */
val L_CONTACT_ORGANISME: LContactOrganisme = LContactOrganisme.L_CONTACT_ORGANISME

/**
 * The table <code>remocra.l_contact_role</code>.
 */
val L_CONTACT_ROLE: LContactRole = LContactRole.L_CONTACT_ROLE

/**
 * The table <code>remocra.l_couche_droit</code>.
 */
val L_COUCHE_DROIT: LCoucheDroit = LCoucheDroit.L_COUCHE_DROIT

/**
 * The table <code>remocra.l_couche_module</code>.
 */
val L_COUCHE_MODULE: LCoucheModule = LCoucheModule.L_COUCHE_MODULE

/**
 * The table <code>remocra.l_courrier_utilisateur</code>.
 */
val L_COURRIER_UTILISATEUR: LCourrierUtilisateur = LCourrierUtilisateur.L_COURRIER_UTILISATEUR

/**
 * The table <code>remocra.l_dashboard_profil</code>.
 */
val L_DASHBOARD_PROFIL: LDashboardProfil = LDashboardProfil.L_DASHBOARD_PROFIL

/**
 * The table <code>remocra.l_debit_simultane_mesure_pei</code>.
 */
val L_DEBIT_SIMULTANE_MESURE_PEI: LDebitSimultaneMesurePei = LDebitSimultaneMesurePei.L_DEBIT_SIMULTANE_MESURE_PEI

/**
 * The table <code>remocra.l_diametre_nature</code>.
 */
val L_DIAMETRE_NATURE: LDiametreNature = LDiametreNature.L_DIAMETRE_NATURE

/**
 * The table <code>remocra.l_indisponibilite_temporaire_pei</code>.
 */
val L_INDISPONIBILITE_TEMPORAIRE_PEI: LIndisponibiliteTemporairePei = LIndisponibiliteTemporairePei.L_INDISPONIBILITE_TEMPORAIRE_PEI

/**
 * The table <code>remocra.l_modele_courrier_profil_droit</code>.
 */
val L_MODELE_COURRIER_PROFIL_DROIT: LModeleCourrierProfilDroit = LModeleCourrierProfilDroit.L_MODELE_COURRIER_PROFIL_DROIT

/**
 * The table <code>remocra.l_pei_anomalie</code>.
 */
val L_PEI_ANOMALIE: LPeiAnomalie = LPeiAnomalie.L_PEI_ANOMALIE

/**
 * The table <code>remocra.l_pei_document</code>.
 */
val L_PEI_DOCUMENT: LPeiDocument = LPeiDocument.L_PEI_DOCUMENT

/**
 * The table <code>remocra.l_profil_droit_document_habilitable</code>.
 */
val L_PROFIL_DROIT_DOCUMENT_HABILITABLE: LProfilDroitDocumentHabilitable = LProfilDroitDocumentHabilitable.L_PROFIL_DROIT_DOCUMENT_HABILITABLE

/**
 * The table <code>remocra.l_profil_utilisateur_organisme_droit</code>.
 */
val L_PROFIL_UTILISATEUR_ORGANISME_DROIT: LProfilUtilisateurOrganismeDroit = LProfilUtilisateurOrganismeDroit.L_PROFIL_UTILISATEUR_ORGANISME_DROIT

/**
 * The table <code>remocra.l_rapport_personnalise_profil_droit</code>.
 */
val L_RAPPORT_PERSONNALISE_PROFIL_DROIT: LRapportPersonnaliseProfilDroit = LRapportPersonnaliseProfilDroit.L_RAPPORT_PERSONNALISE_PROFIL_DROIT

/**
 * The table <code>remocra.l_thematique_courrier</code>.
 */
val L_THEMATIQUE_COURRIER: LThematiqueCourrier = LThematiqueCourrier.L_THEMATIQUE_COURRIER

/**
 * The table <code>remocra.l_thematique_document_habilitable</code>.
 */
val L_THEMATIQUE_DOCUMENT_HABILITABLE: LThematiqueDocumentHabilitable = LThematiqueDocumentHabilitable.L_THEMATIQUE_DOCUMENT_HABILITABLE

/**
 * The table <code>remocra.l_thematique_module</code>.
 */
val L_THEMATIQUE_MODULE: LThematiqueModule = LThematiqueModule.L_THEMATIQUE_MODULE

/**
 * The table <code>remocra.l_tournee_pei</code>.
 */
val L_TOURNEE_PEI: LTourneePei = LTourneePei.L_TOURNEE_PEI

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
 * The table <code>remocra.modele_courrier</code>.
 */
val MODELE_COURRIER: ModeleCourrier = ModeleCourrier.MODELE_COURRIER

/**
 * The table <code>remocra.modele_courrier_parametre</code>.
 */
val MODELE_COURRIER_PARAMETRE: ModeleCourrierParametre = ModeleCourrierParametre.MODELE_COURRIER_PARAMETRE

/**
 * The table <code>remocra.modele_pibi</code>.
 */
val MODELE_PIBI: ModelePibi = ModelePibi.MODELE_PIBI

/**
 * The table <code>remocra.module</code>.
 */
val MODULE: Module = Module.MODULE

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
 * The table <code>remocra.oldeb</code>.
 */
val OLDEB: Oldeb = Oldeb.OLDEB

/**
 * The table <code>remocra.oldeb_caracteristique</code>.
 */
val OLDEB_CARACTERISTIQUE: OldebCaracteristique = OldebCaracteristique.OLDEB_CARACTERISTIQUE

/**
 * The table <code>remocra.oldeb_locataire</code>.
 */
val OLDEB_LOCATAIRE: OldebLocataire = OldebLocataire.OLDEB_LOCATAIRE

/**
 * The table <code>remocra.oldeb_proprietaire</code>.
 */
val OLDEB_PROPRIETAIRE: OldebProprietaire = OldebProprietaire.OLDEB_PROPRIETAIRE

/**
 * The table <code>remocra.oldeb_propriete</code>.
 */
val OLDEB_PROPRIETE: OldebPropriete = OldebPropriete.OLDEB_PROPRIETE

/**
 * The table <code>remocra.oldeb_type_acces</code>.
 */
val OLDEB_TYPE_ACCES: OldebTypeAcces = OldebTypeAcces.OLDEB_TYPE_ACCES

/**
 * The table <code>remocra.oldeb_type_action</code>.
 */
val OLDEB_TYPE_ACTION: OldebTypeAction = OldebTypeAction.OLDEB_TYPE_ACTION

/**
 * The table <code>remocra.oldeb_type_anomalie</code>.
 */
val OLDEB_TYPE_ANOMALIE: OldebTypeAnomalie = OldebTypeAnomalie.OLDEB_TYPE_ANOMALIE

/**
 * The table <code>remocra.oldeb_type_avis</code>.
 */
val OLDEB_TYPE_AVIS: OldebTypeAvis = OldebTypeAvis.OLDEB_TYPE_AVIS

/**
 * The table <code>remocra.oldeb_type_caracteristique</code>.
 */
val OLDEB_TYPE_CARACTERISTIQUE: OldebTypeCaracteristique = OldebTypeCaracteristique.OLDEB_TYPE_CARACTERISTIQUE

/**
 * The table <code>remocra.oldeb_type_categorie_anomalie</code>.
 */
val OLDEB_TYPE_CATEGORIE_ANOMALIE: OldebTypeCategorieAnomalie = OldebTypeCategorieAnomalie.OLDEB_TYPE_CATEGORIE_ANOMALIE

/**
 * The table <code>remocra.oldeb_type_categorie_caracteristique</code>.
 */
val OLDEB_TYPE_CATEGORIE_CARACTERISTIQUE: OldebTypeCategorieCaracteristique = OldebTypeCategorieCaracteristique.OLDEB_TYPE_CATEGORIE_CARACTERISTIQUE

/**
 * The table <code>remocra.oldeb_type_debroussaillement</code>.
 */
val OLDEB_TYPE_DEBROUSSAILLEMENT: OldebTypeDebroussaillement = OldebTypeDebroussaillement.OLDEB_TYPE_DEBROUSSAILLEMENT

/**
 * The table <code>remocra.oldeb_type_residence</code>.
 */
val OLDEB_TYPE_RESIDENCE: OldebTypeResidence = OldebTypeResidence.OLDEB_TYPE_RESIDENCE

/**
 * The table <code>remocra.oldeb_type_suite</code>.
 */
val OLDEB_TYPE_SUITE: OldebTypeSuite = OldebTypeSuite.OLDEB_TYPE_SUITE

/**
 * The table <code>remocra.oldeb_type_zone_urbanisme</code>.
 */
val OLDEB_TYPE_ZONE_URBANISME: OldebTypeZoneUrbanisme = OldebTypeZoneUrbanisme.OLDEB_TYPE_ZONE_URBANISME

/**
 * The table <code>remocra.oldeb_visite</code>.
 */
val OLDEB_VISITE: OldebVisite = OldebVisite.OLDEB_VISITE

/**
 * The table <code>remocra.oldeb_visite_anomalie</code>.
 */
val OLDEB_VISITE_ANOMALIE: OldebVisiteAnomalie = OldebVisiteAnomalie.OLDEB_VISITE_ANOMALIE

/**
 * The table <code>remocra.oldeb_visite_document</code>.
 */
val OLDEB_VISITE_DOCUMENT: OldebVisiteDocument = OldebVisiteDocument.OLDEB_VISITE_DOCUMENT

/**
 * The table <code>remocra.oldeb_visite_suite</code>.
 */
val OLDEB_VISITE_SUITE: OldebVisiteSuite = OldebVisiteSuite.OLDEB_VISITE_SUITE

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
 * The table <code>remocra.pei_prescrit</code>.
 */
val PEI_PRESCRIT: PeiPrescrit = PeiPrescrit.PEI_PRESCRIT

/**
 * The table <code>remocra.pena</code>.
 */
val PENA: Pena = Pena.PENA

/**
 * The table <code>remocra.pena_aspiration</code>.
 */
val PENA_ASPIRATION: PenaAspiration = PenaAspiration.PENA_ASPIRATION

/**
 * The table <code>remocra.pibi</code>.
 */
val PIBI: Pibi = Pibi.PIBI

/**
 * The table <code>remocra.poids_anomalie</code>.
 */
val POIDS_ANOMALIE: PoidsAnomalie = PoidsAnomalie.POIDS_ANOMALIE

/**
 * The table <code>remocra.profil_droit</code>.
 */
val PROFIL_DROIT: ProfilDroit = ProfilDroit.PROFIL_DROIT

/**
 * The table <code>remocra.profil_organisme</code>.
 */
val PROFIL_ORGANISME: ProfilOrganisme = ProfilOrganisme.PROFIL_ORGANISME

/**
 * The table <code>remocra.profil_utilisateur</code>.
 */
val PROFIL_UTILISATEUR: ProfilUtilisateur = ProfilUtilisateur.PROFIL_UTILISATEUR

/**
 * The table <code>remocra.rapport_personnalise</code>.
 */
val RAPPORT_PERSONNALISE: RapportPersonnalise = RapportPersonnalise.RAPPORT_PERSONNALISE

/**
 * The table <code>remocra.rapport_personnalise_parametre</code>.
 */
val RAPPORT_PERSONNALISE_PARAMETRE: RapportPersonnaliseParametre = RapportPersonnaliseParametre.RAPPORT_PERSONNALISE_PARAMETRE

/**
 * The table <code>remocra.reservoir</code>.
 */
val RESERVOIR: Reservoir = Reservoir.RESERVOIR

/**
 * The table <code>remocra.role_contact</code>.
 */
val ROLE_CONTACT: RoleContact = RoleContact.ROLE_CONTACT

/**
 * The table <code>remocra.site</code>.
 */
val SITE: Site = Site.SITE

/**
 * The table <code>remocra.task</code>.
 */
val TASK: Task = Task.TASK

/**
 * The table <code>remocra.thematique</code>.
 */
val THEMATIQUE: Thematique = Thematique.THEMATIQUE

/**
 * The table <code>remocra.tournee</code>.
 */
val TOURNEE: Tournee = Tournee.TOURNEE

/**
 * The table <code>remocra.type_canalisation</code>.
 */
val TYPE_CANALISATION: TypeCanalisation = TypeCanalisation.TYPE_CANALISATION

/**
 * The table <code>remocra.type_organisme</code>.
 */
val TYPE_ORGANISME: TypeOrganisme = TypeOrganisme.TYPE_ORGANISME

/**
 * The table <code>remocra.type_pena_aspiration</code>.
 */
val TYPE_PENA_ASPIRATION: TypePenaAspiration = TypePenaAspiration.TYPE_PENA_ASPIRATION

/**
 * The table <code>remocra.type_reseau</code>.
 */
val TYPE_RESEAU: TypeReseau = TypeReseau.TYPE_RESEAU

/**
 * The table <code>remocra.utilisateur</code>.
 */
val UTILISATEUR: Utilisateur = Utilisateur.UTILISATEUR

/**
 * The table <code>remocra.v_pei_visite_date</code>.
 */
val V_PEI_VISITE_DATE: VPeiVisiteDate = VPeiVisiteDate.V_PEI_VISITE_DATE

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
