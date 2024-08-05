/*
 * This file is generated by jOOQ.
 */
package remocra.db.jooq.remocra.keys

import org.jooq.ForeignKey
import org.jooq.Record
import org.jooq.UniqueKey
import org.jooq.impl.DSL
import org.jooq.impl.Internal
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
import remocra.db.jooq.remocra.tables.LContactGestionnaire
import remocra.db.jooq.remocra.tables.LContactOrganisme
import remocra.db.jooq.remocra.tables.LContactRole
import remocra.db.jooq.remocra.tables.LDiametreNature
import remocra.db.jooq.remocra.tables.LPeiAnomalie
import remocra.db.jooq.remocra.tables.LPeiDocument
import remocra.db.jooq.remocra.tables.LTourneePei
import remocra.db.jooq.remocra.tables.LVisiteAnomalie
import remocra.db.jooq.remocra.tables.LieuDit
import remocra.db.jooq.remocra.tables.LogLine
import remocra.db.jooq.remocra.tables.MarquePibi
import remocra.db.jooq.remocra.tables.Materiau
import remocra.db.jooq.remocra.tables.ModeleCourrier
import remocra.db.jooq.remocra.tables.ModeleCourrierParametre
import remocra.db.jooq.remocra.tables.ModelePibi
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

// -------------------------------------------------------------------------
// UNIQUE and PRIMARY KEY definitions
// -------------------------------------------------------------------------

