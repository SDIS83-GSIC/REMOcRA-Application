/**
 * Les types possibles de données en cache à demander au endpoint nomenclatures/list
 */
export enum TYPE_DATA_CACHE {
  ANOMALIE = "anomalie",
  DIAMETRE = "diametre",
  DOMAINE = "domaine",
  NATURE = "nature",
  NATURE_PIBI = "nature_pibi",
  NATURE_PENA = "nature_pena",
  NATURE_DECI = "nature_deci",
  NIVEAU = "niveau",
  MARQUE_PIBI = "marque_pibi",
  MATERIAU = "materiau",
  MODELE_PIBI = "modele_pibi",
  TYPE_CANALISATION = "type_canalisation",
  TYPE_RESEAU = "type_reseau",
}
export enum NOMENCLATURE_ORGANISME {
  SERVICE_PUBLIC_DECI = "servicePublicDeci",
  AUTORITE_DECI = "autoriteDeci",
}
export default TYPE_DATA_CACHE;
