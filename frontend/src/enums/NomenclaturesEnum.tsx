/**
 * Les types possibles de données en cache à demander au endpoint nomenclatures/list
 */
export enum NOMENCLATURE {
  ANOMALIE = "anomalie",
  DIAMETRE = "diametre",
  DOMAINE = "domaine",
  NATURE = "nature",
  NATURE_PIBI = "nature_pibi",
  NATURE_PENA = "nature_pena",
  NATURE_DECI = "nature_deci",
  MARQUE_PIBI = "marque_pibi",
  MATERIAU = "materiau",
  MODELE_PIBI = "modele_pibi",
  NIVEAU = "niveau",
  RESERVOIR = "reservoir",
  PROFIL_ORGANISME = "profil_organisme",
  PROFIL_UTILISATEUR = "profil_utilisateur",
  ROLE_CONTACT = "role_contact",
  THEMATIQUE = "thematique",
  TYPE_ETUDE = "type_etude",
  TYPE_CANALISATION = "type_canalisation",
  TYPE_ORGANISME = "type_organisme",
  TYPE_PENA_ASPIRATION = "type_pena_aspiration",
  TYPE_RESEAU = "type_reseau",

  OLDEB_TYPE_ACTION = "oldeb_type_action",
  OLDEB_TYPE_AVIS = "oldeb_type_avis",
  OLDEB_TYPE_DEBROUSSAILLEMENT = "oldeb_type_debroussaillement",
  OLDEB_TYPE_ANOMALIE = "oldeb_type_anomalie",
  OLDEB_TYPE_CATEGORIE_ANOMALIE = "oldeb_type_categorie_anomalie",
  OLDEB_TYPE_ACCES = "oldeb_type_acces",
  OLDEB_TYPE_RESIDENCE = "oldeb_type_residence",
  OLDEB_TYPE_SUITE = "oldeb_type_suite",
  OLDEB_TYPE_ZONE_URBANISME = "oldeb_type_zone_urbanisme",
  OLDEB_TYPE_CARACTERISTIQUE = "oldeb_type_caracteristique",
  OLDEB_TYPE_CATEGORIE_CARACTERISTIQUE = "oldeb_type_categorie_caracteristique",
}

export enum NOMENCLATURE_ORGANISME {
  SERVICE_PUBLIC_DECI = "servicePublicDeci",
  AUTORITE_DECI = "autoriteDeci",
}

export default NOMENCLATURE;