val ANOMALIE_ANOMALIE_CODE_KEY: UniqueKey<Record> = Internal.createUniqueKey(Anomalie.ANOMALIE, DSL.name("anomalie_anomalie_code_key"), arrayOf(Anomalie.ANOMALIE.CODE), true)
val ANOMALIE_PKEY: UniqueKey<Record> = Internal.createUniqueKey(Anomalie.ANOMALIE, DSL.name("anomalie_pkey"), arrayOf(Anomalie.ANOMALIE.ID), true)
val ANOMALIE_CATEGORIE_ANOMALIE_CATEGORIE_CODE_KEY: UniqueKey<Record> = Internal.createUniqueKey(AnomalieCategorie.ANOMALIE_CATEGORIE, DSL.name("anomalie_categorie_anomalie_categorie_code_key"), arrayOf(AnomalieCategorie.ANOMALIE_CATEGORIE.CODE), true)
val ANOMALIE_CATEGORIE_PKEY: UniqueKey<Record> = Internal.createUniqueKey(AnomalieCategorie.ANOMALIE_CATEGORIE, DSL.name("anomalie_categorie_pkey"), arrayOf(AnomalieCategorie.ANOMALIE_CATEGORIE.ID), true)
val API_PKEY: UniqueKey<Record> = Internal.createUniqueKey(Api.API, DSL.name("api_pkey"), arrayOf(Api.API.ORGANISME_ID), true)
val COMMUNE_COMMUNE_CODE_INSEE_KEY: UniqueKey<Record> = Internal.createUniqueKey(Commune.COMMUNE, DSL.name("commune_commune_code_insee_key"), arrayOf(Commune.COMMUNE.CODE_INSEE), true)
val COMMUNE_PKEY: UniqueKey<Record> = Internal.createUniqueKey(Commune.COMMUNE, DSL.name("commune_pkey"), arrayOf(Commune.COMMUNE.ID), true)
val CONTACT_PKEY: UniqueKey<Record> = Internal.createUniqueKey(Contact.CONTACT, DSL.name("contact_pkey"), arrayOf(Contact.CONTACT.ID), true)
val DIAMETRE_DIAMETRE_CODE_KEY: UniqueKey<Record> = Internal.createUniqueKey(Diametre.DIAMETRE, DSL.name("diametre_diametre_code_key"), arrayOf(Diametre.DIAMETRE.CODE), true)
val DIAMETRE_PKEY: UniqueKey<Record> = Internal.createUniqueKey(Diametre.DIAMETRE, DSL.name("diametre_pkey"), arrayOf(Diametre.DIAMETRE.ID), true)
val DOCUMENT_PKEY: UniqueKey<Record> = Internal.createUniqueKey(Document.DOCUMENT, DSL.name("document_pkey"), arrayOf(Document.DOCUMENT.ID), true)
val DOMAINE_DOMAINE_CODE_KEY: UniqueKey<Record> = Internal.createUniqueKey(Domaine.DOMAINE, DSL.name("domaine_domaine_code_key"), arrayOf(Domaine.DOMAINE.CODE), true)
val DOMAINE_PKEY: UniqueKey<Record> = Internal.createUniqueKey(Domaine.DOMAINE, DSL.name("domaine_pkey"), arrayOf(Domaine.DOMAINE.ID), true)
val GESTIONNAIRE_GESTIONNAIRE_CODE_KEY: UniqueKey<Record> = Internal.createUniqueKey(Gestionnaire.GESTIONNAIRE, DSL.name("gestionnaire_gestionnaire_code_key"), arrayOf(Gestionnaire.GESTIONNAIRE.CODE), true)
val GESTIONNAIRE_PKEY: UniqueKey<Record> = Internal.createUniqueKey(Gestionnaire.GESTIONNAIRE, DSL.name("gestionnaire_pkey"), arrayOf(Gestionnaire.GESTIONNAIRE.ID), true)
val JOB_PKEY: UniqueKey<Record> = Internal.createUniqueKey(Job.JOB, DSL.name("job_pkey"), arrayOf(Job.JOB.ID), true)
val L_CONTACT_GESTIONNAIRE_CONTACT_ID_KEY: UniqueKey<Record> = Internal.createUniqueKey(LContactGestionnaire.L_CONTACT_GESTIONNAIRE, DSL.name("l_contact_gestionnaire_contact_id_key"), arrayOf(LContactGestionnaire.L_CONTACT_GESTIONNAIRE.CONTACT_ID), true)
val L_CONTACT_GESTIONNAIRE_PKEY: UniqueKey<Record> = Internal.createUniqueKey(LContactGestionnaire.L_CONTACT_GESTIONNAIRE, DSL.name("l_contact_gestionnaire_pkey"), arrayOf(LContactGestionnaire.L_CONTACT_GESTIONNAIRE.CONTACT_ID, LContactGestionnaire.L_CONTACT_GESTIONNAIRE.GESTIONNAIRE_ID), true)
val L_CONTACT_ORGANISME_CONTACT_ID_KEY: UniqueKey<Record> = Internal.createUniqueKey(LContactOrganisme.L_CONTACT_ORGANISME, DSL.name("l_contact_organisme_contact_id_key"), arrayOf(LContactOrganisme.L_CONTACT_ORGANISME.CONTACT_ID), true)
val L_CONTACT_ORGANISME_PKEY: UniqueKey<Record> = Internal.createUniqueKey(LContactOrganisme.L_CONTACT_ORGANISME, DSL.name("l_contact_organisme_pkey"), arrayOf(LContactOrganisme.L_CONTACT_ORGANISME.CONTACT_ID, LContactOrganisme.L_CONTACT_ORGANISME.ORGANISME_ID), true)
val L_CONTACT_ROLE_PKEY: UniqueKey<Record> = Internal.createUniqueKey(LContactRole.L_CONTACT_ROLE, DSL.name("l_contact_role_pkey"), arrayOf(LContactRole.L_CONTACT_ROLE.CONTACT_ID, LContactRole.L_CONTACT_ROLE.ROLE_ID), true)
val L_DIAMETRE_NATURE_PKEY: UniqueKey<Record> = Internal.createUniqueKey(LDiametreNature.L_DIAMETRE_NATURE, DSL.name("l_diametre_nature_pkey"), arrayOf(LDiametreNature.L_DIAMETRE_NATURE.DIAMETRE_ID, LDiametreNature.L_DIAMETRE_NATURE.NATURE_ID), true)
val L_PEI_ANOMALIE_PKEY: UniqueKey<Record> = Internal.createUniqueKey(LPeiAnomalie.L_PEI_ANOMALIE, DSL.name("l_pei_anomalie_pkey"), arrayOf(LPeiAnomalie.L_PEI_ANOMALIE.PEI_ID, LPeiAnomalie.L_PEI_ANOMALIE.ANOMALIE_ID), true)
val L_PEI_DOCUMENT_PKEY: UniqueKey<Record> = Internal.createUniqueKey(LPeiDocument.L_PEI_DOCUMENT, DSL.name("l_pei_document_pkey"), arrayOf(LPeiDocument.L_PEI_DOCUMENT.PEI_ID, LPeiDocument.L_PEI_DOCUMENT.DOCUMENT_ID), true)
val L_TOURNEE_PEI_PKEY: UniqueKey<Record> = Internal.createUniqueKey(LTourneePei.L_TOURNEE_PEI, DSL.name("l_tournee_pei_pkey"), arrayOf(LTourneePei.L_TOURNEE_PEI.TOURNEE_ID, LTourneePei.L_TOURNEE_PEI.PEI_ID), true)
val L_TOURNEE_PEI_TOURNEE_ID_L_TOURNEE_PEI_ORDRE_KEY: UniqueKey<Record> = Internal.createUniqueKey(LTourneePei.L_TOURNEE_PEI, DSL.name("l_tournee_pei_tournee_id_l_tournee_pei_ordre_key"), arrayOf(LTourneePei.L_TOURNEE_PEI.TOURNEE_ID, LTourneePei.L_TOURNEE_PEI.ORDRE), true)
val L_VISITE_ANOMALIE_PKEY: UniqueKey<Record> = Internal.createUniqueKey(LVisiteAnomalie.L_VISITE_ANOMALIE, DSL.name("l_visite_anomalie_pkey"), arrayOf(LVisiteAnomalie.L_VISITE_ANOMALIE.VISITE_ID, LVisiteAnomalie.L_VISITE_ANOMALIE.ANOMALIE_ID), true)
val LIEU_DIT_PKEY: UniqueKey<Record> = Internal.createUniqueKey(LieuDit.LIEU_DIT, DSL.name("lieu_dit_pkey"), arrayOf(LieuDit.LIEU_DIT.ID), true)
val LOG_LINE_PKEY: UniqueKey<Record> = Internal.createUniqueKey(LogLine.LOG_LINE, DSL.name("log_line_pkey"), arrayOf(LogLine.LOG_LINE.ID), true)
val MARQUE_PIBI_MARQUE_PIBI_CODE_KEY: UniqueKey<Record> = Internal.createUniqueKey(MarquePibi.MARQUE_PIBI, DSL.name("marque_pibi_marque_pibi_code_key"), arrayOf(MarquePibi.MARQUE_PIBI.CODE), true)
val MARQUE_PIBI_PKEY: UniqueKey<Record> = Internal.createUniqueKey(MarquePibi.MARQUE_PIBI, DSL.name("marque_pibi_pkey"), arrayOf(MarquePibi.MARQUE_PIBI.ID), true)
val MATERIAU_MATERIAU_CODE_KEY: UniqueKey<Record> = Internal.createUniqueKey(Materiau.MATERIAU, DSL.name("materiau_materiau_code_key"), arrayOf(Materiau.MATERIAU.CODE), true)
val MATERIAU_PKEY: UniqueKey<Record> = Internal.createUniqueKey(Materiau.MATERIAU, DSL.name("materiau_pkey"), arrayOf(Materiau.MATERIAU.ID), true)
val MODELE_COURRIER_MODELE_COURRIER_CODE_KEY: UniqueKey<Record> = Internal.createUniqueKey(ModeleCourrier.MODELE_COURRIER, DSL.name("modele_courrier_modele_courrier_code_key"), arrayOf(ModeleCourrier.MODELE_COURRIER.CODE), true)
val MODELE_COURRIER_PKEY: UniqueKey<Record> = Internal.createUniqueKey(ModeleCourrier.MODELE_COURRIER, DSL.name("modele_courrier_pkey"), arrayOf(ModeleCourrier.MODELE_COURRIER.ID), true)
val MODELE_COURRIER_PARAMETRE_PKEY: UniqueKey<Record> = Internal.createUniqueKey(ModeleCourrierParametre.MODELE_COURRIER_PARAMETRE, DSL.name("modele_courrier_parametre_pkey"), arrayOf(ModeleCourrierParametre.MODELE_COURRIER_PARAMETRE.MODELE_COURRIER_ID, ModeleCourrierParametre.MODELE_COURRIER_PARAMETRE.TYPE_PARAMETRE_COURRIER), true)
val MODELE_PIBI_MODELE_PIBI_CODE_KEY: UniqueKey<Record> = Internal.createUniqueKey(ModelePibi.MODELE_PIBI, DSL.name("modele_pibi_modele_pibi_code_key"), arrayOf(ModelePibi.MODELE_PIBI.CODE), true)
val MODELE_PIBI_PKEY: UniqueKey<Record> = Internal.createUniqueKey(ModelePibi.MODELE_PIBI, DSL.name("modele_pibi_pkey"), arrayOf(ModelePibi.MODELE_PIBI.ID), true)
val NATURE_NATURE_CODE_KEY: UniqueKey<Record> = Internal.createUniqueKey(Nature.NATURE, DSL.name("nature_nature_code_key"), arrayOf(Nature.NATURE.CODE), true)
val NATURE_PKEY: UniqueKey<Record> = Internal.createUniqueKey(Nature.NATURE, DSL.name("nature_pkey"), arrayOf(Nature.NATURE.ID), true)
val NATURE_DECI_PKEY: UniqueKey<Record> = Internal.createUniqueKey(NatureDeci.NATURE_DECI, DSL.name("nature_deci_pkey"), arrayOf(NatureDeci.NATURE_DECI.ID), true)
val NIVEAU_NIVEAU_CODE_KEY: UniqueKey<Record> = Internal.createUniqueKey(Niveau.NIVEAU, DSL.name("niveau_niveau_code_key"), arrayOf(Niveau.NIVEAU.CODE), true)
val NIVEAU_PKEY: UniqueKey<Record> = Internal.createUniqueKey(Niveau.NIVEAU, DSL.name("niveau_pkey"), arrayOf(Niveau.NIVEAU.ID), true)
val ORGANISME_ORGANISME_CODE_KEY: UniqueKey<Record> = Internal.createUniqueKey(Organisme.ORGANISME, DSL.name("organisme_organisme_code_key"), arrayOf(Organisme.ORGANISME.CODE), true)
val ORGANISME_PKEY: UniqueKey<Record> = Internal.createUniqueKey(Organisme.ORGANISME, DSL.name("organisme_pkey"), arrayOf(Organisme.ORGANISME.ID), true)
val PARAMETRE_PARAMETRE_CODE_KEY: UniqueKey<Record> = Internal.createUniqueKey(Parametre.PARAMETRE, DSL.name("parametre_parametre_code_key"), arrayOf(Parametre.PARAMETRE.CODE), true)
val PARAMETRE_PKEY: UniqueKey<Record> = Internal.createUniqueKey(Parametre.PARAMETRE, DSL.name("parametre_pkey"), arrayOf(Parametre.PARAMETRE.ID), true)
val PEI_PEI_NUMERO_COMPLET_KEY: UniqueKey<Record> = Internal.createUniqueKey(Pei.PEI, DSL.name("pei_pei_numero_complet_key"), arrayOf(Pei.PEI.NUMERO_COMPLET), true)
val PEI_PKEY: UniqueKey<Record> = Internal.createUniqueKey(Pei.PEI, DSL.name("pei_pkey"), arrayOf(Pei.PEI.ID), true)
val PENA_PKEY: UniqueKey<Record> = Internal.createUniqueKey(Pena.PENA, DSL.name("pena_pkey"), arrayOf(Pena.PENA.ID), true)
val PENA_ASPIRATION_PENA_ASPIRATION_NUMERO_KEY: UniqueKey<Record> = Internal.createUniqueKey(PenaAspiration.PENA_ASPIRATION, DSL.name("pena_aspiration_pena_aspiration_numero_key"), arrayOf(PenaAspiration.PENA_ASPIRATION.NUMERO), true)
val PENA_ASPIRATION_PKEY: UniqueKey<Record> = Internal.createUniqueKey(PenaAspiration.PENA_ASPIRATION, DSL.name("pena_aspiration_pkey"), arrayOf(PenaAspiration.PENA_ASPIRATION.ID), true)
val PIBI_PKEY: UniqueKey<Record> = Internal.createUniqueKey(Pibi.PIBI, DSL.name("pibi_pkey"), arrayOf(Pibi.PIBI.ID), true)
val POIDS_ANOMALIE_PKEY: UniqueKey<Record> = Internal.createUniqueKey(PoidsAnomalie.POIDS_ANOMALIE, DSL.name("poids_anomalie_pkey"), arrayOf(PoidsAnomalie.POIDS_ANOMALIE.ID), true)
val POIDS_ANOMALIE_POIDS_ANOMALIE_ANOMALIE_ID_POIDS_ANOMALIE_NA_KEY: UniqueKey<Record> = Internal.createUniqueKey(PoidsAnomalie.POIDS_ANOMALIE, DSL.name("poids_anomalie_poids_anomalie_anomalie_id_poids_anomalie_na_key"), arrayOf(PoidsAnomalie.POIDS_ANOMALIE.ANOMALIE_ID, PoidsAnomalie.POIDS_ANOMALIE.NATURE_ID), true)
val PROFIL_ORGANISME_PKEY: UniqueKey<Record> = Internal.createUniqueKey(ProfilOrganisme.PROFIL_ORGANISME, DSL.name("profil_organisme_pkey"), arrayOf(ProfilOrganisme.PROFIL_ORGANISME.ID), true)
val PROFIL_ORGANISME_PROFIL_ORGANISME_CODE_KEY: UniqueKey<Record> = Internal.createUniqueKey(ProfilOrganisme.PROFIL_ORGANISME, DSL.name("profil_organisme_profil_organisme_code_key"), arrayOf(ProfilOrganisme.PROFIL_ORGANISME.CODE), true)
val PROFIL_UTILISATEUR_PKEY: UniqueKey<Record> = Internal.createUniqueKey(ProfilUtilisateur.PROFIL_UTILISATEUR, DSL.name("profil_utilisateur_pkey"), arrayOf(ProfilUtilisateur.PROFIL_UTILISATEUR.ID), true)
val PROFIL_UTILISATEUR_PROFIL_UTILISATEUR_CODE_KEY: UniqueKey<Record> = Internal.createUniqueKey(ProfilUtilisateur.PROFIL_UTILISATEUR, DSL.name("profil_utilisateur_profil_utilisateur_code_key"), arrayOf(ProfilUtilisateur.PROFIL_UTILISATEUR.CODE), true)
val RESERVOIR_PKEY: UniqueKey<Record> = Internal.createUniqueKey(Reservoir.RESERVOIR, DSL.name("reservoir_pkey"), arrayOf(Reservoir.RESERVOIR.ID), true)
val RESERVOIR_RESERVOIR_CODE_KEY: UniqueKey<Record> = Internal.createUniqueKey(Reservoir.RESERVOIR, DSL.name("reservoir_reservoir_code_key"), arrayOf(Reservoir.RESERVOIR.CODE), true)
val ROLE_PKEY: UniqueKey<Record> = Internal.createUniqueKey(Role.ROLE, DSL.name("role_pkey"), arrayOf(Role.ROLE.ID), true)
val ROLE_ROLE_CODE_KEY: UniqueKey<Record> = Internal.createUniqueKey(Role.ROLE, DSL.name("role_role_code_key"), arrayOf(Role.ROLE.CODE), true)
val SITE_PKEY: UniqueKey<Record> = Internal.createUniqueKey(Site.SITE, DSL.name("site_pkey"), arrayOf(Site.SITE.ID), true)
val SITE_SITE_CODE_KEY: UniqueKey<Record> = Internal.createUniqueKey(Site.SITE, DSL.name("site_site_code_key"), arrayOf(Site.SITE.CODE), true)
val TASK_PKEY: UniqueKey<Record> = Internal.createUniqueKey(Task.TASK, DSL.name("task_pkey"), arrayOf(Task.TASK.ID), true)
val TASK_TASK_TYPE_KEY: UniqueKey<Record> = Internal.createUniqueKey(Task.TASK, DSL.name("task_task_type_key"), arrayOf(Task.TASK.TYPE), true)
val TOURNEE_PKEY: UniqueKey<Record> = Internal.createUniqueKey(Tournee.TOURNEE, DSL.name("tournee_pkey"), arrayOf(Tournee.TOURNEE.ID), true)
val TOURNEE_TOURNEE_ORGANISME_ID_TOURNEE_LIBELLE_KEY: UniqueKey<Record> = Internal.createUniqueKey(Tournee.TOURNEE, DSL.name("tournee_tournee_organisme_id_tournee_libelle_key"), arrayOf(Tournee.TOURNEE.ORGANISME_ID, Tournee.TOURNEE.LIBELLE), true)
val TYPE_CANALISATION_PKEY: UniqueKey<Record> = Internal.createUniqueKey(TypeCanalisation.TYPE_CANALISATION, DSL.name("type_canalisation_pkey"), arrayOf(TypeCanalisation.TYPE_CANALISATION.ID), true)
val TYPE_CANALISATION_TYPE_CANALISATION_CODE_KEY: UniqueKey<Record> = Internal.createUniqueKey(TypeCanalisation.TYPE_CANALISATION, DSL.name("type_canalisation_type_canalisation_code_key"), arrayOf(TypeCanalisation.TYPE_CANALISATION.CODE), true)
val TYPE_ORGANISME_PKEY: UniqueKey<Record> = Internal.createUniqueKey(TypeOrganisme.TYPE_ORGANISME, DSL.name("type_organisme_pkey"), arrayOf(TypeOrganisme.TYPE_ORGANISME.ID), true)
val TYPE_ORGANISME_TYPE_ORGANISME_CODE_KEY: UniqueKey<Record> = Internal.createUniqueKey(TypeOrganisme.TYPE_ORGANISME, DSL.name("type_organisme_type_organisme_code_key"), arrayOf(TypeOrganisme.TYPE_ORGANISME.CODE), true)
val TYPE_PENA_ASPIRATION_PKEY: UniqueKey<Record> = Internal.createUniqueKey(TypePenaAspiration.TYPE_PENA_ASPIRATION, DSL.name("type_pena_aspiration_pkey"), arrayOf(TypePenaAspiration.TYPE_PENA_ASPIRATION.ID), true)
val TYPE_PENA_ASPIRATION_TYPE_PENA_ASPIRATION_CODE_KEY: UniqueKey<Record> = Internal.createUniqueKey(TypePenaAspiration.TYPE_PENA_ASPIRATION, DSL.name("type_pena_aspiration_type_pena_aspiration_code_key"), arrayOf(TypePenaAspiration.TYPE_PENA_ASPIRATION.CODE), true)
val TYPE_RESEAU_PKEY: UniqueKey<Record> = Internal.createUniqueKey(TypeReseau.TYPE_RESEAU, DSL.name("type_reseau_pkey"), arrayOf(TypeReseau.TYPE_RESEAU.ID), true)
val TYPE_RESEAU_TYPE_RESEAU_CODE_KEY: UniqueKey<Record> = Internal.createUniqueKey(TypeReseau.TYPE_RESEAU, DSL.name("type_reseau_type_reseau_code_key"), arrayOf(TypeReseau.TYPE_RESEAU.CODE), true)
val UTILISATEUR_PKEY: UniqueKey<Record> = Internal.createUniqueKey(Utilisateur.UTILISATEUR, DSL.name("utilisateur_pkey"), arrayOf(Utilisateur.UTILISATEUR.ID), true)
val UTILISATEUR_UTILISATEUR_EMAIL_KEY: UniqueKey<Record> = Internal.createUniqueKey(Utilisateur.UTILISATEUR, DSL.name("utilisateur_utilisateur_email_key"), arrayOf(Utilisateur.UTILISATEUR.EMAIL), true)
val UTILISATEUR_UTILISATEUR_USERNAME_KEY: UniqueKey<Record> = Internal.createUniqueKey(Utilisateur.UTILISATEUR, DSL.name("utilisateur_utilisateur_username_key"), arrayOf(Utilisateur.UTILISATEUR.USERNAME), true)
val VISITE_PKEY: UniqueKey<Record> = Internal.createUniqueKey(Visite.VISITE, DSL.name("visite_pkey"), arrayOf(Visite.VISITE.ID), true)
val VISITE_CTRL_DEBIT_PRESSION_PKEY: UniqueKey<Record> = Internal.createUniqueKey(VisiteCtrlDebitPression.VISITE_CTRL_DEBIT_PRESSION, DSL.name("visite_ctrl_debit_pression_pkey"), arrayOf(VisiteCtrlDebitPression.VISITE_CTRL_DEBIT_PRESSION.VISITE_ID), true)
val VOIE_PKEY: UniqueKey<Record> = Internal.createUniqueKey(Voie.VOIE, DSL.name("voie_pkey"), arrayOf(Voie.VOIE.ID), true)
val ZONE_INTEGRATION_PKEY: UniqueKey<Record> = Internal.createUniqueKey(ZoneIntegration.ZONE_INTEGRATION, DSL.name("zone_integration_pkey"), arrayOf(ZoneIntegration.ZONE_INTEGRATION.ID), true)
val ZONE_INTEGRATION_ZONE_INTEGRATION_CODE_KEY: UniqueKey<Record> = Internal.createUniqueKey(ZoneIntegration.ZONE_INTEGRATION, DSL.name("zone_integration_zone_integration_code_key"), arrayOf(ZoneIntegration.ZONE_INTEGRATION.CODE), true)

