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
}
export enum NOMENCLATURE_ORGANISME {
  SERVICE_PUBLIC_DECI = "servicePublicDeci",
  AUTORITE_DECI = "autoriteDeci",
}
export default NOMENCLATURE;