// -------------------------------------------------------------------------
// FOREIGN KEY definitions
// -------------------------------------------------------------------------

val ANOMALIE__ANOMALIE_ANOMALIE_ANOMALIE_CATEGORIE_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(Anomalie.ANOMALIE, DSL.name("anomalie_anomalie_anomalie_categorie_id_fkey"), arrayOf(Anomalie.ANOMALIE.ANOMALIE_CATEGORIE_ID), remocra.db.jooq.remocra.keys.ANOMALIE_CATEGORIE_PKEY, arrayOf(AnomalieCategorie.ANOMALIE_CATEGORIE.ID), true)
val API__API_API_ORGANISME_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(Api.API, DSL.name("api_api_organisme_id_fkey"), arrayOf(Api.API.ORGANISME_ID), remocra.db.jooq.remocra.keys.ORGANISME_PKEY, arrayOf(Organisme.ORGANISME.ID), true)
val JOB__JOB_JOB_TASK_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(Job.JOB, DSL.name("job_job_task_id_fkey"), arrayOf(Job.JOB.TASK_ID), remocra.db.jooq.remocra.keys.TASK_PKEY, arrayOf(Task.TASK.ID), true)
val L_CONTACT_GESTIONNAIRE__L_CONTACT_GESTIONNAIRE_CONTACT_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(LContactGestionnaire.L_CONTACT_GESTIONNAIRE, DSL.name("l_contact_gestionnaire_contact_id_fkey"), arrayOf(LContactGestionnaire.L_CONTACT_GESTIONNAIRE.CONTACT_ID), remocra.db.jooq.remocra.keys.CONTACT_PKEY, arrayOf(Contact.CONTACT.ID), true)
val L_CONTACT_GESTIONNAIRE__L_CONTACT_GESTIONNAIRE_GESTIONNAIRE_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(LContactGestionnaire.L_CONTACT_GESTIONNAIRE, DSL.name("l_contact_gestionnaire_gestionnaire_id_fkey"), arrayOf(LContactGestionnaire.L_CONTACT_GESTIONNAIRE.GESTIONNAIRE_ID), remocra.db.jooq.remocra.keys.GESTIONNAIRE_PKEY, arrayOf(Gestionnaire.GESTIONNAIRE.ID), true)
val L_CONTACT_GESTIONNAIRE__L_CONTACT_GESTIONNAIRE_GESTIONNAIRE_SITE_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(LContactGestionnaire.L_CONTACT_GESTIONNAIRE, DSL.name("l_contact_gestionnaire_gestionnaire_site_id_fkey"), arrayOf(LContactGestionnaire.L_CONTACT_GESTIONNAIRE.GESTIONNAIRE_SITE_ID), remocra.db.jooq.remocra.keys.GESTIONNAIRE_PKEY, arrayOf(Gestionnaire.GESTIONNAIRE.ID), true)
val L_CONTACT_ORGANISME__L_CONTACT_ORGANISME_CONTACT_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(LContactOrganisme.L_CONTACT_ORGANISME, DSL.name("l_contact_organisme_contact_id_fkey"), arrayOf(LContactOrganisme.L_CONTACT_ORGANISME.CONTACT_ID), remocra.db.jooq.remocra.keys.CONTACT_PKEY, arrayOf(Contact.CONTACT.ID), true)
val L_CONTACT_ORGANISME__L_CONTACT_ORGANISME_ORGANISME_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(LContactOrganisme.L_CONTACT_ORGANISME, DSL.name("l_contact_organisme_organisme_id_fkey"), arrayOf(LContactOrganisme.L_CONTACT_ORGANISME.ORGANISME_ID), remocra.db.jooq.remocra.keys.ORGANISME_PKEY, arrayOf(Organisme.ORGANISME.ID), true)
val L_CONTACT_ROLE__L_CONTACT_ROLE_CONTACT_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(LContactRole.L_CONTACT_ROLE, DSL.name("l_contact_role_contact_id_fkey"), arrayOf(LContactRole.L_CONTACT_ROLE.CONTACT_ID), remocra.db.jooq.remocra.keys.CONTACT_PKEY, arrayOf(Contact.CONTACT.ID), true)
val L_CONTACT_ROLE__L_CONTACT_ROLE_ROLE_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(LContactRole.L_CONTACT_ROLE, DSL.name("l_contact_role_role_id_fkey"), arrayOf(LContactRole.L_CONTACT_ROLE.ROLE_ID), remocra.db.jooq.remocra.keys.ROLE_PKEY, arrayOf(Role.ROLE.ID), true)
val L_DIAMETRE_NATURE__L_DIAMETRE_NATURE_DIAMETRE_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(LDiametreNature.L_DIAMETRE_NATURE, DSL.name("l_diametre_nature_diametre_id_fkey"), arrayOf(LDiametreNature.L_DIAMETRE_NATURE.DIAMETRE_ID), remocra.db.jooq.remocra.keys.DIAMETRE_PKEY, arrayOf(Diametre.DIAMETRE.ID), true)
val L_DIAMETRE_NATURE__L_DIAMETRE_NATURE_NATURE_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(LDiametreNature.L_DIAMETRE_NATURE, DSL.name("l_diametre_nature_nature_id_fkey"), arrayOf(LDiametreNature.L_DIAMETRE_NATURE.NATURE_ID), remocra.db.jooq.remocra.keys.NATURE_PKEY, arrayOf(Nature.NATURE.ID), true)
val L_PEI_ANOMALIE__L_PEI_ANOMALIE_ANOMALIE_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(LPeiAnomalie.L_PEI_ANOMALIE, DSL.name("l_pei_anomalie_anomalie_id_fkey"), arrayOf(LPeiAnomalie.L_PEI_ANOMALIE.ANOMALIE_ID), remocra.db.jooq.remocra.keys.ANOMALIE_PKEY, arrayOf(Anomalie.ANOMALIE.ID), true)
val L_PEI_ANOMALIE__L_PEI_ANOMALIE_PEI_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(LPeiAnomalie.L_PEI_ANOMALIE, DSL.name("l_pei_anomalie_pei_id_fkey"), arrayOf(LPeiAnomalie.L_PEI_ANOMALIE.PEI_ID), remocra.db.jooq.remocra.keys.PEI_PKEY, arrayOf(Pei.PEI.ID), true)
val L_PEI_DOCUMENT__L_PEI_DOCUMENT_DOCUMENT_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(LPeiDocument.L_PEI_DOCUMENT, DSL.name("l_pei_document_document_id_fkey"), arrayOf(LPeiDocument.L_PEI_DOCUMENT.DOCUMENT_ID), remocra.db.jooq.remocra.keys.DOCUMENT_PKEY, arrayOf(Document.DOCUMENT.ID), true)
val L_PEI_DOCUMENT__L_PEI_DOCUMENT_PEI_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(LPeiDocument.L_PEI_DOCUMENT, DSL.name("l_pei_document_pei_id_fkey"), arrayOf(LPeiDocument.L_PEI_DOCUMENT.PEI_ID), remocra.db.jooq.remocra.keys.PEI_PKEY, arrayOf(Pei.PEI.ID), true)
val L_TOURNEE_PEI__L_TOURNEE_PEI_PEI_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(LTourneePei.L_TOURNEE_PEI, DSL.name("l_tournee_pei_pei_id_fkey"), arrayOf(LTourneePei.L_TOURNEE_PEI.PEI_ID), remocra.db.jooq.remocra.keys.PEI_PKEY, arrayOf(Pei.PEI.ID), true)
val L_TOURNEE_PEI__L_TOURNEE_PEI_TOURNEE_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(LTourneePei.L_TOURNEE_PEI, DSL.name("l_tournee_pei_tournee_id_fkey"), arrayOf(LTourneePei.L_TOURNEE_PEI.TOURNEE_ID), remocra.db.jooq.remocra.keys.TOURNEE_PKEY, arrayOf(Tournee.TOURNEE.ID), true)
val L_VISITE_ANOMALIE__L_VISITE_ANOMALIE_ANOMALIE_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(LVisiteAnomalie.L_VISITE_ANOMALIE, DSL.name("l_visite_anomalie_anomalie_id_fkey"), arrayOf(LVisiteAnomalie.L_VISITE_ANOMALIE.ANOMALIE_ID), remocra.db.jooq.remocra.keys.ANOMALIE_PKEY, arrayOf(Anomalie.ANOMALIE.ID), true)
val L_VISITE_ANOMALIE__L_VISITE_ANOMALIE_VISITE_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(LVisiteAnomalie.L_VISITE_ANOMALIE, DSL.name("l_visite_anomalie_visite_id_fkey"), arrayOf(LVisiteAnomalie.L_VISITE_ANOMALIE.VISITE_ID), remocra.db.jooq.remocra.keys.VISITE_PKEY, arrayOf(Visite.VISITE.ID), true)
val LIEU_DIT__LIEU_DIT_LIEU_DIT_COMMUNE_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(LieuDit.LIEU_DIT, DSL.name("lieu_dit_lieu_dit_commune_id_fkey"), arrayOf(LieuDit.LIEU_DIT.COMMUNE_ID), remocra.db.jooq.remocra.keys.COMMUNE_PKEY, arrayOf(Commune.COMMUNE.ID), true)
val LOG_LINE__LOG_LINE_LOG_LINE_JOB_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(LogLine.LOG_LINE, DSL.name("log_line_log_line_job_id_fkey"), arrayOf(LogLine.LOG_LINE.JOB_ID), remocra.db.jooq.remocra.keys.JOB_PKEY, arrayOf(Job.JOB.ID), true)
val MODELE_COURRIER_PARAMETRE__MODELE_COURRIER_PARAMETRE_MODELE_COURRIER_PARAMETRE_MODELE_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(ModeleCourrierParametre.MODELE_COURRIER_PARAMETRE, DSL.name("modele_courrier_parametre_modele_courrier_parametre_modele_fkey"), arrayOf(ModeleCourrierParametre.MODELE_COURRIER_PARAMETRE.MODELE_COURRIER_ID), remocra.db.jooq.remocra.keys.MODELE_COURRIER_PKEY, arrayOf(ModeleCourrier.MODELE_COURRIER.ID), true)
val MODELE_PIBI__MODELE_PIBI_MODELE_PIBI_MARQUE_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(ModelePibi.MODELE_PIBI, DSL.name("modele_pibi_modele_pibi_marque_id_fkey"), arrayOf(ModelePibi.MODELE_PIBI.MARQUE_ID), remocra.db.jooq.remocra.keys.MARQUE_PIBI_PKEY, arrayOf(MarquePibi.MARQUE_PIBI.ID), true)
val ORGANISME__ORGANISME_ORGANISME_PARENT_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(Organisme.ORGANISME, DSL.name("organisme_organisme_parent_id_fkey"), arrayOf(Organisme.ORGANISME.PARENT_ID), remocra.db.jooq.remocra.keys.ORGANISME_PKEY, arrayOf(Organisme.ORGANISME.ID), true)
val ORGANISME__ORGANISME_ORGANISME_PROFIL_ORGANISME_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(Organisme.ORGANISME, DSL.name("organisme_organisme_profil_organisme_id_fkey"), arrayOf(Organisme.ORGANISME.PROFIL_ORGANISME_ID), remocra.db.jooq.remocra.keys.PROFIL_ORGANISME_PKEY, arrayOf(ProfilOrganisme.PROFIL_ORGANISME.ID), true)
val ORGANISME__ORGANISME_ORGANISME_TYPE_ORGANISME_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(Organisme.ORGANISME, DSL.name("organisme_organisme_type_organisme_id_fkey"), arrayOf(Organisme.ORGANISME.TYPE_ORGANISME_ID), remocra.db.jooq.remocra.keys.TYPE_ORGANISME_PKEY, arrayOf(TypeOrganisme.TYPE_ORGANISME.ID), true)
val ORGANISME__ORGANISME_ORGANISME_ZONE_INTEGRATION_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(Organisme.ORGANISME, DSL.name("organisme_organisme_zone_integration_id_fkey"), arrayOf(Organisme.ORGANISME.ZONE_INTEGRATION_ID), remocra.db.jooq.remocra.keys.ZONE_INTEGRATION_PKEY, arrayOf(ZoneIntegration.ZONE_INTEGRATION.ID), true)
val PEI__PEI_PEI_AUTORITE_DECI_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(Pei.PEI, DSL.name("pei_pei_autorite_deci_id_fkey"), arrayOf(Pei.PEI.AUTORITE_DECI_ID), remocra.db.jooq.remocra.keys.ORGANISME_PKEY, arrayOf(Organisme.ORGANISME.ID), true)
val PEI__PEI_PEI_COMMUNE_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(Pei.PEI, DSL.name("pei_pei_commune_id_fkey"), arrayOf(Pei.PEI.COMMUNE_ID), remocra.db.jooq.remocra.keys.COMMUNE_PKEY, arrayOf(Commune.COMMUNE.ID), true)
val PEI__PEI_PEI_CROISEMENT_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(Pei.PEI, DSL.name("pei_pei_croisement_id_fkey"), arrayOf(Pei.PEI.CROISEMENT_ID), remocra.db.jooq.remocra.keys.VOIE_PKEY, arrayOf(Voie.VOIE.ID), true)
val PEI__PEI_PEI_DOMAINE_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(Pei.PEI, DSL.name("pei_pei_domaine_id_fkey"), arrayOf(Pei.PEI.DOMAINE_ID), remocra.db.jooq.remocra.keys.DOMAINE_PKEY, arrayOf(Domaine.DOMAINE.ID), true)
val PEI__PEI_PEI_GESTIONNAIRE_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(Pei.PEI, DSL.name("pei_pei_gestionnaire_id_fkey"), arrayOf(Pei.PEI.GESTIONNAIRE_ID), remocra.db.jooq.remocra.keys.GESTIONNAIRE_PKEY, arrayOf(Gestionnaire.GESTIONNAIRE.ID), true)
val PEI__PEI_PEI_LIEU_DIT_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(Pei.PEI, DSL.name("pei_pei_lieu_dit_id_fkey"), arrayOf(Pei.PEI.LIEU_DIT_ID), remocra.db.jooq.remocra.keys.LIEU_DIT_PKEY, arrayOf(LieuDit.LIEU_DIT.ID), true)
val PEI__PEI_PEI_MAINTENANCE_DECI_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(Pei.PEI, DSL.name("pei_pei_maintenance_deci_id_fkey"), arrayOf(Pei.PEI.MAINTENANCE_DECI_ID), remocra.db.jooq.remocra.keys.ORGANISME_PKEY, arrayOf(Organisme.ORGANISME.ID), true)
val PEI__PEI_PEI_NATURE_DECI_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(Pei.PEI, DSL.name("pei_pei_nature_deci_id_fkey"), arrayOf(Pei.PEI.NATURE_DECI_ID), remocra.db.jooq.remocra.keys.NATURE_DECI_PKEY, arrayOf(NatureDeci.NATURE_DECI.ID), true)
val PEI__PEI_PEI_NATURE_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(Pei.PEI, DSL.name("pei_pei_nature_id_fkey"), arrayOf(Pei.PEI.NATURE_ID), remocra.db.jooq.remocra.keys.NATURE_PKEY, arrayOf(Nature.NATURE.ID), true)
val PEI__PEI_PEI_NIVEAU_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(Pei.PEI, DSL.name("pei_pei_niveau_id_fkey"), arrayOf(Pei.PEI.NIVEAU_ID), remocra.db.jooq.remocra.keys.NIVEAU_PKEY, arrayOf(Niveau.NIVEAU.ID), true)
val PEI__PEI_PEI_SERVICE_PUBLIC_DECI_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(Pei.PEI, DSL.name("pei_pei_service_public_deci_id_fkey"), arrayOf(Pei.PEI.SERVICE_PUBLIC_DECI_ID), remocra.db.jooq.remocra.keys.ORGANISME_PKEY, arrayOf(Organisme.ORGANISME.ID), true)
val PEI__PEI_PEI_SITE_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(Pei.PEI, DSL.name("pei_pei_site_id_fkey"), arrayOf(Pei.PEI.SITE_ID), remocra.db.jooq.remocra.keys.SITE_PKEY, arrayOf(Site.SITE.ID), true)
val PEI__PEI_PEI_VOIE_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(Pei.PEI, DSL.name("pei_pei_voie_id_fkey"), arrayOf(Pei.PEI.VOIE_ID), remocra.db.jooq.remocra.keys.VOIE_PKEY, arrayOf(Voie.VOIE.ID), true)
val PEI__PEI_PEI_ZONE_SPECIALE_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(Pei.PEI, DSL.name("pei_pei_zone_speciale_id_fkey"), arrayOf(Pei.PEI.ZONE_SPECIALE_ID), remocra.db.jooq.remocra.keys.ZONE_INTEGRATION_PKEY, arrayOf(ZoneIntegration.ZONE_INTEGRATION.ID), true)
val PENA__PENA_PENA_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(Pena.PENA, DSL.name("pena_pena_id_fkey"), arrayOf(Pena.PENA.ID), remocra.db.jooq.remocra.keys.PEI_PKEY, arrayOf(Pei.PEI.ID), true)
val PENA__PENA_PENA_MATERIAU_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(Pena.PENA, DSL.name("pena_pena_materiau_id_fkey"), arrayOf(Pena.PENA.MATERIAU_ID), remocra.db.jooq.remocra.keys.MATERIAU_PKEY, arrayOf(Materiau.MATERIAU.ID), true)
val PENA_ASPIRATION__PENA_ASPIRATION_PENA_ASPIRATION_PENA_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(PenaAspiration.PENA_ASPIRATION, DSL.name("pena_aspiration_pena_aspiration_pena_id_fkey"), arrayOf(PenaAspiration.PENA_ASPIRATION.PENA_ID), remocra.db.jooq.remocra.keys.PENA_PKEY, arrayOf(Pena.PENA.ID), true)
val PENA_ASPIRATION__PENA_ASPIRATION_PENA_ASPIRATION_TYPE_PENA_ASPIRATION_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(PenaAspiration.PENA_ASPIRATION, DSL.name("pena_aspiration_pena_aspiration_type_pena_aspiration_id_fkey"), arrayOf(PenaAspiration.PENA_ASPIRATION.TYPE_PENA_ASPIRATION_ID), remocra.db.jooq.remocra.keys.TYPE_PENA_ASPIRATION_PKEY, arrayOf(TypePenaAspiration.TYPE_PENA_ASPIRATION.ID), true)
val PIBI__PIBI_PIBI_DIAMETRE_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(Pibi.PIBI, DSL.name("pibi_pibi_diametre_id_fkey"), arrayOf(Pibi.PIBI.DIAMETRE_ID), remocra.db.jooq.remocra.keys.DIAMETRE_PKEY, arrayOf(Diametre.DIAMETRE.ID), true)
val PIBI__PIBI_PIBI_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(Pibi.PIBI, DSL.name("pibi_pibi_id_fkey"), arrayOf(Pibi.PIBI.ID), remocra.db.jooq.remocra.keys.PEI_PKEY, arrayOf(Pei.PEI.ID), true)
val PIBI__PIBI_PIBI_JUMELE_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(Pibi.PIBI, DSL.name("pibi_pibi_jumele_id_fkey"), arrayOf(Pibi.PIBI.JUMELE_ID), remocra.db.jooq.remocra.keys.PIBI_PKEY, arrayOf(Pibi.PIBI.ID), true)
val PIBI__PIBI_PIBI_MARQUE_PIBI_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(Pibi.PIBI, DSL.name("pibi_pibi_marque_pibi_id_fkey"), arrayOf(Pibi.PIBI.MARQUE_PIBI_ID), remocra.db.jooq.remocra.keys.MARQUE_PIBI_PKEY, arrayOf(MarquePibi.MARQUE_PIBI.ID), true)
val PIBI__PIBI_PIBI_MODELE_PIBI_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(Pibi.PIBI, DSL.name("pibi_pibi_modele_pibi_id_fkey"), arrayOf(Pibi.PIBI.MODELE_PIBI_ID), remocra.db.jooq.remocra.keys.MODELE_PIBI_PKEY, arrayOf(ModelePibi.MODELE_PIBI.ID), true)
val PIBI__PIBI_PIBI_PENA_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(Pibi.PIBI, DSL.name("pibi_pibi_pena_id_fkey"), arrayOf(Pibi.PIBI.PENA_ID), remocra.db.jooq.remocra.keys.PENA_PKEY, arrayOf(Pena.PENA.ID), true)
val PIBI__PIBI_PIBI_RESERVOIR_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(Pibi.PIBI, DSL.name("pibi_pibi_reservoir_id_fkey"), arrayOf(Pibi.PIBI.RESERVOIR_ID), remocra.db.jooq.remocra.keys.RESERVOIR_PKEY, arrayOf(Reservoir.RESERVOIR.ID), true)
val PIBI__PIBI_PIBI_SERVICE_EAU_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(Pibi.PIBI, DSL.name("pibi_pibi_service_eau_id_fkey"), arrayOf(Pibi.PIBI.SERVICE_EAU_ID), remocra.db.jooq.remocra.keys.ORGANISME_PKEY, arrayOf(Organisme.ORGANISME.ID), true)
val PIBI__PIBI_PIBI_TYPE_CANALISATION_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(Pibi.PIBI, DSL.name("pibi_pibi_type_canalisation_id_fkey"), arrayOf(Pibi.PIBI.TYPE_CANALISATION_ID), remocra.db.jooq.remocra.keys.TYPE_CANALISATION_PKEY, arrayOf(TypeCanalisation.TYPE_CANALISATION.ID), true)
val PIBI__PIBI_PIBI_TYPE_RESEAU_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(Pibi.PIBI, DSL.name("pibi_pibi_type_reseau_id_fkey"), arrayOf(Pibi.PIBI.TYPE_RESEAU_ID), remocra.db.jooq.remocra.keys.TYPE_RESEAU_PKEY, arrayOf(TypeReseau.TYPE_RESEAU.ID), true)
val POIDS_ANOMALIE__POIDS_ANOMALIE_POIDS_ANOMALIE_ANOMALIE_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(PoidsAnomalie.POIDS_ANOMALIE, DSL.name("poids_anomalie_poids_anomalie_anomalie_id_fkey"), arrayOf(PoidsAnomalie.POIDS_ANOMALIE.ANOMALIE_ID), remocra.db.jooq.remocra.keys.ANOMALIE_PKEY, arrayOf(Anomalie.ANOMALIE.ID), true)
val POIDS_ANOMALIE__POIDS_ANOMALIE_POIDS_ANOMALIE_NATURE_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(PoidsAnomalie.POIDS_ANOMALIE, DSL.name("poids_anomalie_poids_anomalie_nature_id_fkey"), arrayOf(PoidsAnomalie.POIDS_ANOMALIE.NATURE_ID), remocra.db.jooq.remocra.keys.NATURE_PKEY, arrayOf(Nature.NATURE.ID), true)
val PROFIL_ORGANISME__PROFIL_ORGANISME_PROFIL_ORGANISME_TYPE_ORGANISME_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(ProfilOrganisme.PROFIL_ORGANISME, DSL.name("profil_organisme_profil_organisme_type_organisme_id_fkey"), arrayOf(ProfilOrganisme.PROFIL_ORGANISME.TYPE_ORGANISME_ID), remocra.db.jooq.remocra.keys.TYPE_ORGANISME_PKEY, arrayOf(TypeOrganisme.TYPE_ORGANISME.ID), true)
val PROFIL_UTILISATEUR__PROFIL_UTILISATEUR_PROFIL_UTILISATEUR_TYPE_ORGANISME_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(ProfilUtilisateur.PROFIL_UTILISATEUR, DSL.name("profil_utilisateur_profil_utilisateur_type_organisme_id_fkey"), arrayOf(ProfilUtilisateur.PROFIL_UTILISATEUR.TYPE_ORGANISME_ID), remocra.db.jooq.remocra.keys.TYPE_ORGANISME_PKEY, arrayOf(TypeOrganisme.TYPE_ORGANISME.ID), true)
val SITE__SITE_SITE_GESTIONNAIRE_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(Site.SITE, DSL.name("site_site_gestionnaire_id_fkey"), arrayOf(Site.SITE.GESTIONNAIRE_ID), remocra.db.jooq.remocra.keys.GESTIONNAIRE_PKEY, arrayOf(Gestionnaire.GESTIONNAIRE.ID), true)
val TOURNEE__TOURNEE_TOURNEE_ORGANISME_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(Tournee.TOURNEE, DSL.name("tournee_tournee_organisme_id_fkey"), arrayOf(Tournee.TOURNEE.ORGANISME_ID), remocra.db.jooq.remocra.keys.ORGANISME_PKEY, arrayOf(Organisme.ORGANISME.ID), true)
val TOURNEE__TOURNEE_TOURNEE_RESERVATION_UTILISATEUR_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(Tournee.TOURNEE, DSL.name("tournee_tournee_reservation_utilisateur_id_fkey"), arrayOf(Tournee.TOURNEE.RESERVATION_UTILISATEUR_ID), remocra.db.jooq.remocra.keys.UTILISATEUR_PKEY, arrayOf(Utilisateur.UTILISATEUR.ID), true)
val TYPE_ORGANISME__TYPE_ORGANISME_TYPE_ORGANISME_PARENT_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(TypeOrganisme.TYPE_ORGANISME, DSL.name("type_organisme_type_organisme_parent_id_fkey"), arrayOf(TypeOrganisme.TYPE_ORGANISME.PARENT_ID), remocra.db.jooq.remocra.keys.TYPE_ORGANISME_PKEY, arrayOf(TypeOrganisme.TYPE_ORGANISME.ID), true)
val UTILISATEUR__UTILISATEUR_UTILISATEUR_ORGANISME_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(Utilisateur.UTILISATEUR, DSL.name("utilisateur_utilisateur_organisme_id_fkey"), arrayOf(Utilisateur.UTILISATEUR.ORGANISME_ID), remocra.db.jooq.remocra.keys.ORGANISME_PKEY, arrayOf(Organisme.ORGANISME.ID), true)
val UTILISATEUR__UTILISATEUR_UTILISATEUR_PROFIL_UTILISATEUR_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(Utilisateur.UTILISATEUR, DSL.name("utilisateur_utilisateur_profil_utilisateur_id_fkey"), arrayOf(Utilisateur.UTILISATEUR.PROFIL_UTILISATEUR_ID), remocra.db.jooq.remocra.keys.PROFIL_UTILISATEUR_PKEY, arrayOf(ProfilUtilisateur.PROFIL_UTILISATEUR.ID), true)
val VISITE__VISITE_VISITE_PEI_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(Visite.VISITE, DSL.name("visite_visite_pei_id_fkey"), arrayOf(Visite.VISITE.PEI_ID), remocra.db.jooq.remocra.keys.PEI_PKEY, arrayOf(Pei.PEI.ID), true)
val VISITE_CTRL_DEBIT_PRESSION__VISITE_CTRL_DEBIT_PRESSION_VISITE_CTRL_DEBIT_PRESSION_VISI_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(VisiteCtrlDebitPression.VISITE_CTRL_DEBIT_PRESSION, DSL.name("visite_ctrl_debit_pression_visite_ctrl_debit_pression_visi_fkey"), arrayOf(VisiteCtrlDebitPression.VISITE_CTRL_DEBIT_PRESSION.VISITE_ID), remocra.db.jooq.remocra.keys.VISITE_PKEY, arrayOf(Visite.VISITE.ID), true)
val VOIE__VOIE_VOIE_COMMUNE_ID_FKEY: ForeignKey<Record, Record> = Internal.createForeignKey(Voie.VOIE, DSL.name("voie_voie_commune_id_fkey"), arrayOf(Voie.VOIE.COMMUNE_ID), remocra.db.jooq.remocra.keys.COMMUNE_PKEY, arrayOf(Commune.COMMUNE.ID), true)
